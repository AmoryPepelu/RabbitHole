package github.amorypepelu.clicktrack.transform;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import github.amorypepelu.clicktrack.config.AsmConfig;
import github.amorypepelu.clicktrack.util.ClassModifierUtils;
import github.amorypepelu.clicktrack.util.GradleUtils;
import github.amorypepelu.clicktrack.lambdasupport.IThrowingBiConsumer;
import github.amorypepelu.clicktrack.lambdasupport.IThrowingConsumer;
import github.amorypepelu.clicktrack.util.LogUtil;

/**
 * Created by sly on 2019-04-20.
 * 支持增量编译
 */
public abstract class IncrementalTransform extends Transform {

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

    /**
     * 支持增量编译
     *
     * @return
     */
    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation tfi) throws IOException {
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

            Stream.concat(dirStream, jarStream).forEach((IThrowingConsumer<QualifiedContent>) directoryInput -> {
                File dest = GradleUtils.getTransformOutputLocation(tfi.getOutputProvider(), directoryInput);
                if (directoryInput.getFile().isFile()) {
                    FileUtils.copyFile(directoryInput.getFile(), dest);
                } else {
                    FileUtils.copyDirectory(directoryInput.getFile(), dest);
                }
            });
            return;
        }

        //插件开启
        //是否增量编译
        if (!tfi.isIncremental()) {
            tfi.getOutputProvider().deleteAll();
        }
        TransformOutputProvider outputProvider = tfi.getOutputProvider();

        /*Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历 */
        tfi.getInputs().forEach(transformInput -> {
            /*遍历目录*/
            transformInput.getDirectoryInputs().forEach((IThrowingConsumer<DirectoryInput>) directoryInput -> {
                /*当前这个 Transform 输出目录*/
                File outputDir = GradleUtils.getTransformOutputLocation(outputProvider, directoryInput);
                File inputDir = directoryInput.getFile();
                List<File> changeFiles = new ArrayList<>();

                if (tfi.isIncremental()) {
                    directoryInput.getChangedFiles().forEach((IThrowingBiConsumer<File, Status>) (file, status) -> {
                        if (status == Status.CHANGED || status == Status.REMOVED) {
                            String clzNameFile = file.getAbsolutePath().replace(inputDir.getAbsolutePath(), "");
                            File cache = new File(outputDir.getAbsoluteFile() + clzNameFile);
                            LogUtil.i("transform cache file dir=" + cache.getPath());
                            if (cache.exists() && cache.isFile()) {
                                FileUtils.deleteQuietly(cache);
                            }
                        }
                        if (status == Status.CHANGED || status == Status.ADDED) {
                            changeFiles.add(file);
                        }
                    });
                } else {
                    List<File> files = Files.walk(inputDir.toPath())
                            .parallel()
                            .map(Path::toFile)
                            .filter(File::isFile)
                            .collect(Collectors.toList());
                    changeFiles.addAll(files);
                }

                changeFiles.parallelStream().filter(File::isFile).forEach((IThrowingConsumer<File>) file -> {
                    //除去根目录的 class 文件目录
                    String clzNameFile = file.getAbsolutePath().replace(inputDir.getAbsolutePath(), "");
                    File target = new File(outputDir.getAbsoluteFile() + clzNameFile);

//                                LogUtil.i("target file dir=" + target.getPath()
//                                        + ",parent=" + target.getParent()
//                                        + ",parent path=" + target.getParentFile().getPath());

                    if (!target.getParentFile().exists()) {
                        target.getParentFile().mkdirs();
                    } else if (target.exists()) {
                        target.delete();
                    }

                    if (ClassModifierUtils.isShouldModify(file.getName())) {
                        byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(file));
                        byte[] modifiedClassBytes = ClassModifierUtils.modifyClass(sourceClassBytes);
                        FileUtils.writeByteArrayToFile(target, modifiedClassBytes);
                    } else {
                        FileUtils.copyFile(file, target);
                    }
                });
            });

            /*遍历 jar*/
            transformInput.getJarInputs().forEach((IThrowingConsumer<JarInput>) jarInput -> {
                File outputJar = GradleUtils.getTransformOutputLocation(outputProvider, jarInput);

                //增加jar时，会把新增jar放置在编号最末尾，减少jar时，会触发全量编译，重新计算各jar依赖编号
                if (tfi.isIncremental()) {
                    if (jarInput.getStatus() == Status.NOTCHANGED) {
                        // LogUtil.i("transform jar isIncremental and input file no change path=" + jarInput.getFile().getPath());
                        return;
                    } else {
//                        LogUtil.i("transform jar isIncremental change input file path=" + jarInput.getFile().getPath()
//                                + ",output jar =" + outputJar.getPath());
                        FileUtils.deleteQuietly(outputJar);
                    }
                }

//                LogUtil.i("transform jar file path=" + outputJar.toPath());

                /*
                 * 读取原 jar
                 * 输出jar 是 数字 + .jar 和放在各个module libs 下面的jar 是一一对应的
                 */
                JarFile jarFile = new JarFile(jarInput.getFile());

                LogUtil.i("transform jar input file path=" + jarInput.getFile().getPath());

                Enumeration enumeration = jarFile.entries();
                if (!outputJar.getParentFile().exists()) {
                    outputJar.getParentFile().mkdirs();
                } else if (outputJar.exists()) {
                    outputJar.delete();
                }
                JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar));
                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) enumeration.nextElement();
                    InputStream inputStream = jarFile.getInputStream(jarEntry);

                    String entryName = jarEntry.getName();
                    String className;

                    ZipEntry zipEntry = new ZipEntry(entryName);

                    jarOutputStream.putNextEntry(zipEntry);

                    byte[] modifiedClassBytes = null;
                    byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
                    if (entryName.endsWith(".class")) {

                        //LogUtil.i("modifyJar class name=" + entryName);

                        className = entryName.replace("/", ".");
                        if (ClassModifierUtils.isShouldModify(className)) {
                            modifiedClassBytes = ClassModifierUtils.modifyClass(sourceClassBytes);
                        }
                    }
                    if (modifiedClassBytes == null) {
                        modifiedClassBytes = sourceClassBytes;
                    }
                    jarOutputStream.write(modifiedClassBytes);
                    jarOutputStream.closeEntry();
                }
                jarOutputStream.close();
                jarFile.close();
            });
        });
    }
}
