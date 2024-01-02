package com.example.webviewrapid.client;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import com.example.utilsgather.logcat.LogUtil;

public class RapidWebChromeClient extends WebChromeClient {
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        LogUtil.i(consoleMessage.message());
        return true;
    }
}
