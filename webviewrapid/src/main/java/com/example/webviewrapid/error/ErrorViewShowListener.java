package com.example.webviewrapid.error;

import android.view.View;

public interface ErrorViewShowListener {
    /**
     * 当错误页面展示时, 回调错误页面, 并携带信息
     */
    void onErrorViewShow(View errorView, String errorUrl, String errorDescription, int errorCode);
}
