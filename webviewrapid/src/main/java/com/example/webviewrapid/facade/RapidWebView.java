package com.example.webviewrapid.facade;

//import android.annotation.Nullable;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.client.RapidWebChromeClient;
import com.example.webviewrapid.client.RapidWebViewClient;
import com.example.webviewrapid.client.WebViewClientCallback;
import com.example.webviewrapid.webview_manager.WebViewManager;

public class RapidWebView {

    private BaseWebView theWebView;  //真实的WebView

    public RapidWebView(Builder builder) {
        FrameLayout parentLayout = new FrameLayout(builder.mActivity);
        theWebView = WebViewManager.doObtain(builder.mActivity);

        RapidWebViewClient rapidWebViewClient = new RapidWebViewClient();
        rapidWebViewClient.setWebViewClientCallback(builder.mWebViewClientCallback);
        theWebView.setWebViewClient(rapidWebViewClient);

        theWebView.setWebChromeClient(new RapidWebChromeClient());

        parentLayout.addView(theWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        builder.mWebContainer.addView(parentLayout, builder.mLayoutParams);

        //用于自动去感知Activity的生命周期，以此来调用WebView中的生命周期
        builder.mActivity.getLifecycle().addObserver(new ActivityObserver(theWebView));

    }

    public void loadUrl(String url) {
        theWebView.loadUrl(url);
    }
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        theWebView.evaluateJavascript(script, resultCallback);
    }

    public boolean canGoBackReal() {
        return theWebView.canGoBackReal();
    }

    public void goBack() {
        theWebView.goBack();
    }

//----------------------------------------------Builder-------------------------------------------------

    public static Builder with(@NonNull AppCompatActivity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        private AppCompatActivity mActivity;  //加载WebView的Activity
        private ViewGroup mWebContainer;
        private ViewGroup.LayoutParams mLayoutParams;
        private WebViewClientCallback mWebViewClientCallback;

        public Builder(AppCompatActivity activity) {
            mActivity = activity;
        }

        public Builder setWebParent(@NonNull ViewGroup webContainer, ViewGroup.LayoutParams layoutParams) {
            this.mWebContainer = webContainer;
            this.mLayoutParams = layoutParams;
            return this;
        }

        public Builder setWebViewClientCallback(WebViewClientCallback webViewClientCallback) {
            this.mWebViewClientCallback = webViewClientCallback;
            return this;
        }

        //在这一步才实例化了一个RapidWebView
        public RapidWebView getRapidWebView() {
            return new RapidWebView(this);
        }

        public RapidWebView loadUrl(String url) {
            RapidWebView rapidWebView = getRapidWebView();
            rapidWebView.loadUrl(url);
            return rapidWebView;
        }
    }
}
