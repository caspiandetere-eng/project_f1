package com.example.project_f1.api;

import com.example.project_f1.models.OpenF1Position;
import com.example.project_f1.models.OpenF1Session;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenF1ApiService {
    
    @GET("v1/sessions")
    Call<List<OpenF1Session>> getSessions(
        @Query("year") int year,
        @Query("session_name") String sessionName
    );
    
    @GET("v1/position")
    Call<List<OpenF1Position>> getPositions(
        @Query("session_key") int sessionKey
    );
}
