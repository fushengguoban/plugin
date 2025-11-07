package com.jthl.morewx.utils

import android.app.Activity
import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log

/**
 * @author wanglei
 * @date 2025/11/7 14:24
 * @Description：
 */
class CustomInstrumentation(
    private val context: Context,
    private val original: Instrumentation
) : Instrumentation() {

    companion object {
        private const val STUB_ACTIVITY_NAME = "com.jthl.morewx.utils.StubActivity"
        private const val PLUGIN_PACKAGE_PREFIX = "com.jthl.secondmodule"
    }

    // 关键 Hook 方法
    fun execStartActivity(
        who: Context, contextThread: IBinder, token: IBinder, target: Activity,
        intent: Intent, requestCode: Int, options: Bundle?
    ): ActivityResult? {

        val component = intent.component

        // 检查是否是插件 Activity
        if (component != null && component.className.startsWith(PLUGIN_PACKAGE_PREFIX)) {
            Log.d("Hook", "发现插件 Activity，目标: ${component.className}")

            // 1. 存储真实的插件 Activity 类名
            intent.putExtra("targetActivityName", component.className)

            // 2. 替换 Intent：指向预注册的占位 Activity
            val stubComponent = ComponentName(context.packageName, STUB_ACTIVITY_NAME)
            intent.component = stubComponent
        }

        // 3. 通过反射调用原始 Instrumentation 的 execStartActivity
        return try {
            val execStartActivityMethod = Instrumentation::class.java.getDeclaredMethod(
                "execStartActivity",
                Context::class.java, IBinder::class.java, IBinder::class.java, Activity::class.java,
                Intent::class.java, Int::class.java, Bundle::class.java
            )
            execStartActivityMethod.isAccessible = true
            execStartActivityMethod.invoke(
                original, who, contextThread, token, target, intent, requestCode, options
            ) as ActivityResult?
        } catch (e: Exception) {
            Log.e("Hook", "调用原始 execStartActivity 失败", e)
            null
        }
    }
}