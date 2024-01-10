package com.example.webviewgather.packet.network_part;



import com.example.webviewgather.packet.unzip_inter.TheBeginningCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


public class MineResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;

    public MineResponseBody(ResponseBody responseBody, TheBeginningCallback listener) {
        this.responseBody = responseBody;
        if (null != listener) {
            listener.onStart(responseBody);
        }
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(getSource(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source getSource(Source source) {
        return new ForwardingSource(source) {
            long downloadBytes = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long singleRead = super.read(sink, byteCount);
                if (-1 != singleRead) {
                    downloadBytes += singleRead;
                }
                return singleRead;
            }
        };
    }
}
