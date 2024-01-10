package com.example.webviewgather.packet.other

import android.os.Handler
import android.os.Looper

//用枚举类实现单例
enum class ImplementHandleUI {
    INSTANCE;

    /**
     * 切为UI线程执行
     */
    fun runOnUI(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }
}