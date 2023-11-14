package com.example.webviewrapid.base;

import android.content.Context;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.example.utilsgather.logcat.LogUtil;

public class BaseWebView extends WebView {
    private final String canonicalName = getClass().getCanonicalName();

    public BaseWebView(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(canonicalName + " 回调onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(canonicalName + " 回调onPause");
    }
}
