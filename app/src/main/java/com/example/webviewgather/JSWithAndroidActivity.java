package com.example.webviewgather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utilsgather.logcat.LogUtil;
import com.example.utilsgather.ui.screen.ScreenFunctionUtils;
import com.example.webviewrapid.facade.RapidWebView;

public class JSWithAndroidActivity extends AppCompatActivity {

    public static final String TAG = "PASSED_URL";  //用于每个不同url之间的分隔
    RapidWebView rapidWebView;

    public static void start(Context context, String passedUrl) {
        Intent intent = new Intent(context, JSWithAndroidActivity.class);
        intent.putExtra(TAG, passedUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jswithandroid);

        ScreenFunctionUtils.hideActionBar(this);

        rapidWebView = RapidWebView.with(JSWithAndroidActivity.this)
                .setWebParent(findViewById(R.id.ll_out_container_js_with_android), new LinearLayout.LayoutParams(-1, -1))
                .loadUrl(getIntent().getStringExtra(TAG));

        findViewById(R.id.btn_operation_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rapidWebView.loadUrl("javascript:calledBy_loadUrl('Hello JS, I am from loadUrl().')");
            }
        });
        findViewById(R.id.btn_operation_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rapidWebView.evaluateJavascript("javascript:calledBy_evaluateJavascript('Hello JS, I am from evaluateJavascript().')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        LogUtil.d("Android得到了JS的返回值: " + value);
                    }
                });
            }
        });

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