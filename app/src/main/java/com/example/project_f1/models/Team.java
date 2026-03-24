package com.example.project_f1.models;

import com.example.project_f1.R;
import java.util.ArrayList;
import java.util.List;

public class Team {
    public String id;
    public String name;
    public String color;
    public int logoResId;

    public Team(String id, String name, String color, int logoResId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.logoResId = logoResId;
    }

    public static List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();

        teams.add(new Team("mercedes", "Mercedes-AMG", "#00D2BE", R.drawable.mercedes));
        teams.add(new Team("ferrari", "Ferrari", "#DC0000", R.drawable.ferrari_logo));
        teams.add(new Team("mclaren", "McLaren", "#FF8700", R.drawable.mclaren));
        teams.add(new Team("red_bull", "Red Bull Racing", "#0600EF", R.drawable.redbull_logo));
        teams.add(new Team("aston_martin", "Aston Martin", "#006F62", R.drawable.aston_martin_logo));
        teams.add(new Team("audi", "Audi", "#BB0000", R.drawable.audi_logo_new));
        teams.add(new Team("cadillac", "Cadillac Formula 1 Team", "#0082FA", R.drawable.cadillac_logo));
        teams.add(new Team("alpine", "Alpine", "#0082FA", R.drawable.alpine_logo));
        teams.add(new Team("williams", "Williams", "#005AFF", R.drawable.williams_logo));
        teams.add(new Team("rb", "Racing Bulls", "#1E3A5F", R.drawable.racing_bulls_logo));
        teams.add(new Team("haas", "Haas F1 Team", "#B6BABD", R.drawable.haas_logo));

        return teams;
    }

    public static Team getTeamById(String id) {
        for (Team team : getAllTeams()) {
            if (team.id.equals(id)) {
                return team;
            }
        }
        return null;
    }
}
