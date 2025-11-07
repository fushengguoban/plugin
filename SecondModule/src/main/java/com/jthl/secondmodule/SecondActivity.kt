package com.jthl.secondmodule

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.jthl.common.IPluginActivity
import com.jthl.common.PluginBaseActivity

/**
 * @author wanglei
 * @date 2025/11/5 8:36
 * @Description：
 */
class SecondActivity : Activity(), IPluginActivity by PluginBaseActivity() {
    // 保持对宿主 Activity 的引用 (StubActivity)
    private val pluginDelegate = PluginBaseActivity()

    private var myActivity: Activity? = null

    override fun attach(hostActivity: Activity) {
        this.myActivity = hostActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("Plugin", "Plugin Activity onCreate 调用")
        pluginDelegate.onCreate(savedInstanceState)

        // 使用 hostActivity 的 Context 来创建和设置视图
        val textView = TextView(this).apply {
            text = "插件化跳转成功了！！！！！"
            textSize = 24f
            setBackgroundColor(Color.YELLOW)
        }

        // 关键：使用 hostActivity 来设置内容视图
        myActivity?.setContentView(textView)
    }

    override fun onStart() {
        pluginDelegate.onStart()
    }

    override fun onPause() {

    }

    override fun onDestroy() {

        Log.d("Plugin", "Plugin Activity onDestroy 调用")
    }
}