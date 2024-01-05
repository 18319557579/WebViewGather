package com.example.webviewgather;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.utilsgather.ui.screen.ScreenFunctionUtils;
import com.example.utilsgather.ui.status.OtherStatusBarUtil;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.facade.RapidWebView;

public class PracticeActivity extends AppCompatActivity {

    public static final String TAG = "PracticeActivity";  //用于每个不同url之间的分隔
    RapidWebView rapidWebView;

    private TextView toolBarTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

//        ScreenFunctionUtils.hideActionBar(this);
        OtherStatusBarUtil.setColor(this, 0xFF2483D9, 0);

        initTitle();

        rapidWebView = RapidWebView.with(PracticeActivity.this)
                .setWebParent(findViewById(R.id.ll_out_container), new LinearLayout.LayoutParams(-1, -1))
                .setProgressGradientColor(Color.parseColor("#FF8C00"), Color.parseColor("#FF0000"))
                .loadUrl(getIntent().getStringExtra(TAG));
    }

    private void initTitle() {
        Toolbar mTitleToolBar = findViewById(R.id.practice_tool_bar);
        setSupportActionBar(mTitleToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }

        toolBarTv = findViewById(R.id.practice_tool_bar_tv);
        toolBarTv.post(new Runnable() {
            @Override
            public void run() {
                toolBarTv.setSelected(true);
            }
        });
        toolBarTv.setText("年会上佛教撒旦佛i就撒旦佛爱睡觉的佛i就哦按时间段佛加哦就氨基酸的泼妇撒旦佛");


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && rapidWebView.canGoBackReal()) {
            rapidWebView.goBack();
            return true;
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }
}