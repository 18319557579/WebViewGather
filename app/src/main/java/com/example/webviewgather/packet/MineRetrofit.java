package com.example.webviewgather.packet;

import android.text.TextUtils;


import com.example.utilsgather.logcat.LogUtil;
import com.example.webviewgather.packet.network_part.MineInterceptor;
import com.example.webviewgather.packet.unzip_inter.TheBeginningCallback;
import com.example.webviewgather.packet.unzip_inter.TheInterfaceNet;



import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MineRetrofit {
    private static final int TIME_OUT_SECOND = 15;
    private static OkHttpClient.Builder mBuilder;
    private static MineInterceptor s_DownloadInterceptor = null;

    /**
     * 获得Retrofit对象
     */
    private static Retrofit getDownloadRetrofit(TheBeginningCallback startPullInspector) {
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .addHeader("Accept-Encoding", "gzip")
                        .method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (!TextUtils.isEmpty(message)) {
                    LogUtil.d(message);
                }
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (null == mBuilder) {
            mBuilder = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT_SECOND, TimeUnit.SECONDS)
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(loggingInterceptor);
        }
        if (MineRetrofit.s_DownloadInterceptor != null){
            List<Interceptor> interceptors =  mBuilder.interceptors();
            interceptors.remove(MineRetrofit.s_DownloadInterceptor);
            MineRetrofit.s_DownloadInterceptor = null;
        }
        MineRetrofit.s_DownloadInterceptor = new MineInterceptor(startPullInspector);
        mBuilder.addInterceptor(MineRetrofit.s_DownloadInterceptor);

        return new Retrofit.Builder()
                .baseUrl("http://localhost/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mBuilder.build())
                .build();
    }

    /**
     * 取消网络请求
     */
    public static void cancel(Disposable d) {
        if (null != d && !d.isDisposed()) {
            d.dispose();
        }
    }

    /**
     * 下载网络请求
     */
    public static void downloadFile(String url, long startPos, TheBeginningCallback startPullInspector, Observer<ResponseBody> observer) {
        getDownloadRetrofit(startPullInspector)
                .create(TheInterfaceNet.class)
                .downloadFile("bytes=" + startPos + "-", url)

                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
