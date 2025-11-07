package com.jthl.morewx

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import java.lang.reflect.Method

/**
 * @author wanglei
 * @date 2025/11/7 13:12
 * @Description：
 */
class ProxyActivity: Activity() {
    private var pluginActivity: Any? = null
    private var pluginResources: Resources? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // 1. 获取插件信息
            val pluginApkPath = intent.getStringExtra("plugin_apk_path")
            val pluginActivityName = intent.getStringExtra("plugin_activity_name")

            if (pluginApkPath == null || pluginActivityName == null) {
                throw RuntimeException("缺少插件信息")
            }

            // 2. 加载插件
            PluginLoader.loadPlugin(this, pluginApkPath)

            // 3. 加载插件Activity类
            val pluginActivityClass = PluginLoader.loadClass(pluginActivityName)
                ?: throw RuntimeException("无法加载插件Activity: $pluginActivityName")

            // 4. 创建插件Activity实例
            pluginActivity = pluginActivityClass.newInstance()

            // 5. 设置插件Activity的Context和Resources
            pluginResources = PluginLoader.getPluginResources()

            // 6. 调用插件Activity的onCreate方法
            val onCreateMethod = pluginActivityClass.getDeclaredMethod("onCreate", Bundle::class.java)
            onCreateMethod.isAccessible = true

            // 使用反射设置Context
            val attachBaseContextMethod = Activity::class.java.getDeclaredMethod(
                "attachBaseContext",
                android.content.Context::class.java
            )
            attachBaseContextMethod.isAccessible = true
            attachBaseContextMethod.invoke(pluginActivity, this)

            // 调用onCreate
            onCreateMethod.invoke(pluginActivity, savedInstanceState)

            Log.d("ProxyActivity", "插件Activity加载成功")

        } catch (e: Exception) {
            Log.e("ProxyActivity", "加载插件Activity失败", e)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        invokePluginMethod("onStart")
    }

    override fun onResume() {
        super.onResume()
        invokePluginMethod("onResume")
    }

    override fun onPause() {
        super.onPause()
        invokePluginMethod("onPause")
    }

    override fun onStop() {
        super.onStop()
        invokePluginMethod("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        invokePluginMethod("onDestroy")
    }

    /**
     * 调用插件Activity的方法
     */
    private fun invokePluginMethod(methodName: String) {
        try {
            pluginActivity?.let { activity ->
                val method = activity.javaClass.getDeclaredMethod(methodName)
                method.isAccessible = true
                method.invoke(activity)
            }
        } catch (e: Exception) {
            Log.e("ProxyActivity", "调用插件方法失败: $methodName", e)
        }
    }

    /**
     * 重写getResources，返回插件的Resources
     */
    override fun getResources(): Resources {
        return pluginResources ?: super.getResources()
    }

    fun findMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method? {
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            try {
                // 1. 先在当前类中查找声明的方法
                val method = currentClass.getDeclaredMethod(methodName, *parameterTypes)
                // 找到了，确保它可以被访问并返回
                method.isAccessible = true
                return method
            } catch (e: NoSuchMethodException) {
                // 2. 在当前类没找到，就去它的父类中继续找
                currentClass = currentClass.superclass
            }
        }
        // 遍历完所有父类都没找到，返回 null
        return null
    }
}