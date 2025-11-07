package com.jthl.morewx

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * @author wanglei
 * @date 2025/11/7 13:11
 * @Description：
 */
object PluginLoader {
    private var pluginClassLoader: DexClassLoader? = null
    private var pluginResources: Resources? = null
    private var pluginPackageName: String? = null

    /**
     * 加载插件APK
     * @param context 上下文
     * @param apkPath 插件APK文件路径
     */
    fun loadPlugin(context: Context, apkPath: String) {
        try {
            val apkFile = File(apkPath)
            if (!apkFile.exists()) {
                throw RuntimeException("插件APK文件不存在: $apkPath")
            }

            // 1. 创建DexClassLoader加载插件DEX
            val optimizedDirectory = context.getDir("plugin_opt", Context.MODE_PRIVATE).absolutePath
            val libraryPath = context.getDir("plugin_lib", Context.MODE_PRIVATE).absolutePath

            pluginClassLoader = DexClassLoader(
                apkPath,
                optimizedDirectory,
                libraryPath,
                context.classLoader
            )

            // 2. 加载插件Resources
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES)

            if (packageInfo == null) {
                throw RuntimeException("无法解析插件APK")
            }

            val applicationInfo = packageInfo.applicationInfo
            applicationInfo?.sourceDir = apkPath
            applicationInfo?.publicSourceDir = apkPath

            pluginResources = packageManager.getResourcesForApplication(applicationInfo!!)
            pluginPackageName = applicationInfo?.packageName

            android.util.Log.d("PluginLoader", "插件加载成功: $apkPath")

        } catch (e: Exception) {
            android.util.Log.e("PluginLoader", "加载插件失败", e)
            throw e
        }
    }

    /**
     * 从Assets复制插件APK到内部存储
     */
    fun copyPluginFromAssets(context: Context, assetName: String): String {
        val pluginDir = context.getDir("plugins", Context.MODE_PRIVATE)
        val pluginFile = File(pluginDir, assetName)

        if (pluginFile.exists()) {
            return pluginFile.absolutePath
        }

        // 从Assets复制
        val inputStream: InputStream = context.assets.open(assetName)
        val outputStream = FileOutputStream(pluginFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return pluginFile.absolutePath
    }

    /**
     * 加载插件类
     */
    fun loadClass(className: String): Class<*>? {
        return try {
            pluginClassLoader?.loadClass(className)
        } catch (e: Exception) {
            android.util.Log.e("PluginLoader", "加载类失败: $className", e)
            null
        }
    }

    /**
     * 获取插件Resources
     */
    fun getPluginResources(): Resources? {
        return pluginResources
    }

    /**
     * 获取插件包名
     */
    fun getPluginPackageName(): String? {
        return pluginPackageName
    }
}