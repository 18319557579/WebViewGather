package com.example.webviewrapid.webview_client;

public interface WebViewClientCallback {
    void onPageStarted(String url);
    void onPageFinished(String url);
}
