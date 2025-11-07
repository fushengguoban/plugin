package com.jthl.morewx.utils

/**
 * @author wanglei
 * @date 2025/11/7 16:30
 * @Description：
 */
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log

object ReflectionUtil {

    /**
     * 通过反射将目标 Activity (ContextWrapper) 的 mBase 字段替换为新的 Context。
     * 目标 Activity 此时是 SecondActivity 实例，新的 Context 是 StubActivity。
     */
    fun injectContext(targetActivity: Activity, newContext: Context) {
        try {
            // ContextWrapper 是 Activity 的父类
            val contextWrapperClass = ContextWrapper::class.java

            // ContextWrapper 中存储实际 Context 的字段是 mBase
            val mBaseField = contextWrapperClass.getDeclaredField("mBase")
            mBaseField.isAccessible = true

            // 写入新的 Context (即 StubActivity)
            mBaseField.set(targetActivity, newContext)

            Log.d("Reflection", "Context injected successfully into ${targetActivity.javaClass.simpleName}")

        } catch (e: Exception) {
            Log.e("Reflection", "Context 注入失败: ${e.message}", e)
            throw RuntimeException("Context 注入失败", e)
        }
    }

    /**
     * 将插件 Resources/AssetManager 注入到插件 Activity 实例中。
     * 目标 Activity 必须继承自 ContextThemeWrapper (即 Activity)。
     */
    fun injectResources(targetActivity: Activity, resources: Resources, assetManager: AssetManager) {
        try {
            // 1. 注入 Activity.mResources (Activity 自身持有 Resources 引用)
            val mResourcesField = Activity::class.java.getDeclaredField("mResources")
            mResourcesField.isAccessible = true
            mResourcesField.set(targetActivity, resources)

            // 2. 注入 ContextThemeWrapper.mResources 缓存 (防止 ContextThemeWrapper 重新创建 Resources)
            // ContextThemeWrapper 是 Activity 的父父类
            val contextThemeWrapperClass = Class.forName("android.view.ContextThemeWrapper")

            val contextThemeWrapperResourcesField = contextThemeWrapperClass.getDeclaredField("mResources")
            contextThemeWrapperResourcesField.isAccessible = true
            contextThemeWrapperResourcesField.set(targetActivity, resources)

            // 3. 注入 ContextThemeWrapper.mAssets（某些旧版本或厂商定制系统需要）
            try {
                val mAssetsField = contextThemeWrapperClass.getDeclaredField("mAssets")
                mAssetsField.isAccessible = true
                mAssetsField.set(targetActivity, assetManager)
            } catch (ignored: NoSuchFieldException) {
                // 某些版本没有 mAssets 字段，忽略
            }

            Log.d("Reflection", "Resources/AssetManager injected successfully.")

        } catch (e: Exception) {
            Log.e("Reflection", "Resources 注入失败: ${e.message}", e)
            throw RuntimeException("Resources 注入失败", e)
        }
    }
}