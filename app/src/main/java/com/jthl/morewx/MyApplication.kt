package com.jthl.morewx

import android.app.Application
import android.content.Context
import com.jthl.morewx.utils.InstrumentationHooker
import com.jthl.morewx.utils.PluginManager
import com.wgllss.dynamic.host.lib.impl.WXDynamicLoader

/**
 * @author wanglei
 * @date 2025/11/7 10:14
 * @Description：
 */
class MyApplication : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // 1. 在这里加载插件 APK
        PluginManager.loadPlugin(base)

        // 2. Hook Instrumentation
        InstrumentationHooker.hook(base)
    }
    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext


    }
}