package com.potato.exclude.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.potato.exclude.extentions.ExcludeExtension
import org.gradle.api.Project

class ExcludeTransform extends Transform {

    private Project project
    private static boolean openLog//是否开启日志打印
    private static ArrayList exclude//排除class文件

    ExcludeTransform(Project project) {
        this.project = project
        ExcludeExtension excludeExtension = project.excludeEx
        project.afterEvaluate {
            openLog = excludeExtension.openLog
            exclude = excludeExtension.exclude
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
     * 指明Transform的输入类型(这里返回CONTENT_RESOURCES，只处理资源文件)
     *
     * @return 输入类型
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_RESOURCES
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

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //获取输入文件（这里只处理了资源文件类型）
        transformInvocation.getInputs().each { input ->
            //排除资源文件
            input.directoryInputs.each { directoryInput ->
                def srcDir = directoryInput.file
                def destDir = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(srcDir, destDir)
                if (exclude.size() == 0) {
                    log("为匹配删除路径，不会删除资源文件")
                } else {
                    exclude.each { String dir ->
                        //"app/assets/flysafe/dji.nfzdb2.confumix","app/assets/flysafe/dji.nfzdb2.sig","app/assets/flysafe/flysafe_areas_djigo.db.confumix"
                        removeResourceFile(destDir, dir)
                    }
                }
            }
            //复制jar
            input.jarInputs.each { jarInput ->
                def destDir = transformInvocation.outputProvider.getContentLocation(jarInput.name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, destDir)
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