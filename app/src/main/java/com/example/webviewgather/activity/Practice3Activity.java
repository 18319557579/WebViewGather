package com.example.webviewgather.activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
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
import com.example.webviewgather.R;
import com.example.webviewrapid.error.ErrorViewShowListener;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.webchrome_client.WebChromeClientCallback;

import java.lang.ref.WeakReference;

public class Practice3Activity extends AppCompatActivity {

    public static final String TAG = "PracticeActivity";  //用于每个不同url之间的分隔
    RapidWebView rapidWebView;

    private TextView toolBarTv;

/*    private WeakReference<Practice3Activity> mActivityRef;

    {
        mActivityRef = new WeakReference<>(this);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        OtherStatusBarUtil.setColor(this, 0xFF2483D9, 0);

        initTitle();

        rapidWebView = RapidWebView.with(Practice3Activity.this)
                .setWebParent(findViewById(R.id.ll_out_container), new LinearLayout.LayoutParams(-1, -1))
                .setProgressGradientColor(Color.parseColor("#FF8C00"), Color.parseColor("#FF0000"))
                .setWebChromeClientCallback(new MineWebChromeClientCallback(Practice3Activity.this))
                .setErrorLayoutId(R.layout.by_load_url_error, R.id.iv_click_refresh)
                .setErrorViewShowListener(new AnonymousErrorViewShowListener())
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
        switch (item.getItemId()) {
            case android.R.id.home:
                if (! rapidWebView.handleBack()) {
                    finish();
                }
                break;
            case R.id.actionbar_share:
                String sharedText = rapidWebView.getTitle() + "\n" + rapidWebView.getUrl();
                SystemShareUtil.textShare(this, sharedText);
                break;
            case R.id.actionbar_cope:
                ClipboardUtil.copyToClipboard(rapidWebView.getUrl(), this);
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.actionbar_webview_refresh:
                rapidWebView.reload();
                break;
            case R.id.actionbar_open:
                BrowserUtil.jumpBrowser(rapidWebView.getUrl(), this);
                break;
            case R.id.actionbar_list:
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

/*    private WebChromeClientCallback webChromeClientCallback = new WebChromeClientCallback() {
        @Override
        public void onReceivedTitle(String title) {
            toolBarTv.setText(title);
        }
    };*/

    static class MineWebChromeClientCallback extends WebChromeClientCallback {
        private final WeakReference<Practice3Activity> mActivityRef;
        public MineWebChromeClientCallback(Practice3Activity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void onReceivedTitle(String title) {
            mActivityRef.get().toolBarTv.setText(title);
        }
    }

/*    private final ErrorViewShowListener errorViewShowListener = new ErrorViewShowListener() {
        @Override
        public void onErrorViewShow(View errorView, String errorUrl, String errorDescription, int errorCode) {
            ((TextView) (errorView.findViewById(R.id.app_error_url))).setText(errorUrl);
            ((TextView) (errorView.findViewById(R.id.app_error_description))).setText(errorDescription);
            ((TextView) (errorView.findViewById(R.id.app_error_code))).setText(String.valueOf(errorCode));
        }
    };*/

    static class AnonymousErrorViewShowListener implements ErrorViewShowListener {
        @Override
        public void onErrorViewShow(View errorView, String errorUrl, String errorDescription, int errorCode) {
            ((TextView) (errorView.findViewById(R.id.app_error_url))).setText(errorUrl);
            ((TextView) (errorView.findViewById(R.id.app_error_description))).setText(errorDescription);
            ((TextView) (errorView.findViewById(R.id.app_error_code))).setText(String.valueOf(errorCode));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        View ll = findViewById(R.id.ll_out_container);
        if (ll != null) {
            ViewGroup parentView = (ViewGroup) ll.getParent();
            if (parentView != null) {
                parentView.removeView(ll);
            }
        }
    }

    /*@Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewGroup parentView = findViewById(R.id.ll_out_container);
        parentView.removeAllViews();
    }*/
}