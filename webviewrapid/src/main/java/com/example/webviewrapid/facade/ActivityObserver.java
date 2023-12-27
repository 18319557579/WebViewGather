package com.example.webviewrapid.facade;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.webview_manager.WebViewManager;

public class ActivityObserver implements DefaultLifecycleObserver {
    private BaseWebView mWebView;

    public ActivityObserver(BaseWebView mWebView) {
        this.mWebView = mWebView;
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        LogUtil.d("外部宿主回调 onResume");
        mWebView.resumeTimers();
        mWebView.onResume();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        LogUtil.d("外部宿主回调 onPause");
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        LogUtil.d("外部宿主回调 onDestroy");
        WebViewManager.doRecycle(mWebView);
    }
}
