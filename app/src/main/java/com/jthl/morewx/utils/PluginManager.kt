package com.jthl.morewx.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import dalvik.system.DexClassLoader
import java.io.File

/**
 * @author wanglei
 * @date 2025/11/7 14:23
 * @Description：
 */
@SuppressLint("StaticFieldLeak")
object PluginManager {

    // 假设插件 APK 路径
    private const val PLUGIN_APK_PATH = "/sdcard/plugin-debug.apk"

    private lateinit var hostContext: Context
    private var pluginClassLoader: DexClassLoader? = null
    private var pluginResources: Resources? = null
    // 缓存插件的 AssetManager 和 Resources
    private var pluginAssetManager: AssetManager? = null


    fun loadPlugin(context: Context) {
        hostContext = context.applicationContext

        val pluginFile = File(PLUGIN_APK_PATH)
        if (!pluginFile.exists()) {
            Log.e("PluginManager", "插件 APK 文件不存在：$PLUGIN_APK_PATH")
            return
        }

        // 1. 创建优化 Dex 文件的目录
        val optimizedDirectory = context.getDir("dex", Context.MODE_PRIVATE)

        // 2. 创建 DexClassLoader 加载代码
        pluginClassLoader = DexClassLoader(
            PLUGIN_APK_PATH,
            optimizedDirectory.absolutePath,
            null, // Native Lib 路径
            context.classLoader // 宿主 ClassLoader 作为父类
        )

        // 3. 加载资源 (反射 AssetManager)
        try {
            // 使用 Kotlin 的 ::class.java 进行反射
            val assetManager = AssetManager::class.java.newInstance()

            val addAssetPathMethod = AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            addAssetPathMethod.isAccessible = true
            addAssetPathMethod.invoke(assetManager, PLUGIN_APK_PATH)
            this.pluginAssetManager = assetManager
            val superResources = hostContext.resources
            // 构造新的 Resources 实例
            pluginResources = Resources(
                assetManager,
                superResources.displayMetrics,
                superResources.configuration
            )
            Log.d("PluginManager", "插件加载成功!")
        } catch (e: Exception) {
            Log.e("PluginManager", "加载资源失败", e)
        }
    }

    fun getPluginClassLoader(): ClassLoader = pluginClassLoader ?: hostContext.classLoader

    fun getPluginResources(): Resources? = pluginResources
    fun getPluginAssetManager(): AssetManager? = pluginAssetManager
}