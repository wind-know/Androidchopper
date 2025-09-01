package com.example.androidchopper

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.androidchopper.utils.importer.QuestionImporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

class MyApplication : Application() {
    private lateinit var prefs: SharedPreferences
    // 创建 Application 级别的 CoroutineScope（使用 IO 线程池，避免阻塞主线程）
    private val applicationScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        // 检查是否首次启动，首次启动时导入数据
        if (prefs.getBoolean("first_launch", true)) {
            // 使用 applicationScope 启动协程（替代 lifecycleScope）
            applicationScope.launch {

                try {
                    // 显式传递非空上下文
                    val success = QuestionImporter.importFromAssets(this@MyApplication)
                    withContext(Dispatchers.Main) {
                        prefs.edit().putBoolean("first_launch", !success).apply()
                        if (success) {
                            Toast.makeText(this@MyApplication, "导入成功", Toast.LENGTH_SHORT).show()
                            System.out.println("导入成功")
                        }else{
                            System.out.println("导入成功")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AppInit", "Data import failed", e)
                }
            }
        }
    }

    // 当 Application 销毁时，取消所有未完成的协程，避免内存泄漏
    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}