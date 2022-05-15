package com.richer.nio1;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkhttpClient {
    //读超时设置
    public static final long DEFAULT_READ_TIMEOUT_MILLIS = 15 * 1000;
    //写超时设置
    public static final long DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;
    //连接超时设置
    public static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 20 * 1000;
    //创建okhttp
    private static OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .build();


    public static void main(String[] args) throws IOException {
        String url="http://localhost:8801";
        getHttpServer01(url);
    }

    public static void  getHttpServer01(String url) throws IOException {
        Request request = (new Request.Builder()).url(url).get().addHeader("Content-Type", "text/html;charset=utf-8").build();
        Response response = okHttpClient.newCall(request).execute();
        String result = response.body().string();
        System.out.println(result);
    }

}
