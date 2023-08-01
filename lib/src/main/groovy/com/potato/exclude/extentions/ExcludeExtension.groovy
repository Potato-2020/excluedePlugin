package com.potato.exclude.extentions

class ExcludeExtension {

    def openLog//是否开启日志打印
    ArrayList exclude//排除

    ExcludeExtension() {
        exclude = []//初始化一个ArrayList
    }

    def openLog(boolean openLog) {
        this.openLog = openLog
    }

    def excludeSingle(String dir) {
        if (!exclude.contains(dir)) {
            exclude << dir
        }
    }

    def exclude(String... dirs) {
        dirs.each { excludeSingle(it) }
    }
}