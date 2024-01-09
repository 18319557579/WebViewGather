package com.example.webviewrapid.webchrome_client;

import android.net.Uri;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.webview_client.page.PageState;

public class RapidWebChromeClient extends WebChromeClient {
    private RapidWebView mRapidWebView;
    private WebChromeClientCallback mWebChromeClientCallback;

    public RapidWebChromeClient(RapidWebView mRapidWebView, WebChromeClientCallback webChromeClientCallback) {
        this.mRapidWebView = mRapidWebView;
        this.mWebChromeClientCallback = webChromeClientCallback;
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
        if (mRapidWebView.getWebProgress() != null) {  //如果有进度条的话, 则设置进度
            mRapidWebView.getWebProgress().setWebProgress(newProgress);
        }

        if (newProgress == 100) {
            mRapidWebView.determineWaitingToNormal();
        }

/*        if (mRapidWebView.getRealWebView().getVisibility() == View.INVISIBLE  //如果WebView隐藏起来了, 即刚刚为错误页面时
//                && (mRapidWebView.getErrorManager() == null || mRapidWebView.getErrorManager().isHide())
                && (mRapidWebView.pageState.theErrorManager == null || mRapidWebView.pageState.theErrorManager.isHide())
            && newProgress == 100 ) {  //当显示错误页面时，进度达到100才显示网页
//            mRapidWebView.getRealWebView().setVisibility(View.VISIBLE);
            mRapidWebView.pageState.handleState(PageState.MyState.NORMAL);
        }*/
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (mWebChromeClientCallback != null) {
            mWebChromeClientCallback.onReceivedTitle(title);
        }
    }
}
