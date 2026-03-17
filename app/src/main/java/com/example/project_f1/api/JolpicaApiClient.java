package com.example.project_f1.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JolpicaApiClient {
    private static final String BASE_URL = "https://api.jolpi.ca/ergast/f1/";
    private static JolpicaApiService apiService;

    public static JolpicaApiService getApiService() {
        if (apiService == null) {
            apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ApiHttpClient.get())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JolpicaApiService.class);
        }
        return apiService;
    }
}
