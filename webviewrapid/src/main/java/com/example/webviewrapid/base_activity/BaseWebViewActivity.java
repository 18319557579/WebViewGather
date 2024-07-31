package com.example.webviewrapid.base_activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.utilsgather.lifecycle_callback.LifecycleLogActivity;

public class BaseWebViewActivity extends LifecycleLogActivity {
    public static final String KEY_URL = "KEY_URL";

    private String targetUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 获得需要跳转的url
     */
    public String getTargetUrl() {
        if (targetUrl == null) {
            Intent intent = getIntent();
            targetUrl = intent.getStringExtra(KEY_URL);
        }
        return targetUrl;
    }


}
