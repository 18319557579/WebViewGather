package com.example.webviewrapid.facade;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import androidx.core.content.FileProvider;

import com.example.utilsgather.logcat.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileChooserManager {
    public ValueCallback<Uri[]> filePathCallback;
    public WebChromeClient.FileChooserParams fileChooserParams;
    private WeakReference<Activity> mActivityWeakReference;
    private int FILE_CHOOSE = 9001;

    private Uri captureImageUri;

    public FileChooserManager(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams, Activity activity) {
        this.filePathCallback = filePathCallback;
        this.fileChooserParams = fileChooserParams;
        this.mActivityWeakReference = new WeakReference<>(activity);
    }

    public boolean open(int chooserFunction) {
        Intent intentChooser;
        //1 的话，使用WebView给的Intent行为。188bets.cc实际为以下代码
        /* Intent intentPickPhoto = new Intent();
            intentPickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            intentPickPhoto.setType("image/*");
            intentPickPhoto.putExtra("android.intent.extra.MIME_TYPES", new String[]{"image/*"});
         */
        if (chooserFunction == 1) {
            intentChooser = fileChooserParams.createIntent();

            //2 的话，同时提供拍照和相册两种选择
        } else {
            Intent intentPickPhoto = new Intent();
            intentPickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            intentPickPhoto.setType("image/*");
            intentPickPhoto.putExtra("android.intent.extra.MIME_TYPES", new String[]{"image/*"});

            Intent intentTaskPic = new Intent();
            intentTaskPic.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intentTaskPic.resolveActivity(mActivityWeakReference.get().getPackageManager()) != null) {
                File photoFile;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    LogUtil.d("调用createImageFile出错了");
                    return false;
                }

                Uri photoURI = getUriForFile(photoFile);
                intentTaskPic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }

            intentChooser = Intent.createChooser(intentPickPhoto, "请选择吧hh");
            intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intentTaskPic});
        }

        try {
            mActivityWeakReference.get().startActivityForResult(intentChooser, FILE_CHOOSE);
        } catch (Exception e) {
            this.filePathCallback = null;
            return false;
        }

        return true;
    }

    /**
     * 创建一个临时File用来保存拍照的照片
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mActivityWeakReference.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    /**
     * 得到File的Uri
     */
    private Uri getUriForFile(File photoFile) {
        Uri photoURI = FileProvider.getUriForFile(mActivityWeakReference.get(),
                "com.example.android.fileprovider", photoFile);
        captureImageUri = photoURI;
        LogUtil.d("查看得到的Uri：" + captureImageUri);
        return photoURI;
    }


    /**
     * 处理onActivityResult()的结果
     */
    public void handleFileChooser(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSE) {
            if (filePathCallback == null) {
                return;
            }

            switch (resultCode) {
                case Activity.RESULT_OK:
                    LogUtil.d("intent: " + data);
                    if (data == null || data.getDataString() == null) {
                        //如果没有数据，基本可以判断是拍照的情况
                        LogUtil.d("图片拍照的结果：" + captureImageUri);
                        filePathCallback.onReceiveValue(new Uri[]{captureImageUri});
                    } else {
                        //有数据的话，是选择图片的情况
                        String dataString = data.getDataString();
                        LogUtil.d("获取到的dataString: " + dataString);
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
