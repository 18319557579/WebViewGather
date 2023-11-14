package com.example.webviewrapid.webview_settings;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebSettingsConfiguration {
    @SuppressLint("SetJavaScriptEnabled")
    public static void buildSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);

        //缓存使用默认
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        //不能访问文件
        webSettings.setAllowFileAccess(false);

        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //卧槽，设置为false后，图片都不会加载了。
        webSettings.setLoadsImagesAutomatically(true);

        //设置编码格式
        webSettings.setDefaultTextEncodingName("UTF-8");

        //允许让网站使用localStorage
        webSettings.setDomStorageEnabled(true);

        //允许让网站使用浏览器
        webSettings.setDatabaseEnabled(true);

        //不保存密码
        webSettings.setSavePassword(false);

        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

}
