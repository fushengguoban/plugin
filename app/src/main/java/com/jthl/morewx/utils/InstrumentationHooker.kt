package com.jthl.morewx.utils

import android.app.Instrumentation
import android.content.Context
import android.util.Log

/**
 * @author wanglei
 * @date 2025/11/7 14:24
 * @Description：
 */
object InstrumentationHooker {
    fun hook(context: Context) {
        try {
            // 1. 获取 ActivityThread 实例 (Kotlin 反射)
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
            currentActivityThreadMethod.isAccessible = true
            val activityThread = currentActivityThreadMethod.invoke(null)

            // 2. 获取 ActivityThread 中的 mInstrumentation 字段
            val mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation")
            mInstrumentationField.isAccessible = true
            val originalInstrumentation = mInstrumentationField.get(activityThread) as Instrumentation

            // 3. 替换为自定义的 CustomInstrumentation
            val customInstrumentation = CustomInstrumentation(context, originalInstrumentation)
            mInstrumentationField.set(activityThread, customInstrumentation)

            Log.d("Hooker", "Instrumentation Hook 成功!")
        } catch (e: Exception) {
            Log.e("Hooker", "Instrumentation Hook 失败", e)
        }
    }
}