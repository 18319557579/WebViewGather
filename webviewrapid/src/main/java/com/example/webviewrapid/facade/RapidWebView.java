package com.example.webviewrapid.facade;

//import android.annotation.Nullable;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.ui.SizeTransferUtil;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.webchrome_client.RapidWebChromeClient;
import com.example.webviewrapid.webchrome_client.WebChromeClientCallback;
import com.example.webviewrapid.webview_client.ErrorManager;
import com.example.webviewrapid.webview_client.RapidWebViewClient;
import com.example.webviewrapid.webview_client.WebViewClientCallback;
import com.example.webviewrapid.floatlayer.WebProgress;
import com.example.webviewrapid.webview_client.page.PageState;
import com.example.webviewrapid.webview_manager.WebViewManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RapidWebView {

    private BaseWebView theWebView;  //真实的WebView
    private WebProgress theWebProgress;  //进度条
    public PageState pageState;  //用于WebView和ErrorView切换的状态管理

    public RapidWebView(Builder builder) {
        FrameLayout parentLayout = new FrameLayout(builder.mActivity);
        theWebView = WebViewManager.doObtain(builder.mActivity);

        parentLayout.addView(theWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        builder.mWebContainer.addView(parentLayout, builder.mLayoutParams);

        setWebProgress(builder, parentLayout);

        RapidWebViewClient rapidWebViewClient = new RapidWebViewClient(this, builder.mWebViewClientCallback);
        theWebView.setWebViewClient(rapidWebViewClient);

        theWebView.setWebChromeClient(new RapidWebChromeClient(this, builder.mWebChromeClientCallback));

        checkThenAddJavascriptInterface(builder.mInterfaceObj, builder.mInterfaceName);

        // 移除有风险的WebView系统隐藏接口
        theWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        theWebView.removeJavascriptInterface("accessibility");
        theWebView.removeJavascriptInterface("accessibilityTraversal");



        //用于自动去感知Activity的生命周期，以此来调用WebView中的生命周期
        builder.mActivity.getLifecycle().addObserver(new ActivityObserver(theWebView));

        pageState = new PageState(this);

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

    private void setWebProgress(Builder builder, FrameLayout parentLayout) {
        if (! builder.mProgressUse) {
            return;
        }

        WebProgress webProgress = new WebProgress(builder.mActivity);  //todo 如果WebView进行服用的话,我感觉WebProgress也能复用
        if (builder.mProgressEndColor == 0) {
            webProgress.setColor(builder.mProgressColor);
        } else {
            webProgress.setColor(builder.mProgressColor, builder.mProgressEndColor);
        }

        webProgress.setHeight(builder.mProgressHeight_dp);
        webProgress.setVisibility(View.GONE);
        parentLayout.addView(webProgress, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeTransferUtil.dip2px(builder.mProgressHeight_dp, parentLayout.getContext())));

        theWebProgress = webProgress;
    }

    public void loadUrl(String url) {
        theWebView.loadUrl(url);

        if (theWebProgress != null) {
            theWebProgress.show();
        }

        hideErrorView();
    }

    public WebProgress getWebProgress() {
        return theWebProgress;
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

    /**
     * 当触发了返回键时,先给WebView一个处理的时机.如果WebView可以还可以回退的话,那么就进行回退,代表自己处理了
     * @return true代表WebView自己处理了,false代表WebView没处理
     */
    public boolean handleBack() {
        hideErrorView();

        if (theWebView.canGoBackReal()) {
            theWebView.goBack();
            return true;
        }
        /*if (theWebView.getVisibility() == View.INVISIBLE) {
            theWebView.setVisibility(View.VISIBLE);
        }*/
        return false;
    }

    public String getTitle() {
        return theWebView.getTitle();
    }

    public String getUrl() {
        return theWebView.getUrl();
    }

    public void reload() {
        hideErrorView();
        theWebView.reload();
        LogUtil.d("点击了刷新");
    }

    //显示错误布局
    public void showErrorView(String errorUrl, String errorDescription, int errorCode) {
        /*if (theErrorManager == null) {
            theErrorManager = new ErrorManager(theWebView, this::reload);
        } else {
            theErrorManager.show();
        }
        theErrorManager.setErrorInfo(errorUrl, errorDescription, errorCode);

        theWebView.setVisibility(View.INVISIBLE);*/

        pageState.handleState(PageState.MyState.ERROR
            .setErrorInfo(errorUrl, errorDescription, errorCode)
        );
    }

    /**
     * 将错误页面隐藏起来,有3种方式:
     * 1.loadUrl
     * 2.reload
     * 3.goBack返回上一个页面时
     */
    public void hideErrorView() {
        /*if (theErrorManager != null) {
            theErrorManager.hide();
        }*/

        pageState.handleState(PageState.MyState.WAITING);
    }

    public void determineWaitingToNormal() {
        if (pageState.currentState.equals(PageState.MyState.WAITING)) {
            pageState.handleState(PageState.MyState.NORMAL);
            return;
        }
        LogUtil.d("当前不是等待状态, 不用切为正常状态 (可能为正常: 正常页面的进度变化; 可能为错误: 错误的回调总是比progress100来得更早)");
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
        private WebChromeClientCallback mWebChromeClientCallback;

        private String mInterfaceName = null;  //映射的对象名
        private Object mInterfaceObj = null;  //映射的对象

        private boolean mProgressUse = true;  //默认展示进度条
        private int mProgressColor = 0xFFFF0000;  //进度条纯色 / 渐变的起始色. 默认红色
        private int mProgressEndColor;  //渐变的结束色.
        private int mProgressHeight_dp = 3;  //进度条高度. 默认3dp



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

        public Builder setWebChromeClientCallback(WebChromeClientCallback webChromeClientCallback) {
            this.mWebChromeClientCallback = webChromeClientCallback;
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

        /**
         * 是否使用进度条. 默认为true
         * @param progressUse 如果为false, 那么就不会展示. 后续的进度条设置都会无效
         */
        public Builder setProgressUse(boolean progressUse) {
            this.mProgressUse = progressUse;
            return this;
        }

        public Builder setProgressColor(@ColorInt int color) {
            this.mProgressColor = color;
            return this;
        }

        public Builder setProgressGradientColor(int startColor, int endColor) {
            this.mProgressColor = startColor;
            this.mProgressEndColor = endColor;
            return this;
        }

        public Builder setProgressHeight_dp(int height_dp) {
            this.mProgressHeight_dp = height_dp;
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
