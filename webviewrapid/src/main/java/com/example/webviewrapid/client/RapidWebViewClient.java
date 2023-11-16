package com.example.webviewrapid.client;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.R;

import java.io.InputStream;

public class RapidWebViewClient extends WebViewClient {
    /**
     * 我发现在webView.loadUrl()加载的url是不会回调到这里的，说白了这里就是在WebView中点击的链接才会在这里进行回调，给开发者一个控制的机会
     * 无论回调true/false，都将会把该url交给webview进行处理，而不是跳转到其他的浏览器（前提是setWebViewClient被调用）
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        LogUtil.d("获取跳转的url：" + url);
        LogUtil.d("RapidWebViewClient-Intercept", "shouldOverrideUrlLoading 当前的线程信息：" + Thread.currentThread());
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtil.d("回调onPageStarted " + url);
        LogUtil.d("RapidWebViewClient-Intercept", "onPageStarted 当前的线程信息：" + Thread.currentThread());
    }

    /**
     * 调用京东网页时，会出现一次onPageStarted()回调，而多次onPageFinished()回调的情况；还有onPageFinished()和onPageStarted()
     * url不完全一致的情况
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.d("回调onPageFinished " + url);
        LogUtil.d("RapidWebViewClient-Intercept", "onPageFinished 当前的线程信息：" + Thread.currentThread());
    }

    /**
     * 重写此方法才能处理浏览器中的按键事件
     * todo 看看这个按键事件能带来什么用处
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        LogUtil.d("查看WebView中的按键事件：" + event);
        return super.shouldOverrideKeyEvent(view, event);
    }

    /**
     * 页面每一次请求资源之前都会调用这个方法。非UI线程调用，意思就是在该回调中时非UI线程
     * 此回调可用于各种URL方案(例如，http(s):、data:、file:等)，而不仅仅是那些通过网络发送请求的方案。javascript: URLs, blob: URLs，或者通过file:///android_asset/或file:// android_res/ URLs访问的资源都不会调用这个函数。
     */
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        LogUtil.d("RapidWebViewClient-Intercept", "打算拦截请求？ " + url);
        LogUtil.d("RapidWebViewClient-Intercept", "shouldInterceptRequest 当前的线程信息：" + Thread.currentThread());

        //如果是百度标题那个Logo的Url，那么用本地的图片进行替换
        if (url.equals("https://www.baidu.com/img/flexible/logo/plus_logo_web_2.png")) {
            try {
                InputStream is = view.getContext().getResources().openRawResource(R.raw.webviewrapid_set_meal) ;
                WebResourceResponse response = new WebResourceResponse("image/png", "UTF-8", is);
                Thread.sleep(10000);  //这里延迟10秒。只有这里成功返回后onPageFinished才会进行回调
                return response;
            } catch (Resources.NotFoundException exception) {
                exception.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }


        return super.shouldInterceptRequest(view, request);
    }

    /**
     * 页面加载资源时调用，每加载一个资源（比如图片）就调用一次。（shouldInterceptRequest是请求，onLoadResource是加载）
     */
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);

        LogUtil.d("RapidWebViewClient-Intercept", "onLoadResource的url：" + url);
    }
}
