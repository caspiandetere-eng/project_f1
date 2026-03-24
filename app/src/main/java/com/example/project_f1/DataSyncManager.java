package com.example.project_f1;

import android.content.Context;
import com.example.project_f1.api.JolpicaApiClient;
import com.example.project_f1.api.OpenF1ApiClient;
import com.example.project_f1.models.JolpicaDriverResponse;
import com.example.project_f1.models.JolpicaStandingsResponse;
import com.example.project_f1.models.OpenF1Session;
import com.google.gson.Gson;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataSyncManager {
    private static final String CACHE_SESSIONS = "sessions_2026";
    private static final String CACHE_STANDINGS = "standings_2026";
    private static final long CACHE_VALIDITY_SESSIONS = 6 * 60 * 60 * 1000; // 6 hours
    private static final long CACHE_VALIDITY_STANDINGS = 60 * 60 * 1000; // 1 hour
    private static final long CACHE_VALIDITY_HISTORICAL = 7 * 24 * 60 * 60 * 1000L; // 7 days

    private static final int[] HISTORICAL_YEARS = {2021, 2022, 2023, 2024, 2025};

    public interface SyncCallback<T> {
        void onSuccess(T data);
        void onFailure(String error);
    }

    /**
     * Load sessions: cache-first, then refresh from API if stale
     */
    public static void loadSessions(Context ctx, int year, SyncCallback<List<OpenF1Session>> callback) {
        String cached = CacheManager.getCache(ctx, CACHE_SESSIONS);
        if (cached != null) {
            try {
                List<OpenF1Session> sessions = new Gson().fromJson(cached,
                        com.google.gson.reflect.TypeToken.getParameterized(List.class, OpenF1Session.class).getType());
                callback.onSuccess(sessions);
                refreshSessionsInBackground(ctx, year);
                return;
            } catch (Exception e) {
                CacheManager.clearCache(ctx, CACHE_SESSIONS);
            }
        }
        fetchSessionsFromApi(ctx, year, callback);
    }

    /**
     * Load standings: cache-first, then refresh from API if stale
     */
    public static void loadStandings(Context ctx, int year, SyncCallback<JolpicaStandingsResponse> callback) {
        String cached = CacheManager.getCache(ctx, CACHE_STANDINGS);
        if (cached != null) {
            try {
                JolpicaStandingsResponse response = new Gson().fromJson(cached, JolpicaStandingsResponse.class);
                callback.onSuccess(response);
                refreshStandingsInBackground(ctx, year);
                return;
            } catch (Exception e) {
                CacheManager.clearCache(ctx, CACHE_STANDINGS);
            }
        }
        fetchStandingsFromApi(ctx, year, callback);
    }

    private static void fetchSessionsFromApi(Context ctx, int year, SyncCallback<List<OpenF1Session>> callback) {
        OpenF1ApiClient.getApiService().getSessions(year, "Race").enqueue(new Callback<List<OpenF1Session>>() {
            @Override
            public void onResponse(Call<List<OpenF1Session>> call, Response<List<OpenF1Session>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = new Gson().toJson(response.body());
                    CacheManager.saveCache(ctx, CACHE_SESSIONS, json, CACHE_VALIDITY_SESSIONS);
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("API error");
                }
            }

            @Override
            public void onFailure(Call<List<OpenF1Session>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    private static void fetchStandingsFromApi(Context ctx, int year, SyncCallback<JolpicaStandingsResponse> callback) {
        JolpicaApiClient.getApiService().getDriverStandings(year).enqueue(new Callback<JolpicaStandingsResponse>() {
            @Override
            public void onResponse(Call<JolpicaStandingsResponse> call, Response<JolpicaStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = new Gson().toJson(response.body());
                    CacheManager.saveCache(ctx, CACHE_STANDINGS, json, CACHE_VALIDITY_STANDINGS);
                    UserRepository.saveStandings(ctx, year, response.body());
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("API error");
                }
            }

            @Override
            public void onFailure(Call<JolpicaStandingsResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    private static void refreshSessionsInBackground(Context ctx, int year) {
        OpenF1ApiClient.getApiService().getSessions(year, "Race").enqueue(new Callback<List<OpenF1Session>>() {
            @Override
            public void onResponse(Call<List<OpenF1Session>> call, Response<List<OpenF1Session>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = new Gson().toJson(response.body());
                    CacheManager.saveCache(ctx, CACHE_SESSIONS, json, CACHE_VALIDITY_SESSIONS);
                }
            }

            @Override
            public void onFailure(Call<List<OpenF1Session>> call, Throwable t) {}
        });
    }

    private static void refreshStandingsInBackground(Context ctx, int year) {
        JolpicaApiClient.getApiService().getDriverStandings(year).enqueue(new Callback<JolpicaStandingsResponse>() {
            @Override
            public void onResponse(Call<JolpicaStandingsResponse> call, Response<JolpicaStandingsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = new Gson().toJson(response.body());
                    CacheManager.saveCache(ctx, CACHE_STANDINGS, json, CACHE_VALIDITY_STANDINGS);
                    UserRepository.saveStandings(ctx, year, response.body());
                }
            }

            @Override
            public void onFailure(Call<JolpicaStandingsResponse> call, Throwable t) {}
        });
    }

    // ─── Historical driver data prefetch (2021-2025) ──────────────────────────

    public static String driverInfoKey(String driverId) {
        return "driver_info_" + driverId;
    }

    public static String driverStandingsKey(int year, String driverId) {
        return "driver_standings_" + year + "_" + driverId;
    }

    /**
     * Prefetches driver bio + standings for all 22 drivers across 2021-2025.
     * Skips any key already cached. Safe to call on app start — all work is
     * fire-and-forget background network calls.
     */
    public static void prefetchDriverData(Context ctx) {
        List<com.example.project_f1.models.Driver> drivers =
                com.example.project_f1.models.Driver.getAllDrivers();
        Gson gson = new Gson();

        for (com.example.project_f1.models.Driver driver : drivers) {
            // Bio (career, never changes) — fetch once
            String bioKey = driverInfoKey(driver.apiId);
            if (CacheManager.getCache(ctx, bioKey) == null) {
                JolpicaApiClient.getApiService()
                        .getDriverInfo(driver.apiId)
                        .enqueue(new Callback<com.example.project_f1.models.JolpicaDriverResponse>() {
                            @Override
                            public void onResponse(
                                    Call<com.example.project_f1.models.JolpicaDriverResponse> call,
                                    Response<com.example.project_f1.models.JolpicaDriverResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    CacheManager.saveCache(ctx, bioKey,
                                            gson.toJson(response.body()), CACHE_VALIDITY_HISTORICAL);
                                }
                            }
                            @Override
                            public void onFailure(
                                    Call<com.example.project_f1.models.JolpicaDriverResponse> call,
                                    Throwable t) {}
                        });
            }

            // Standings per year
            for (int year : HISTORICAL_YEARS) {
                String standKey = driverStandingsKey(year, driver.apiId);
                if (CacheManager.getCache(ctx, standKey) != null) continue;
                final int y = year;
                JolpicaApiClient.getApiService()
                        .getDriverSeasonStandings(y, driver.apiId)
                        .enqueue(new Callback<JolpicaStandingsResponse>() {
                            @Override
                            public void onResponse(
                                    Call<JolpicaStandingsResponse> call,
                                    Response<JolpicaStandingsResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // 2025 is live — shorter TTL; past years are permanent
                                    long ttl = (y == 2025)
                                            ? CACHE_VALIDITY_STANDINGS
                                            : CACHE_VALIDITY_HISTORICAL;
                                    CacheManager.saveCache(ctx, standKey,
                                            gson.toJson(response.body()), ttl);
                                }
                            }
                            @Override
                            public void onFailure(
                                    Call<JolpicaStandingsResponse> call, Throwable t) {}
                        });
            }
        }
    }
}
