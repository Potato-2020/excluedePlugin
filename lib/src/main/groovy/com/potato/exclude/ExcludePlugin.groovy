package com.potato.exclude

import com.potato.exclude.extentions.ExcludeExtension
import com.potato.exclude.transform.ExcludeTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

class ExcludePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //仅支持app工程
        if (project.plugins.hasPlugin("com.android.application")) {
            //创建混淆扩展块
            project.extensions.create("excludeEx", ExcludeExtension)
            //对AppExtension注册一个任务
            project.extensions.getByType(AppExtension).registerTransform(new ExcludeTransform(project))
        }
    }
}