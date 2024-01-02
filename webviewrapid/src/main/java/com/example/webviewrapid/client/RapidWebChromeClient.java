package com.example.webviewrapid.client;

import android.net.Uri;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.utilsgather.logcat.LogUtil;

public class RapidWebChromeClient extends WebChromeClient {
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        LogUtil.i(consoleMessage.message());
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        Uri uri = Uri.parse(message);
        String scheme = uri.getScheme();
        if ("daisyscheme".equals(scheme)) {
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
}
