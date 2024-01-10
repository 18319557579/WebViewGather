package com.example.webviewgather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.middleagent.OpenWebActivity;
import com.example.utilsgather.exit.ExitUtil;
import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewgather.packet.StartDownloadEntry;
import com.example.webviewgather.packet.other.ImplementFile;
import com.example.webviewgather.packet.unzip_inter.TheEntryCallback;
import com.example.webviewrapid.facade.RapidWebView;
import com.example.webviewrapid.WebViewActivity;
import com.example.webviewrapid.base.BaseWebView;
import com.example.webviewrapid.webview_manager.WebViewManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private String folderName;

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
                }else if (checkedId == R.id.rb_ten) {
//                    editText.setText("http://dev.2023gitdev.com/product/GoldenPoker5004v7/-/archive/master/GoldenPoker5004v7-master.zip");
//                    editText.setText("file:///data/data/com.example.webviewgather/files/organic-158bet/index.html");

                    String url = "file://" + getFilesDir().getAbsolutePath() + File.separator +
                            "unzip" + File.separator +
                            folderName + File.separator +
                            "index.html";
                    editText.setText(url);
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

        TextView tvDownloadProgress = findViewById(R.id.tv_download_progress);
        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = "https://188d7e42c0e304afe9f9de18e79137aa.dlied1.cdntips.net/downv6.qq.com/qqweb/QQ_1/android_apk/Android_9.0.8_64.apk?mkey=659e647c0e91ec3e&f=9372&cip=14.145.202.203&proto=https&access_type=&tx_domain=down.qq.com&tx_path=%2Fqqweb%2F&tx_id=6c9382a8c8";
//                String url = "https://sem.xhscdn.com/sem/xiaohongshu.apk?%26comment=JTdCJTIyY2F0ZWdvcnklMjIlM0ElMjIlRTUlODUlQjYlRTQlQkIlOTYlN0MlRTUlODUlQjYlRTQlQkIlOTYlN0NwYXN0ZWJvYXJkJTNBNjc3ZTIxNjNjYmVlZDc0NDQyZTIyNDdlNTc1NzcwOTg3MDAxMGNhYzViZTA0ZWE4OGZhZTM3MzM5NTk4NjA5YyUyMiUyQyUyMm1hcmtldCUyMiUzQSUyMmJhaWR1JTIyJTJDJTIydGltZXN0YW1wJTIyJTNBMTcwNDg3NDQ2MjMyOSUyQyUyMnVzZXJpZCUyMiUzQSUyMmJjZmJjNDc4LTMxNzItNGJjMS1hZGRjLTViNWRkNGJhYzY0ZCUyMiU3RA%26sign=04625739a0529083f5cae50ed81a3076%26t=1704875362329&comment=JTdCJTIyY2F0ZWdvcnklMjIlM0ElMjIlRTUlODUlQjYlRTQlQkIlOTYlN0MlRTUlODUlQjYlRTQlQkIlOTYlN0NwYXN0ZWJvYXJkJTNBNjc3ZTIxNjNjYmVlZDc0NDQyZTIyNDdlNTc1NzcwOTg3MDAxMGNhYzViZTA0ZWE4OGZhZTM3MzM5NTk4NjA5YyUyMiUyQyUyMm1hcmtldCUyMiUzQSUyMmJhaWR1JTIyJTJDJTIydGltZXN0YW1wJTIyJTNBMTcwNDg3NDQ2MjMyOSUyQyUyMnVzZXJpZCUyMiUzQSUyMmJjZmJjNDc4LTMxNzItNGJjMS1hZGRjLTViNWRkNGJhYzY0ZCUyMiU3RA&sign=04625739a0529083f5cae50ed81a3076&t=1704875362329";
                String url = "https://158bet.s3.us-east-2.amazonaws.com/apk/com.h5spin.cou12/organic-128.zip";

                folderName = ImplementFile.getCleanName(ImplementFile.getName(url));
//                String location = "/data/user/0/com.example.webviewgather/files/unzip/" + folderName;
                String location = "/data/user/0/com.example.webviewgather/files/unzip";

                StartDownloadEntry.downloadFacade(url, location, MainActivity.this, new TheEntryCallback() {
                    @Override
                    public void onSuccess() {
                        LogUtil.d("下载并解压成功");

                    }

                    @Override
                    public void onDownloading(long now, long max) {
                        LogUtil.d("下载中: " + "now " + now + " | max " + max);
                        int progress = (int)((now * 100) / max);
                        tvDownloadProgress.setText("总量: " + ImplementFile.byteFormat(max) + ", 当前: " + ImplementFile.byteFormat(now) +
                                ", 进度: " + progress + "%");
                    }

                    @Override
                    public void onFail() {
                        LogUtil.d("失败");

                    }
                });

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