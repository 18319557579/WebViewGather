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
import com.example.webviewrapid.webview_manager.WebViewBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = findViewById(R.id.ed_url);

        RadioGroup rg = findViewById(R.id.rg_parent);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_one) {
                    editText.setHint("https://www.baidu.com");
                } else if (checkedId == R.id.rb_two) {
                    editText.setHint("https://m.jd.com/");
                } else if (checkedId == R.id.rb_three) {
                    editText.setHint("");
                }
            }
        });



        findViewById(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlTarget;
                if (!TextUtils.isEmpty(editText.getText())) {
                    urlTarget = editText.getText().toString();
                } else {
                    urlTarget = editText.getHint().toString();
                }
                OpenWebActivity.openWebView(MainActivity.this, urlTarget);
            }
        });

        rg.check(R.id.rb_one);

        findViewById(R.id.btn_jump_rapid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_prepare_webview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewBuilder.doPrepare(MainActivity.this);
            }
        });
    }
}