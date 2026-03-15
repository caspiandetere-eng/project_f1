package com.example.project_f1.api;

import com.example.project_f1.models.RaceResponse;
import com.example.project_f1.models.StandingsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface F1ApiService {
    
    @GET("current.json")
    Call<RaceResponse> getCurrentSeasonRaces();
    
    @GET("current/next.json")
    Call<RaceResponse> getNextRace();
    
    @GET("current/driverStandings.json")
    Call<StandingsResponse> getDriverStandings();
    
    @GET("{year}/last/results.json")
    Call<RaceResponse> getLastRaceResults(@Path("year") String year);
}
