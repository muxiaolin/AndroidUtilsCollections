package io.github.muxiaolin.utils.demo

import android.app.Application

/**
 * Author: PL
Date: 2023/10/18
Desc:
 */
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        core.colin.basic.utils.Utils.init(this, true)
    }
}