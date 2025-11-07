package com.jthl.common

import android.app.Activity
import android.content.Context
import android.os.Bundle

/**
 * 插件 Activity 继承的基类。注意：它不能继承 android.app.Activity
 * 因为它会被宿主和插件 ClassLoader 加载，继承 Activity 会导致复杂问题。
 * 在此方案中，我们让具体的插件 Activity (如 SecondActivity) 直接继承 android.app.Activity
 * 并在运行时注入 Context。
 */

open class PluginBaseActivity: IPluginActivity {

    // 注意：这里的 hostActivity 实际上是宿主的 StubActivity
    protected lateinit var hostActivity: Activity
    protected val pluginContext: Context get() = hostActivity

    override fun attach(hostActivity: Activity) {
        this.hostActivity = hostActivity

        // 【关键操作】：在更完善的插件化框架中，这里会通过反射，
        // 将 hostActivity (StubActivity) 的 Context 注入到 this (PluginActivity) 的 mBase 字段中，
        // 从而让 PluginActivity 能够像正常的 Activity 一样使用 Context。
    }

    // 实现接口方法，由子类重写
    override fun onCreate(savedInstanceState: Bundle?) {}
    override fun onStart() {}
    override fun onPause() {}
    override fun onDestroy() {}
}