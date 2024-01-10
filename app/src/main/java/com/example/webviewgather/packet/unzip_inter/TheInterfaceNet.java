package com.example.webviewgather.packet.unzip_inter;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface TheInterfaceNet {
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Header("Range") String range, @Url String url);
}
