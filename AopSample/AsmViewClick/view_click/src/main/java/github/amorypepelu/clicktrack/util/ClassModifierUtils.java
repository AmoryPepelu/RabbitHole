package github.amorypepelu.clicktrack.util;

import com.android.tools.r8.org.objectweb.asm.ClassReader;
import com.android.tools.r8.org.objectweb.asm.ClassVisitor;
import com.android.tools.r8.org.objectweb.asm.ClassWriter;
import com.android.tools.r8.org.objectweb.asm.Opcodes;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import github.amorypepelu.clicktrack.classvisitor.DefaultClassVisitor;

/**
 * Created by sly on 2019-04-20.
 */
public class ClassModifierUtils {
    private static HashSet<String> exclude = new HashSet<>();

    static {
        //可能有多排除的，或者漏掉的
        exclude.add("android.support.");
        exclude.add("io.github.pepelu.track_util.");
        exclude.add("android.arch.");
        exclude.add("androidx.");
        exclude.add("kotlin.");
        exclude.add("org.intellij.");
        exclude.add("java.");
    }

    public static boolean isShouldModify(String className) {
        if (!className.endsWith(".class")) {
            return false;
        }

        for (String packageName : exclude) {
            if (className.startsWith(packageName)) {
                return false;
            }
        }

        if (className.contains("R$") ||
                className.contains("R2$") ||
                className.contains("R.class") ||
                className.contains("R2.class") ||
                className.contains("BR.class") ||
                className.contains("BuildConfig.class")) {
            return false;
        }

        return true;
    }

    public static File modifyClassFile(File dir, File clzFile, File tempDir) {
        File modified = null;
        try {
            String className = path2ClassName(clzFile.getAbsolutePath().replace(dir.getAbsolutePath() + File.separator, ""));
            byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(clzFile));
            byte[] modifiedClassBytes = modifyClass(sourceClassBytes);
            if (modifiedClassBytes != null) {
                modified = new File(tempDir, className.replace(".", "") + ".class");
                if (modified.exists()) {
                    modified.delete();
                }
                modified.createNewFile();
                new FileOutputStream(modified).write(modifiedClassBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            modified = clzFile;
        }
        return modified;
    }

    public static String path2ClassName(String pathName) {
        return pathName.replace(File.separator, ".").replace(".class", "");
    }

    public static byte[] modifyClass(byte[] srcClass) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new DefaultClassVisitor(Opcodes.ASM5, classWriter);
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    public static File modifyJar(File jarFile, File tempDir, boolean nameHex) throws IOException {
        /*
         * 读取原 jar
         */
        JarFile file = new JarFile(jarFile);

        /*
         * 设置输出到的 jar
         */
        String hexName = "";
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.getAbsolutePath()).substring(0, 8);
        }
        File outputJar = new File(tempDir, hexName + jarFile.getName());
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar));

        Enumeration enumeration = file.entries();

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            InputStream inputStream = file.getInputStream(jarEntry);

            String entryName = jarEntry.getName();
            String className;

            ZipEntry zipEntry = new ZipEntry(entryName);

            jarOutputStream.putNextEntry(zipEntry);

            byte[] modifiedClassBytes = null;
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
            if (entryName.endsWith(".class")) {

                LogUtil.i("modifyJar class name=" + entryName);

                className = entryName.replace("/", ".");
                if (isShouldModify(className)) {
                    modifiedClassBytes = modifyClass(sourceClassBytes);
                }
            }
            if (modifiedClassBytes == null) {
                modifiedClassBytes = sourceClassBytes;
            }
            jarOutputStream.write(modifiedClassBytes);
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
        file.close();
        return outputJar;
    }
}
