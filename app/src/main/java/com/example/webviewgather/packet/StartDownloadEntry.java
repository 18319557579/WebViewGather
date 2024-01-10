package com.example.webviewgather.packet;

import android.app.Activity;
import android.text.TextUtils;


import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewgather.packet.other.ImplementFile;
import com.example.webviewgather.packet.other.ImplementHandleUI;
import com.example.webviewgather.packet.unzip_inter.Desugar_January;
import com.example.webviewgather.packet.unzip_inter.TheBeginningCallback;
import com.example.webviewgather.packet.unzip_inter.TheEntryCallback;
import com.example.webviewgather.packet.unzip_inter.TheInnerCallback;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class StartDownloadEntry {
    private static Disposable mDownloadTask;

    public static void downloadFacade(String url, String location, Activity activity, TheEntryCallback outerDownloadCallback) {

        //这是zip下载到的路径
        String path = activity.getFilesDir().getAbsolutePath()
                + File.separator + ImplementFile.getName(url);

        StartDownloadEntry.download(url, path, new TheInnerCallback() {
            @Override
            public void onStart(Disposable d) {
                mDownloadTask = d;
                LogUtil.d("download onStart");
            }

            @Override
            public void onProgress(long totalByte, long currentByte, int progress) {
                LogUtil.d("onProgress " + progress);
                LogUtil.d("max：" + ImplementFile.byteFormat(totalByte) + ", downloaded：" + ImplementFile.byteFormat(currentByte) +
                        ", progress：" + progress + "%");
                if (outerDownloadCallback != null) outerDownloadCallback.onDownloading(currentByte, totalByte);
            }

            @Override
            public void onFinish(File file, String message) {
                LogUtil.d(message + "download onFinish " + file.getAbsolutePath());


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Desugar_January.decompressFile(path, location, true);
//                            Desugar_February.uncompressFile(path, location, true);
                            LogUtil.d("Decompression completed");

                            if (outerDownloadCallback != null) outerDownloadCallback.onSuccess();

                        } catch (Exception e) {
                            LogUtil.d("Decompression problem：" + e);
                        }
                    }
                }).start();
            }

            @Override
            public void onError(String msg) {
                LogUtil.d("download onError " + msg);
                if (outerDownloadCallback != null) outerDownloadCallback.onFail();
            }
        });
    }

    public static void download(final String url, final String filePath, final TheInnerCallback callback) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filePath)) {
            if (null != callback) {
                callback.onError("url or path empty");
            }
            return;
        }

        File oldFile = new File(filePath);
        if (oldFile.exists()) {
            if (null != callback) {
                callback.onFinish(oldFile, "File already exists");
            }
            return;
        }

        TheBeginningCallback listener = new TheBeginningCallback() {
            @Override
            public void onStart(ResponseBody responseBody) {
                saveFile(responseBody, url, filePath, callback);
            }
        };

        MineRetrofit.downloadFile(url, ImplementFile.getTempFile(url, filePath).length(), listener, new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if (null != callback) {
                    callback.onStart(d);
                }
            }

            @Override
            public void onNext(@NonNull ResponseBody responseBody) {
                LogUtil.d("implemented onNext " + responseBody);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                LogUtil.d("onError " + e.getMessage());
                if (null != callback) {
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onComplete() {
                LogUtil.d("download onComplete ");
            }
        });
    }

    /**
     * 这里是最终成功/失败的出口 ？
     */
    private static void saveFile(final ResponseBody responseBody, String url, final String filePath, final TheInnerCallback callback) {
        boolean downloadSuccess = true;
        final File tempFile = ImplementFile.getTempFile(url, filePath);
        try {
            writeFileToDisk(responseBody, tempFile.getAbsolutePath(), callback);
        } catch (Exception e) {
            e.printStackTrace();
            downloadSuccess = false;
            LogUtil.d("download failed：" + e);
        }

        LogUtil.d("download successfully？ " + downloadSuccess);

        if (downloadSuccess) {
            final boolean renameSuccess = tempFile.renameTo(new File(filePath));
            LogUtil.d("Whether the name change is successful：" + renameSuccess);
            ImplementHandleUI.INSTANCE.runOnUI(new Runnable() {
                @Override
                public void run() {
                    if (null != callback && renameSuccess) {
                        callback.onFinish(new File(filePath), "The download really worked");
                    }
                }
            });
        }
    }

    private static void writeFileToDisk(ResponseBody responseBody, String filePah, final TheInnerCallback callback) throws IOException {
        long totalByte = responseBody.contentLength();
        LogUtil.d("Length of content to download this time:" + totalByte);

        long downloadByte = 0;
        File file = new File(filePah);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        byte[] buffer = new byte[1024 * 4];
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        long tempFileLen = file.length();
        LogUtil.d("The length of the temporary file:" + tempFileLen);

        randomAccessFile.seek(tempFileLen);

        LogUtil.d("Begins reading the corresponding input stream");
        while (true) {
            //这里将输入流读到缓冲区去
            int len = responseBody.byteStream().read(buffer);
            if (len == -1) {
                LogUtil.d("The input stream reads to the end");
                break;
            }
            randomAccessFile.write(buffer, 0, len);
            downloadByte += len;

            callbackProgress(tempFileLen + totalByte, tempFileLen + downloadByte, callback);
        }
        randomAccessFile.close();

    }

    private static void callbackProgress(final long totalByte, final long downloadedByte, final TheInnerCallback callback) {
        ImplementHandleUI.INSTANCE.runOnUI(new Runnable() {
            @Override
            public void run() {
                if (null != callback) {
                    callback.onProgress(totalByte, downloadedByte, (int)((downloadedByte * 100) / totalByte));
                }
            }
        });
    }
}
