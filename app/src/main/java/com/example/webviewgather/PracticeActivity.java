package com.example.webviewgather;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.utilsgather.ui.screen.ScreenFunctionUtils;
import com.example.utilsgather.ui.status.OtherStatusBarUtil;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.facade.RapidWebView;

public class PracticeActivity extends AppCompatActivity {

    public static final String TAG = "PracticeActivity";  //用于每个不同url之间的分隔
    RapidWebView rapidWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        ScreenFunctionUtils.hideActionBar(this);
        OtherStatusBarUtil.setColor(this, 0x081926, 0);

        rapidWebView = RapidWebView.with(PracticeActivity.this)
                .setWebParent(findViewById(R.id.ll_out_container), new LinearLayout.LayoutParams(-1, -1))
                .loadUrl(getIntent().getStringExtra(TAG));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && rapidWebView.canGoBackReal()) {
            rapidWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}