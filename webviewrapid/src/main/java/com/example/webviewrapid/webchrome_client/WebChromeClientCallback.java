package com.example.webviewrapid.webchrome_client;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public abstract class WebChromeClientCallback {
    public void onReceivedTitle(String title) {
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams){
        return false;
    }
}
