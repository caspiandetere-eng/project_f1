package com.example.project_f1.models;

import com.google.gson.annotations.SerializedName;

public class OpenF1Session {
    @SerializedName("session_key")
    public int sessionKey;
    
    @SerializedName("session_name")
    public String sessionName;
    
    @SerializedName("circuit_short_name")
    public String circuitShortName;
    
    @SerializedName("country_name")
    public String countryName;
    
    @SerializedName("year")
    public int year;
    
    @SerializedName("date_start")
    public String dateStart;
    
    @SerializedName("date_end")
    public String dateEnd;
}
