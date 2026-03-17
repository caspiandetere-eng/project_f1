package com.example.project_f1.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class OpenF1ApiClient {
    private static final String BASE_URL = "https://api.openf1.org/";
    private static OpenF1ApiService apiService;

    public static OpenF1ApiService getApiService() {
        if (apiService == null) {
            apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(sharedClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenF1ApiService.class);
        }
        return apiService;
    }

    static OkHttpClient sharedClient() {
        return ApiHttpClient.get();
    }
}
