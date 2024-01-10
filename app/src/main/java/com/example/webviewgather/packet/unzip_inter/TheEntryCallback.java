package com.example.webviewgather.packet.unzip_inter;

public interface TheEntryCallback {
    void onSuccess();
    void onDownloading(long now, long max);
    void onFail();
}
