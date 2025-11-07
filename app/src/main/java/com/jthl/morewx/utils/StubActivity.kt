package com.jthl.morewx.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import com.jthl.common.IPluginActivity
/**
 * @author wanglei
 * @date 2025/11/7 14:26
 * @Description：
 */
class StubActivity: Activity() {
    private var pluginActivity: IPluginActivity? = null
    private var targetClassName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        targetClassName = intent.getStringExtra("targetActivityName")

        if (targetClassName == null) {
            finish()
            return
        }

        try {
            val pluginLoader = PluginManager.getPluginClassLoader()
            // 1. 实例化插件 Activity
            val targetClass = pluginLoader.loadClass(targetClassName)
            pluginActivity = targetClass.newInstance() as IPluginActivity

            // 2. 注入宿主 Context
            pluginActivity?.attach(this)
            val rawPluginActivity = pluginActivity as Activity
            ReflectionUtil.injectContext(rawPluginActivity, this)

            // 2. 🌟 关键：Resources 注入 🌟
            val pluginResources = PluginManager.getPluginResources()
            val pluginAssetManager = PluginManager.getPluginAssetManager()

            if (pluginResources != null && pluginAssetManager != null) {
                // 注入 Resources 和 AssetManager
                ReflectionUtil.injectResources(rawPluginActivity, pluginResources, pluginAssetManager)
            } else {
                Log.e("StubActivity", "插件 Resources 或 AssetManager 为空，无法注入!")
            }

            // 3. 转发 onCreate
            pluginActivity?.onCreate(savedInstanceState)

        } catch (e: Exception) {
            Log.e("StubActivity", "启动插件失败: $targetClassName", e)
            finish()
        }
    }

    // --- 5. 完整的生命周期转发 ---

    override fun onStart() {
        super.onStart()
        pluginActivity?.onStart()
    }

    override fun onResume() {
        super.onResume()
//        pluginActivity?.onResume()
    }

    override fun onPause() {
        super.onPause()
        pluginActivity?.onPause()
    }

    override fun onStop() {
        super.onStop()
//        pluginActivity?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        pluginActivity?.onDestroy()
    }

    // --- 6. 额外处理 (Manifest 属性转发) ---

    // 覆写 getResources/getClassLoader/getAssetManager
    // 在 Context 注入成功后，这些方法通常会通过反射字段工作。
    // 但为了更稳妥，可以再次覆写这些方法，直接返回插件的 Resources/ClassLoader。

    override fun getClassLoader(): ClassLoader {
        // 返回插件的 ClassLoader，确保插件内部能正确加载自己的类
        return PluginManager.getPluginClassLoader() ?: super.getClassLoader()
    }

    override fun getResources(): android.content.res.Resources {
        // 返回插件的 Resources，确保插件内部使用 this.getResources() 时获取到正确资源
        return PluginManager.getPluginResources() ?: super.getResources()
    }

    // 处理 ActivityInfo/Theme 的转发 (可选，但推荐)
    override fun getTheme(): android.content.res.Resources.Theme {
        // 如果插件有自定义主题，这里需要特殊处理
        return super.getTheme()
    }

    // 如果需要设置插件 Activity 的屏幕方向等属性，可以在这里查找插件 Manifest 中的配置
    override fun getRequestedOrientation(): Int {
        try {
            val activityInfo = packageManager.getActivityInfo(
                componentName,
                PackageManager.GET_META_DATA
            )
            // 假设我们把插件的真实配置放在 meta-data 里，或者直接使用 StubActivity 的配置
            return activityInfo.screenOrientation
        } catch (e: PackageManager.NameNotFoundException) {
            return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}