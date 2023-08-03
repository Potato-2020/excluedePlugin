package com.potato.exclude.extentions

class ExcludeExtension {

    def openLog//是否开启日志打印
    def aarName//aar包名称dji-sdk-v5-aircraft-5.5.0
    ArrayList exclude//排除

    ExcludeExtension() {
        exclude = []//初始化一个ArrayList
    }

    def openLog(boolean openLog) {
        this.openLog = openLog
    }

    def aarName(String aarName) {
        this.aarName = aarName
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