package com.example.webviewrapid.webchrome_client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.facade.RapidWebView;

import java.lang.ref.WeakReference;

public class RapidWebChromeClient extends WebChromeClient {
    private RapidWebView mRapidWebView;
    private WebChromeClientCallback mWebChromeClientCallback;
    private ShowFileChooserCallback mShowFileChooserCallback;
    private boolean mOpenFileChooserFunction;

    public RapidWebChromeClient(RapidWebView mRapidWebView, WebChromeClientCallback webChromeClientCallback,
                                ShowFileChooserCallback showFileChooserCallback, boolean mOpenFileChooserFunction) {
        this.mRapidWebView = mRapidWebView;
        this.mWebChromeClientCallback = webChromeClientCallback;
        this.mShowFileChooserCallback = showFileChooserCallback;
        this.mOpenFileChooserFunction = mOpenFileChooserFunction;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        LogUtil.v(consoleMessage.message());
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        Uri uri = Uri.parse(message);
        String scheme = uri.getScheme();
        if ("daisyscheme".equals(scheme)) {  //这里专门是用于测试Js使用prompt()调用Android的.其实用onConsoleMessage来实现也是同理的
            LogUtil.d("Android响应到了JS的调用");
            if ("daisyhost".equals(uri.getHost())) {
                for (String queryKey : uri.getQueryParameterNames()) {
                    LogUtil.d("key: " + queryKey + ", value: " + uri.getQueryParameter(queryKey));
                }
            }
            result.confirm("Hello JS.");
            return true;  //这里要返回true,中止加载url.因为这里并不是真正去加载url
        }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        LogUtil.d("进度发生变化: " + newProgress);
        if (mRapidWebView.getWebProgress() != null) {  //如果有进度条的话, 则设置进度
            mRapidWebView.getWebProgress().setWebProgress(newProgress);
        }

        if (newProgress == 100) {  //错误 -> 等待 -> 正常 这一过程, 需要进度达到100%才去显示WebView
            mRapidWebView.determineWaitingToNormal();
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (mWebChromeClientCallback != null) {
            mWebChromeClientCallback.onReceivedTitle(title);
        }
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        LogUtil.d("上传图片请求，回调到了");

        //如果mShowFileChooserCallback不为空，那么代表用户自定义图片上传功能
        if (mShowFileChooserCallback != null) {
            return mShowFileChooserCallback.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }

        //如果开启图片上传功能，那么使用默认的上传功能
        if (mOpenFileChooserFunction) {
            return mRapidWebView.showFileChooser(filePathCallback, fileChooserParams);
        }

        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }
}
