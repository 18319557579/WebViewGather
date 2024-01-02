package com.example.webviewgather.interaction;

import android.webkit.JavascriptInterface;

import com.example.utilsgather.logcat.LogUtil;

public class JSCallAndroidObject extends Object{

    @JavascriptInterface
    public String executeHello(String parameters) {
        LogUtil.d("Android得到JS的传值: " + parameters);
        return "Hello JS.";
    }
}
