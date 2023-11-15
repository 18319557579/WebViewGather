package com.example.webviewrapid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.widget.FrameLayout;


import com.example.utilsgather.lifecycle_callback.CallbackActivity;
import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.ui.ScreenFunctionUtils;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.base_activity.BaseWebViewActivity;
import com.example.webviewrapid.databinding.ActivityWebViewBinding;
import com.example.webviewrapid.webview_manager.WebViewManager;
import com.example.webviewrapid.webview_settings.WebSettingsConfiguration;

public class WebViewActivity extends BaseWebViewActivity {

    private ActivityWebViewBinding mBinding;
    private BaseWebView mWebView;

    /**
     * 跳转WebViewActivity
     */
    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(BaseWebViewActivity.KEY_URL, url);
        context.startActivity(intent);
    }

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

        webView.loadUrl(getTargetUrl());


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
        findViewById(R.id.webviewrapid_btn_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("判断是否能返回 " + mWebView.canGoBack());
                LogUtil.d("判断是否真正能返回 " + mWebView.canGoBackReal());
            }
        });
        findViewById(R.id.webviewrapid_btn_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d("判断是否能前进 " + mWebView.canGoForward());
            }
        });

        mBinding.webviewrapidBtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goBack();
                LogUtil.d("调用了goBack ");
            }
        });
        mBinding.webviewrapidBtn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goForward();
                LogUtil.d("调用了goForward ");
            }
        });
        mBinding.webviewrapidBtn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebBackForwardList webBackForwardList = mWebView.copyBackForwardList();
                for (int i = 0; i < webBackForwardList.getSize(); i++) {
                    WebHistoryItem webHistoryItem = webBackForwardList.getItemAtIndex(i);
                    LogUtil.d("index：" + i + ", webHistoryItem:" + webHistoryItem.getUrl() + " | " +
                            webHistoryItem.getOriginalUrl() + " | " + webHistoryItem.getTitle() + " | " +
                            webHistoryItem.getFavicon());

                    String host = Uri.parse(webHistoryItem.getUrl()).getHost();
                    LogUtil.d("打印host：" + host);
                    if (TextUtils.isEmpty(host)) {
                        LogUtil.d("该页面是空的页面");
                    }

                }

            }
        });
        mBinding.webviewrapidBtn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.zoomOut();
            }
        });
        mBinding.webviewrapidBtn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.zoomIn();
            }
        });
        mBinding.webviewrapidBtn12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.zoomBy(1.1F);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBackReal()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}