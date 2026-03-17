package com.example.project_f1.api;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ApiHttpClient {
    private static OkHttpClient instance;

    public static OkHttpClient get() {
        if (instance == null) {
            instance = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        }
        return instance;
    }
}
