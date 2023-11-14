package com.example.webviewrapid;

import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;


import com.example.utilsgather.lifecycle_callback.CallbackActivity;
import com.example.utilsgather.ui.ScreenFunctionUtils;
import com.example.webviewrapid.databinding.ActivityWebViewBinding;
import com.example.webviewrapid.webview_manager.WebViewBuilder;
import com.example.webviewrapid.webview_settings.WebSettingsConfiguration;

public class WebViewActivity extends CallbackActivity {

    private ActivityWebViewBinding mBinding;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ScreenFunctionUtils.hideActionBar(this);

        WebView webView = WebViewBuilder.doObtain(WebViewActivity.this);
        mWebView = webView;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        mBinding.webviewrapidRlContainer.addView(webView);

        WebSettingsConfiguration.buildSettings(webView);

        webView.loadUrl("https://m.jd.com/");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebViewBuilder.doRecycle(mWebView);
    }
}