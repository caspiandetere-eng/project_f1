package com.example.project_f1.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class F1ApiClient {
    private static final String BASE_URL = "https://ergast.com/api/f1/";
    private static Retrofit retrofit;
    
    public static F1ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit.create(F1ApiService.class);
    }
}
