package com.example.webviewrapid.webview_settings;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebSettingsConfiguration {
    @SuppressLint("SetJavaScriptEnabled")
    public static void buildSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        //可以说是必须要设置的，否则页面将几乎无法进行交互
        webSettings.setJavaScriptEnabled(true);

        //--设置自适应屏幕，两者合用
        //是否启用对viewport元标签的支持
        //发现使用设置为true，原生html显示的字体会比较小；设置为false，会比较大。其他的页面没看出区别
        webSettings.setUseWideViewPort(true);
        //内容宽度大于WebView控件的宽度时，按宽度缩小内容以适合屏幕
        webSettings.setLoadWithOverviewMode(true);

        //--设置是否支持手势缩放。是下面setBuiltInZoomControls的前提
        //（它这里说的是是否支持手势缩放，因此并不影响zoomIn(), zoomOut(), zoomBy()的调用效果）
        webSettings.setSupportZoom(true);
        //是否设置内置的缩放机制，如果为false，那么该WebView不可缩放。"内置机制是当前唯一支持的缩放机制，因此建议始终启用此设置"。
        //（因此，如果要进行缩放，必须要setSupportZoom和setBuiltInZoomControls同时为true）
        //（目前发现哪怕是两者同时为true，很多页面还是不能缩放的，能缩放的有www.google.com, 原生html）
        webSettings.setBuiltInZoomControls(true);
        //是否显示原生的缩放控件，就是那个可以点击+—号来进行缩放。"屏幕上的缩放控件在Android中被弃用了，所以建议将其设置为false"。
        webSettings.setDisplayZoomControls(false);
//
//        //缓存使用默认
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//
        //设置是否能访问文件（对file:///android_asset和file:// android_res不影响）
        //todo 访问文件这一块还需要实践
        webSettings.setAllowFileAccess(false);
//
//        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//
//        //卧槽，设置为false后，图片都不会加载了。
        webSettings.setLoadsImagesAutomatically(true);
//
//        //设置编码格式
        webSettings.setDefaultTextEncodingName("UTF-8");
//
//        //允许让网站使用localStorage（开启这里后，让公司的h5可以正常运行了）
        webSettings.setDomStorageEnabled(true);
//
//        //允许让网站使用浏览器
        webSettings.setDatabaseEnabled(true);
//
//        //不保存密码
//        webSettings.setSavePassword(false);

        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);

//        MIXED_CONTENT_ALWAYS_ALLOW 允许从任何来源加载内容，即使起源是不安全的；
//        MIXED_CONTENT_NEVER_ALLOW 不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
//        MIXED_CONTENT_COMPLTIBILITY_MODE 当涉及到混合式内容时，WebView会尝试去兼容最新Web浏览器的风格；
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

}
