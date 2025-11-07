package com.jthl.morewx

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.jthl.morewx.ui.theme.MoreWXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoreWXTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "插件化",
                        modifier = Modifier.padding(innerPadding),
                        this
                    )
                }
            }
        }
    }

}

private  fun initClick(context: Context, activity: MainActivity) {
    println("触发点击事件！")
    jumpToPluginActivity(context)

}

/**
 * 跳转到插件Activity
 */
private fun jumpToPluginActivity(context: Context) {
//    try {
//        // 1. 从Assets复制插件APK到内部存储（如果还没有）
//        val pluginApkPath = PluginLoader.copyPluginFromAssets(
//            context = context,
//            "plugin.apk"  // Assets中的插件APK文件名
//        )
//
//        // 2. 创建Intent，启动占位Activity
//        val intent = Intent(context, ProxyActivity::class.java).apply {
//            putExtra("plugin_apk_path", pluginApkPath)
//            putExtra("plugin_activity_name", "com.jthl.secondmodule.SecondActivity")
//        }
//
//        context.startActivity(intent)
//
//    } catch (e: Exception) {
//        android.util.Log.e("MainActivity", "跳转失败", e)
//        // 可以显示Toast提示用户
//    }

    val intent = Intent().apply {
        // !!! 目标是插件的真实类名 !!!
        component = ComponentName(
            "com.jthl.secondmodule",
            "com.jthl.secondmodule.SecondActivity" // 插件中未注册的 Activity
        )
    }
    context.startActivity(intent) // 触发系统启动流程
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, activity: MainActivity) {
    val current = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "你好 $name!",
            modifier = modifier.clickable {
                initClick(current, activity)
            },
            fontSize = 40.sp,
            color = Color.Black,
        )
    }


}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MoreWXTheme {
//        Greeting("Android")
    }
}