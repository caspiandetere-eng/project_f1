package com.example.project_f1.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JolpicaDriverResponse {
    @SerializedName("MRData")
    public MRData mrData;

    public static class MRData {
        @SerializedName("DriverTable")
        public DriverTable driverTable;
    }

    public static class DriverTable {
        @SerializedName("Drivers")
        public List<DriverInfo> drivers;
    }

    public static class DriverInfo {
        @SerializedName("driverId")
        public String driverId;
        @SerializedName("permanentNumber")
        public String permanentNumber;
        @SerializedName("code")
        public String code;
        @SerializedName("givenName")
        public String givenName;
        @SerializedName("familyName")
        public String familyName;
        @SerializedName("dateOfBirth")
        public String dateOfBirth;
        @SerializedName("nationality")
        public String nationality;
        @SerializedName("url")
        public String url;
    }
}
