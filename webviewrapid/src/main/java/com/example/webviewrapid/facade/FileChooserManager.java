package com.example.webviewrapid.facade;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import java.lang.ref.WeakReference;

public class FileChooserManager {
    public ValueCallback<Uri[]> filePathCallback;
    public WebChromeClient.FileChooserParams fileChooserParams;
    private WeakReference<Activity> mActivityWeakReference;
    private int FILE_CHOOSE = 9001;

    public FileChooserManager(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams, Activity activity) {
        this.filePathCallback = filePathCallback;
        this.fileChooserParams = fileChooserParams;
        this.mActivityWeakReference = new WeakReference<>(activity);
    }

    public boolean open() {
        // 创建一个文件选择的Intent
        Intent intent = fileChooserParams.createIntent();
        try {
            mActivityWeakReference.get().startActivityForResult(intent, FILE_CHOOSE);
        } catch (Exception e) {
            this.filePathCallback = null;
            return false;
        }

        return true;
    }

    public void handleFileChooser(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSE) {
            if (filePathCallback == null) {
                return;
            }

            switch (resultCode) {
                case Activity.RESULT_OK:

                    if (data != null) {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            Uri[] results = new Uri[]{Uri.parse(dataString)};
                            filePathCallback.onReceiveValue(results);
                        }
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    filePathCallback.onReceiveValue(null);
                    break;
            }
            filePathCallback = null;
        }
    }
}
