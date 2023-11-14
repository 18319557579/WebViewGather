package com.example.webviewrapid;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.example.utilsgather.lifecycle_callback.CallbackActivity;
import com.example.utilsgather.ui.ScreenFunctionUtils;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.databinding.ActivityWebViewBinding;
import com.example.webviewrapid.webview_manager.WebViewManager;
import com.example.webviewrapid.webview_settings.WebSettingsConfiguration;

public class WebViewActivity extends CallbackActivity {

    private ActivityWebViewBinding mBinding;
    private BaseWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ScreenFunctionUtils.hideActionBar(this);

        BaseWebView webView = WebViewManager.doObtain(WebViewActivity.this);
        mWebView = webView;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        mBinding.webviewrapidRlContainer.addView(webView);

        WebSettingsConfiguration.buildSettings(webView);

        webView.loadUrl("https://m.jd.com/");


        initView();
    }

    private void initView() {
        findViewById(R.id.webviewrapid_btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.onResume();
            }
        });
        findViewById(R.id.webviewrapid_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.onPause();
            }
        });
        findViewById(R.id.webviewrapid_btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.resumeTimers();
            }
        });
        findViewById(R.id.webviewrapid_btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.pauseTimers();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebViewManager.doRecycle(mWebView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.resumeTimers();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }
}