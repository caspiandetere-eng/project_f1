package com.example.project_f1.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JolpicaApiClient {
    private static final String BASE_URL = "https://api.jolpi.ca/ergast/f1/";
    private static JolpicaApiService apiService;

    public static JolpicaApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(JolpicaApiService.class);
        }
        return apiService;
    }
}
