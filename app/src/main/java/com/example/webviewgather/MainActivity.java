package com.example.webviewgather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.middleagent.OpenWebActivity;
import com.example.utilsgather.exit.ExitUtil;
import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.WebViewActivity;
import com.example.webviewrapid.base.BaseWebView;
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
                    editText.setText("file:///android_asset/jswithandroid/host.html");
                } else if (checkedId == R.id.rb_six) {
                    editText.setText("https://www.128bet.cc/?pl_props=go0Xk5Gn8lcJD3SveyJjaHRfY29kZSI6IjY0NzIwNzg2In0=");
                } else if (checkedId == R.id.rb_seven) {
                    editText.setText("https://haokan.baidu.com/?subTab=homeindex#homeindex");
                } else if (checkedId == R.id.rb_eight) {
                    editText.setText("file:///android_asset/organic-158bet/index.html");
                } else if (checkedId == R.id.rb_nine) {
                    editText.setText("file:///data/data/com.example.webviewgather/organic-158bet/index.html");
                }
            }
        });



        findViewById(R.id.tv_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenWebActivity.openWebView(MainActivity.this, getUrl());
            }
        });

        rg.check(R.id.rb_six);

        findViewById(R.id.btn_prepare_webview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewManager.doPrepare(MainActivity.this);
            }
        });
        findViewById(R.id.btn_jump_rapid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.start(MainActivity.this, getUrl());
            }
        });

        findViewById(R.id.btn_check_stack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseWebView webView = WebViewManager.getCurrent();
                WebBackForwardList webBackForwardList = webView.copyBackForwardList();
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

        findViewById(R.id.btn_load_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PracticeActivity.class);
                intent.putExtra(PracticeActivity.TAG, getUrl());
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_js_with_android).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSWithAndroidActivity.start(MainActivity.this, getUrl());
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

    @Override
    public void onBackPressed() {
        if (ExitUtil.handle(this, ExitUtil.Action.SYSTEM_HANDLE)) {
            return;
        }
        super.onBackPressed();
    }
}