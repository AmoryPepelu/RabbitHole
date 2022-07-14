package github.amorypepelu.clicktrack.transform;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import github.amorypepelu.clicktrack.config.AsmConfig;
import github.amorypepelu.clicktrack.util.ClassModifierUtils;
import github.amorypepelu.clicktrack.util.GradleUtils;

/**
 * Created by sly on 2019-04-20.
 * 基本款，效率很低，主要是在文件复制上，不支持增量编译
 */
public abstract class DefaultTransform extends Transform {

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES 代表处理的 java 的 class 文件，RESOURCES 代表要处理 java 的资源
     *
     * @return
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    /**
     * 指 Transform 要操作内容的范围，官方文档 Scope 有 7 种类型：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     *
     * @return
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        // Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT)
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation tfi) throws TransformException, InterruptedException, IOException {
        //插件关闭
        if (!AsmConfig.ext.enable) {
            tfi.getOutputProvider().deleteAll();
            Stream<DirectoryInput> dirStream = tfi.getInputs()
                    .parallelStream()
                    .flatMap((Function<TransformInput, Stream<DirectoryInput>>) transformInput ->
                            transformInput.getDirectoryInputs().parallelStream())
                    .filter(directoryInput -> directoryInput.getFile().exists());

            Stream<JarInput> jarStream = tfi.getInputs()
                    .parallelStream()
                    .flatMap((Function<TransformInput, Stream<JarInput>>) transformInput ->
                            transformInput.getJarInputs().parallelStream())
                    .filter(directoryInput -> directoryInput.getFile().exists());

            Stream.concat(dirStream, jarStream).forEach(directoryInput -> {
                File dest = GradleUtils.getTransformOutputLocation(tfi.getOutputProvider(), directoryInput);
                if (directoryInput.getFile().isFile()) {
                    try {
                        FileUtils.copyFile(directoryInput.getFile(), dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        FileUtils.copyDirectory(directoryInput.getFile(), dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return;
        }

        //插件开启
        if (!tfi.isIncremental()) {
            tfi.getOutputProvider().deleteAll();
        }
        TransformOutputProvider outputProvider = tfi.getOutputProvider();

        /*Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历 */
        tfi.getInputs().forEach(transformInput -> {
            /*遍历目录*/
            transformInput.getDirectoryInputs().forEach(directoryInput -> {
                /*当前这个 Transform 输出目录*/
                File dest = GradleUtils.getTransformOutputLocation(outputProvider, directoryInput);
                File dir = directoryInput.getFile();
                if (dir != null && dir.exists()) {
                    Map<String, File> modifyMap = new HashMap<>();
                    traverse(dir, dir, modifyMap, tfi.getContext(), ".class");
                    try {
                        FileUtils.copyDirectory(directoryInput.getFile(), dest);//拷贝文件目录，连同内部文件一起拷贝
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    modifyMap.forEach((s, file) -> {
                        File target = new File(dest.getAbsoluteFile() + s);
                        if (target.exists()) {
                            target.delete();
                        }
                        try {
                            FileUtils.copyFile(file, target);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        file.delete();
                    });
                }
            });

            /*遍历 jar*/
            transformInput.getJarInputs().forEach(jarInput -> {
                String destName = jarInput.getFile().getName();

                /*截取文件路径的 md5 值重命名输出文件,因为可能同名,会覆盖*/
                String hexName = DigestUtils.md5Hex(jarInput.getFile().getAbsolutePath()).substring(0, 8);
                /* 获取 jar 名字*/
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4);
                }

                /* 获得输出文件*/
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                File modifiedJar = null;
                try {
                    modifiedJar = ClassModifierUtils.modifyJar(jarInput.getFile(), tfi.getContext().getTemporaryDir(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (modifiedJar == null) {
                    modifiedJar = jarInput.getFile();
                }
                try {
                    FileUtils.copyFile(modifiedJar, dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void traverse(File dir, File clzFile, Map<String, File> modifyMap, Context
            context, String nameFilter) {
        if (clzFile.isDirectory()) {
            File[] files = clzFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    traverse(dir, file, modifyMap, context, nameFilter);
                }
            }
        }

        if (clzFile.isFile() && clzFile.getName().endsWith(nameFilter)) {
            if (ClassModifierUtils.isShouldModify(clzFile.getName())) {
                File clzModified = ClassModifierUtils.modifyClassFile(dir, clzFile, context.getTemporaryDir());
                if (clzModified != null) {
                    /* key 为包名 + 类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class*/
                    String ke = clzFile.getAbsolutePath().replace(dir.getAbsolutePath(), "");
                    modifyMap.put(ke, clzModified);
                }
            }
        }
    }
}
