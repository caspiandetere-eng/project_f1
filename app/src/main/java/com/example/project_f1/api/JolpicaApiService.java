package com.example.project_f1.api;

import com.example.project_f1.models.JolpicaScheduleResponse;
import com.example.project_f1.models.JolpicaStandingsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface JolpicaApiService {
    @GET("{year}/driverStandings.json")
    Call<JolpicaStandingsResponse> getDriverStandings(@Path("year") int year);

    @GET("{year}.json")
    Call<JolpicaScheduleResponse> getRaceSchedule(@Path("year") int year);
}
