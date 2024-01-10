package com.example.webviewgather.packet.network_part;



import com.example.webviewgather.packet.unzip_inter.TheBeginningCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;


public class MineInterceptor implements Interceptor {
    private final TheBeginningCallback listener;

    public MineInterceptor(TheBeginningCallback listener) {
        this.listener = listener;
    }


    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originResponse = chain.proceed(chain.request());

        return originResponse.newBuilder()
                .body(new MineResponseBody(originResponse.body(), listener))
                .build();
    }
}
