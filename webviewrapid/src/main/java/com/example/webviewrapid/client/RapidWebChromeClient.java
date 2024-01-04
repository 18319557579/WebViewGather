package com.example.webviewrapid.client;

import android.net.Uri;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.floatlayer.WebProgress;

public class RapidWebChromeClient extends WebChromeClient {
    private RapidWebView mRapidWebView;

    public RapidWebChromeClient(RapidWebView mRapidWebView) {
        this.mRapidWebView = mRapidWebView;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        LogUtil.i(consoleMessage.message());
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
        if (mRapidWebView.getWebProgress() != null) {
            mRapidWebView.getWebProgress().setWebProgress(newProgress);
        }
    }
}
