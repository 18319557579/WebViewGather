package com.example.webviewgather;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utilsgather.browser.BrowserUtil;
import com.example.utilsgather.clipboard.ClipboardUtil;
import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.share.SystemShareUtil;
import com.example.utilsgather.ui.status.OtherStatusBarUtil;
import com.example.webviewrapid.error.ErrorViewShowListener;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.webchrome_client.ShowFileChooserCallback;
import com.example.webviewrapid.webchrome_client.WebChromeClientCallback;
import com.example.webviewrapid.webview_client.WebViewClientCallback;

public class PracticeActivity extends AppCompatActivity {

    public static final String TAG = "PracticeActivity";  //用于每个不同url之间的分隔
    RapidWebView rapidWebView;

    private TextView toolBarTv;

    private ValueCallback<Uri[]> fileUploadCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        OtherStatusBarUtil.setColor(this, 0xFF2483D9, 0);

        initTitle();

        rapidWebView = RapidWebView.with(PracticeActivity.this)
                .setWebParent(findViewById(R.id.ll_out_container), new LinearLayout.LayoutParams(-1, -1))
                .setProgressGradientColor(Color.parseColor("#FF8C00"), Color.parseColor("#FF0000"))
                .setWebChromeClientCallback(webChromeClientCallback)
                .setErrorLayoutId(R.layout.by_load_url_error, R.id.iv_click_refresh)
                .setErrorViewShowListener(errorViewShowListener)
                .setmShowFileChooserCallback(new ShowFileChooserCallback() {
                    @Override
                    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                        fileUploadCallback = filePathCallback;

                        // 创建一个文件选择的Intent
                        Intent intent = fileChooserParams.createIntent();
                        try {
                            startActivityForResult(intent, 7777);
                        } catch (Exception e) {
                            fileUploadCallback = null;
                            return false;
                        }

                        return true;
                    }
                })
                .setWebViewClientCallback(new WebViewClientCallback() {
                    @Override
                    public void onPageFinished(String url) {
                        rapidWebView.evaluateJavascript(
                                "document.body.style.webkitUserSelect = 'none'; ", null
                        );
                    }

                    @Override
                    public void onPageStarted(String url) {
                        rapidWebView.evaluateJavascript(
                                "document.body.style.webkitUserSelect = 'none'; ", null
                        );
                    }
                })
                .loadUrl(getIntent().getStringExtra(TAG));
    }

    private void initTitle() {
        Toolbar mTitleToolBar = findViewById(R.id.practice_tool_bar);
        setSupportActionBar(mTitleToolBar);

        toolBarTv = findViewById(R.id.practice_tool_bar_tv);
        toolBarTv.post(new Runnable() {
            @Override
            public void run() {
                toolBarTv.setSelected(true);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (rapidWebView.handleBack()) {  //如果WebView自己进行了处理,那么中断事件传递
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            if (! rapidWebView.handleBack()) {
                finish();
            }
        } else if (itemId == R.id.actionbar_share) {
            String sharedText = rapidWebView.getTitle() + "\n" + rapidWebView.getUrl();
            SystemShareUtil.textShare(this, sharedText);
        } else if (itemId == R.id.actionbar_cope) {
            ClipboardUtil.copyToClipboard(rapidWebView.getUrl(), this);
            Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.actionbar_webview_refresh) {
            rapidWebView.reload();
        } else if (itemId == R.id.actionbar_open) {
            BrowserUtil.jumpBrowser(rapidWebView.getUrl(), this);
        } else if (itemId == R.id.actionbar_list) {
            WebBackForwardList webBackForwardList = rapidWebView.getRealWebView().copyBackForwardList();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    private final WebChromeClientCallback webChromeClientCallback = new WebChromeClientCallback() {
        @Override
        public void onReceivedTitle(String title) {
            toolBarTv.setText(title);
        }
    };

    private final ErrorViewShowListener errorViewShowListener = new ErrorViewShowListener() {
        @Override
        public void onErrorViewShow(View errorView, String errorUrl, String errorDescription, int errorCode) {
            ((TextView) (errorView.findViewById(R.id.app_error_url))).setText(errorUrl);
            ((TextView) (errorView.findViewById(R.id.app_error_description))).setText(errorDescription);
            ((TextView) (errorView.findViewById(R.id.app_error_code))).setText(String.valueOf(errorCode));
        }
    };

    // 处理文件选择的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtil.d("选择图片后回调 requestCode：" + requestCode + ", resultCode: " + resultCode);

        if (requestCode == 7777) {
            if (fileUploadCallback == null) {
                return;
            }

            switch (resultCode) {
                case RESULT_OK:
                    if (data != null) {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            Uri[] results = new Uri[]{Uri.parse(dataString)};
                            fileUploadCallback.onReceiveValue(results);
                        }
                    }
                    break;
                case RESULT_CANCELED:
                    fileUploadCallback.onReceiveValue(null);
                    break;
            }
            fileUploadCallback = null;
        }
    }
}