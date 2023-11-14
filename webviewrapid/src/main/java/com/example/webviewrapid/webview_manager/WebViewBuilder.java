package com.example.webviewrapid.webview_manager;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.webview_settings.WebSettingsConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public enum WebViewBuilder {
    INSTANCE;

    public static void doPrepare(Context context) {
        INSTANCE.prepare(context);
    }

    public static WebView doObtain(Context context) {
        return INSTANCE.obtain(context);
    }

    public static void doRecycle(WebView webView) {
        INSTANCE.recycle(webView);
    }

    public static void doDestroy() {
        INSTANCE.destroy();
    }


    private final ArrayList<WebView> webViewCache = new ArrayList<>();

    /**
     * WebView的创建
     */
    private WebView create(Context context) {
        WebView webView = new WebView(context);
        WebSettingsConfiguration.buildSettings(webView);
        return webView;
    }

    /**
     * 对WebView进行预加载
     */
    public void prepare(Context context) {
        if (webViewCache.isEmpty()) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    webViewCache.add(create(new MutableContextWrapper(context.getApplicationContext())));
                    LogUtil.d("WebView的实例准备好了");
                    return false;
                }
            });
        }
    }

    /**
     * 获得WebView的实例（如果列表里面已经有则直接用，否则新建一个）
     */
    public WebView obtain(Context context) {
        //在准备完成了之后应该不会为空了
        if (webViewCache.isEmpty()) {
            webViewCache.add(create(new MutableContextWrapper(context)));
        }
        WebView webView = webViewCache.remove(0);
        MutableContextWrapper mutableContextWrapper = (MutableContextWrapper) webView.getContext();
        mutableContextWrapper.setBaseContext(context);
        webView.clearHistory();
        webView.resumeTimers();
        return webView;
    }

    public void recycle(WebView webView) {
        webView.stopLoading();
        webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        webView.clearHistory();
        webView.pauseTimers();
//        webView.setWebChromeClient(null);
//        webView.setWebViewClient(null);  //怎么能传null给非null的呢
        ViewGroup parent = (ViewGroup) webView.getParent();
        if (parent != null) {
            parent.removeView(webView);
        }
        MutableContextWrapper mutableContextWrapper = (MutableContextWrapper) webView.getContext();
        mutableContextWrapper.setBaseContext(webView.getContext().getApplicationContext());

        if (!webViewCache.contains(webView)) {
            webViewCache.add(webView);
        }
        LogUtil.d("WebView已被回收，当前的列表：" + webViewCache);
    }

    public void destroy() {
        for (WebView webView : webViewCache) {
            webView.removeAllViews();
            webView.destroy();
        }
        webViewCache.clear();
    }
}
