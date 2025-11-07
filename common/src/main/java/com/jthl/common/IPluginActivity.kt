package com.jthl.common

import android.app.Activity
import android.os.Bundle

/**
 * @author wanglei
 * @date 2025/11/7 15:25
 * @Description：
 */
interface IPluginActivity {
    fun attach(hostActivity: Activity)
    fun onCreate(savedInstanceState: Bundle?)
    fun onStart()
    fun onPause()
    fun onDestroy()
    // ... 添加所有需要转发的生命周期方法
}