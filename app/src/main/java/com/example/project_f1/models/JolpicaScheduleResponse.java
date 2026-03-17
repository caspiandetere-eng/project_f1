package com.example.project_f1.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JolpicaScheduleResponse {

    @SerializedName("MRData")
    public MRData mrData;

    public static class MRData {
        @SerializedName("RaceTable")
        public RaceTable raceTable;
    }

    public static class RaceTable {
        @SerializedName("Races")
        public List<Race> races;
    }

    public static class Race {
        @SerializedName("round")
        public String round;

        @SerializedName("raceName")
        public String raceName;

        @SerializedName("date")
        public String date; // yyyy-MM-dd

        @SerializedName("time")
        public String time; // HH:mm:ssZ

        @SerializedName("Circuit")
        public Circuit circuit;

        @SerializedName("FirstPractice")
        public Session firstPractice;

        @SerializedName("SecondPractice")
        public Session secondPractice;

        @SerializedName("ThirdPractice")
        public Session thirdPractice;

        @SerializedName("Qualifying")
        public Session qualifying;

        @SerializedName("Sprint")
        public Session sprint;
    }

    public static class Circuit {
        @SerializedName("circuitName")
        public String circuitName;

        @SerializedName("Location")
        public Location location;
    }

    public static class Location {
        @SerializedName("locality")
        public String locality;

        @SerializedName("country")
        public String country;
    }

    public static class Session {
        @SerializedName("date")
        public String date;

        @SerializedName("time")
        public String time;
    }
}
