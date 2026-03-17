package com.example.project_f1.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JolpicaStandingsResponse {
    @SerializedName("MRData")
    public MRData mrData;

    public static class MRData {
        @SerializedName("StandingsTable")
        public StandingsTable standingsTable;
    }

    public static class StandingsTable {
        @SerializedName("StandingsLists")
        public List<StandingsList> standingsLists;
    }

    public static class StandingsList {
        @SerializedName("DriverStandings")
        public List<DriverStanding> driverStandings;
    }

    public static class Driver {
        @SerializedName("driverId")
        public String driverId;
        @SerializedName("givenName")
        public String givenName;
        @SerializedName("familyName")
        public String familyName;
        @SerializedName("nationality")
        public String nationality;
        @SerializedName("dateOfBirth")
        public String dateOfBirth;
    }

    public static class DriverStanding {
        @SerializedName("position")
        public String position;
        @SerializedName("points")
        public String points;
        @SerializedName("wins")
        public String wins;
        @SerializedName("Constructors")
        public java.util.List<Constructor> constructors;
        @SerializedName("Driver")
        public Driver driver;

        // convenience getter — first constructor name or empty
        public String getConstructor() {
            if (constructors != null && !constructors.isEmpty()) return constructors.get(0).name;
            return "";
        }
    }

    public static class Constructor {
        @SerializedName("name")
        public String name;
    }
}
