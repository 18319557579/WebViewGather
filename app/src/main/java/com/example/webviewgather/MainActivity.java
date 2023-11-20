package com.example.webviewgather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.middleagent.OpenWebActivity;
import com.example.webviewrapid.WebViewActivity;
import com.example.webviewrapid.base_activity.BaseWebViewActivity;
import com.example.webviewrapid.webview_manager.WebViewManager;

public class MainActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.ed_url);

        RadioGroup rg = findViewById(R.id.rg_parent);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_one) {
                    editText.setText("https://www.baidu.com");
                } else if (checkedId == R.id.rb_two) {
                    editText.setText("https://m.jd.com/");
                } else if (checkedId == R.id.rb_three) {
                    editText.setText("https://www.google.com");
                } else if (checkedId == R.id.rb_four) {
                    editText.setText("https://m.weibo.cn");
                } else if (checkedId == R.id.rb_five) {
                    editText.setText("file:///android_asset/myjs.html");
                }
            }
        });



        findViewById(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenWebActivity.openWebView(MainActivity.this, getUrl());
            }
        });

        rg.check(R.id.rb_one);

        findViewById(R.id.btn_jump_rapid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.start(MainActivity.this, getUrl());
            }
        });

        findViewById(R.id.btn_prepare_webview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewManager.doPrepare(MainActivity.this);
            }
        });
    }

    private String getUrl() {
        String urlTarget = null;
        if (!TextUtils.isEmpty(editText.getText())) {
            urlTarget = editText.getText().toString();
        }
        return urlTarget;
    }
}