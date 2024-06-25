package com.example.webviewrapid.webview_manager;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.net.Uri;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebViewClient;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.webview_settings.WebSettingsConfiguration;

import java.util.ArrayList;

public enum WebViewManager {
    INSTANCE;

    public static void doPrepare(Context context) {
        INSTANCE.prepare(context);
    }

    public static BaseWebView doObtain(Context context) {
        return INSTANCE.obtain(context);
    }

    public static void doRecycle(BaseWebView webView) {
        INSTANCE.recycle(webView);
    }

    public static void doDestroy() {
        INSTANCE.destroy();
    }


    private final ArrayList<BaseWebView> webViewCache = new ArrayList<>();

    public static BaseWebView getCurrent() {
        return INSTANCE.webViewCache.get(0);
    }

    /**
     * WebView的创建
     */
    private BaseWebView create(Context context) {
        BaseWebView webView = new BaseWebView(context);
        WebSettingsConfiguration.buildSettings(webView);
//        webView.setWebViewClient(new RapidWebViewClient());
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
    public BaseWebView obtain(Context context) {
        //在准备完成了之后应该不会为空了
        if (webViewCache.isEmpty()) {
            webViewCache.add(create(new MutableContextWrapper(context.getApplicationContext())));
        }
        BaseWebView webView = webViewCache.remove(0);

        WebBackForwardList webBackForwardList = webView.copyBackForwardList();
        for (int i = 0; i < webBackForwardList.getSize(); i++) {
            WebHistoryItem webHistoryItem = webBackForwardList.getItemAtIndex(i);
            LogUtil.d("index：" + i + ", webHistoryItem:" + webHistoryItem.getUrl() + " | " +
                    webHistoryItem.getOriginalUrl() + " | " + webHistoryItem.getTitle() + " | " +
                    webHistoryItem.getFavicon());

            String host = Uri.parse(webHistoryItem.getUrl()).getHost();
            LogUtil.d("打印host：" + host);
            if (TextUtils.isEmpty(host)) {
                LogUtil.d("该页面是空的页面");
            }

        }

        MutableContextWrapper mutableContextWrapper = (MutableContextWrapper) webView.getContext();
        mutableContextWrapper.setBaseContext(context);
        webView.clearHistory();
        webView.resumeTimers();
        return webView;
    }

    public void recycle(BaseWebView webView) {
        webView.stopLoading();
        webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        webView.clearHistory();
        webView.pauseTimers();

        //由于Activity级后退时,为了防止看到系统默认的访问失败丑界面,因此那里的可见性还是为INVISIBLE. 这时就在这里回收时设置为VISIBLE
        if (webView.getVisibility() != View.VISIBLE) {
            webView.setVisibility(View.VISIBLE);
        }
        webView.setBackground(null);  //同样防止设置了webview的背景

        webView.setWebChromeClient(null);
        webView.setWebViewClient(new EmptyWebViewClient());  //怎么能传null给非null的呢

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
        for (BaseWebView webView : webViewCache) {
            webView.removeAllViews();
            webView.destroy();
        }
        webViewCache.clear();
    }

    private static class EmptyWebViewClient extends WebViewClient {}
}
