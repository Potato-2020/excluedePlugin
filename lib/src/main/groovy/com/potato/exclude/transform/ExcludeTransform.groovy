package com.potato.exclude.transform


import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.potato.exclude.extentions.ExcludeExtension
import org.gradle.api.Project

import java.util.zip.ZipFile

class ExcludeTransform extends Transform {

    private Project project
    private static boolean openLog//是否开启日志打印
    private static ArrayList exclude//排除class文件
    private static String aarName//aar名字：dji-sdk-v5-aircraft-5.5.0

    ExcludeTransform(Project project) {
        this.project = project
        ExcludeExtension excludeExtension = project.excludeEx
        project.afterEvaluate {
            openLog = excludeExtension.openLog
            exclude = excludeExtension.exclude
            aarName = excludeExtension.aarName
            if (openLog) {
                log("准备删除资源文件目录，exclude: $exclude")
            }
        }
    }

    /**
     * 代表该Transform的task的名字
     *
     * @return name
     */
    @Override
    public String getName() {
        return "ExcludeTransform"
    }

    /**
     * 指明Transform的输入类型
     *
     * @return 输入类型
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
//        return TransformManager.CONTENT_RESOURCES
        return TransformManager.CONTENT_JARS
    }

    /**
     * 指明Transform的作用域（整个项目）
     *
     * @return 作用域
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
//        return ImmutableSet.of(QualifiedContent.Scope.PROJECT)
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否支持增量编译
     *
     * @return false：不支持
     */
    @Override
    public boolean isIncremental() {
        return false
    }

    private static void removeResourceFile(File directory, String relativePath) {
        File fileToRemove = new File(directory, relativePath)
        if (fileToRemove.exists()) {
            log("$relativePath 存在，正在删除")
            fileToRemove.delete()
        } else {
            log("$relativePath 不存在，不可以删除")
        }
    }

    private static void processDirectory(File directory) {
        exclude.forEach { fileToDelete ->
            File file = new File(directory, fileToDelete);
            if (file.exists()) {
                FileUtils.forceDelete(file)
                log("正在删除：$fileToDelete")
            } else {
                log("$fileToDelete 不存在")
            }
        }
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //获取输入文件（这里只处理了资源文件类型）
        log("====================================================================开始获取资源文件====================================================================")
        log("====================================================================开始获取资源文件====================================================================")
        log("====================================================================开始获取资源文件====================================================================")

        transformInvocation.getInputs().each { input ->
            // 处理 JAR 输入
            input.jarInputs.each { jarInput ->
                def jarFile = jarInput.file
                log("aar名字：${jarFile.toString()}")
                // 在这里检查 jarFile 是否包含预期的资源文件（AAR）
                if (jarFile.toString().endsWith(aarName)) {
                    log("找到了:$aarName")
                    // 解压 AAR 包
                    File aarDir = new File(jarFile.parent, "${jarInput.name}_unpacked")
                    FileUtils.deleteDirectory(aarDir)
                    ZipFile zipFile = new ZipFile(jarFile)
                    zipFile.extractAll(aarDir.toString())

                    // 查找资源文件目录
                    File resDir = new File(aarDir, "res")
                    // 删除指定的文件
                    if (exclude.size() == 0) {
                        log("没有配置资源目录，没办法删除资源文件")
                    } else {
                        exclude.each { String dir ->
                            File fileToDelete = new File(resDir, dir)
                            if (fileToDelete.exists()) {
                                FileUtils.forceDelete(fileToDelete)
                                log("$dir 已删除")
                            } else {
                                log("$dir 不存在，无需删除")
                            }
                        }
                    }
                } else {
                    log("没有找到:$aarName")
                }
            }
        }
    }

    /**
     * 打印日志
     */
    static def log(String msg) {
        if (openLog) {
            System.out.println(msg)
        }
    }

}