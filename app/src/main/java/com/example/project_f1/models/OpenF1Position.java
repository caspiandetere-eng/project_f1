package com.example.project_f1.models;

import com.google.gson.annotations.SerializedName;

public class OpenF1Position {
    @SerializedName("driver_number")
    public int driverNumber;
    
    @SerializedName("position")
    public int position;
    
    @SerializedName("date")
    public String date;
}
