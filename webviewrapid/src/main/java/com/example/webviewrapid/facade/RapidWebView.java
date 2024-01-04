package com.example.webviewrapid.facade;

//import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.client.RapidWebChromeClient;
import com.example.webviewrapid.client.RapidWebViewClient;
import com.example.webviewrapid.client.WebViewClientCallback;
import com.example.webviewrapid.webview_manager.WebViewManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RapidWebView {

    private BaseWebView theWebView;  //真实的WebView

    public RapidWebView(Builder builder) {
        FrameLayout parentLayout = new FrameLayout(builder.mActivity);
        theWebView = WebViewManager.doObtain(builder.mActivity);

        RapidWebViewClient rapidWebViewClient = new RapidWebViewClient();
        rapidWebViewClient.setWebViewClientCallback(builder.mWebViewClientCallback);
        theWebView.setWebViewClient(rapidWebViewClient);

        theWebView.setWebChromeClient(new RapidWebChromeClient());

        checkThenAddJavascriptInterface(builder.mInterfaceObj, builder.mInterfaceName);

        parentLayout.addView(theWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        builder.mWebContainer.addView(parentLayout, builder.mLayoutParams);

        //用于自动去感知Activity的生命周期，以此来调用WebView中的生命周期
        builder.mActivity.getLifecycle().addObserver(new ActivityObserver(theWebView));

    }

    @SuppressLint("JavascriptInterface")
    private void checkThenAddJavascriptInterface(Object interfaceObj, String interfaceName) {
        //先检查是否为空,如果都不为空则继续
        if (TextUtils.isEmpty(interfaceName) || interfaceObj == null) {
            return;
        }

        //检查映射的对象中是否有@JavascriptInterface注释
        Class clazz = interfaceObj.getClass();
        Method[] methods = clazz.getMethods();

        boolean atLeastOneAnnotation = false;
        outerLoop: for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof JavascriptInterface) {
                    atLeastOneAnnotation = true;
                    break outerLoop;
                }
            }
        }

        if (!atLeastOneAnnotation) {
            throw new RuntimeException("This object has ont offer javascript to call, please check addJavascriptInterface annotation was be added");
        }

        //正式注入映射对象
        theWebView.addJavascriptInterface(interfaceObj, interfaceName);
    }

    public void loadUrl(String url) {
        theWebView.loadUrl(url);
    }
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        theWebView.evaluateJavascript(script, resultCallback);
    }
    public BaseWebView getRealWebView() {
        return theWebView;
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
        private ViewGroup mWebContainer;  //宿主布局
        private ViewGroup.LayoutParams mLayoutParams;  //布局参数
        private WebViewClientCallback mWebViewClientCallback;

        private String mInterfaceName = null;  //映射的对象名
        private Object mInterfaceObj = null;  //映射的对象


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

        /**
         * 添加Js监听
         */
        public Builder addJavascriptInterface(Object interfaceObj, String interfaceName) {
            this.mInterfaceName = interfaceName;
            this.mInterfaceObj = interfaceObj;
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
