package com.example.webviewgather;

import android.app.Instrumentation;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.utilsgather.browser.BrowserUtil;
import com.example.utilsgather.clipboard.ClipboardUtil;
import com.example.utilsgather.share.SystemShareUtil;
import com.example.utilsgather.ui.status.OtherStatusBarUtil;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.webchrome_client.WebChromeClientCallback;

public class PracticeActivity extends AppCompatActivity {

    public static final String TAG = "PracticeActivity";  //用于每个不同url之间的分隔
    RapidWebView rapidWebView;

    private TextView toolBarTv;

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {  //首先只有返回键需要进行处理,
            if (rapidWebView.handleBack()) {  //如果WebView自己进行了处理,那么中断事件传递
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    private WebChromeClientCallback webChromeClientCallback = new WebChromeClientCallback() {
        @Override
        public void onReceivedTitle(String title) {
            toolBarTv.setText(title);
        }
    };
}