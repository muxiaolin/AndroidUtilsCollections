# AndroidUtilsCollections
android utils工具类集合

一.引用， Add the dependency

dependencies {
    implementation 'io.github.muxiaolin:android-utils:${last_version}'
    implementation 'com.google.code.gson:gson:${last_version}'
}

二.在Application中，初始化

    class BaseApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            core.colin.basic.utils.Utils.init(this, true)
        }
    }

