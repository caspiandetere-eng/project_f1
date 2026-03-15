package com.example.project_f1.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenF1ApiClient {
    private static final String BASE_URL = "https://api.openf1.org/";
    private static OpenF1ApiService apiService;

    public static OpenF1ApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            apiService = retrofit.create(OpenF1ApiService.class);
        }
        return apiService;
    }
}
