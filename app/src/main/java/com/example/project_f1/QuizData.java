package com.example.project_f1;

import java.util.Arrays;
import java.util.List;

public class QuizData {

    public static final List<QuizQuestion> QUESTIONS = Arrays.asList(
        new QuizQuestion("What does DRS stand for?",
            new String[]{"Drag Reduction System", "Dynamic Racing Speed", "Downforce Reduction Setup", "Drive Response System"},
            0, "DRS reduces drag by opening the rear wing."),

        new QuizQuestion("How many drivers are on the grid?",
            new String[]{"10", "20", "22", "24"},
            1, "There are 10 teams with 2 drivers each."),

        new QuizQuestion("What is pole position?",
            new String[]{"Last place", "Fastest lap in race", "First starting position", "Pit lane start"},
            2, "Pole position is P1 on the starting grid."),

        new QuizQuestion("Which tire is fastest?",
            new String[]{"Hard", "Medium", "Soft", "Wet"},
            2, "Soft tires provide maximum grip."),

        new QuizQuestion("What is ERS?",
            new String[]{"Engine Racing System", "Energy Recovery System", "Electronic Setup", "Engine Speed"},
            1, "ERS stores and deploys energy."),

        new QuizQuestion("What does a yellow flag mean?",
            new String[]{"Stop race", "Slow down", "Overtake", "Pit"},
            1, "Drivers must slow down under yellow."),

        new QuizQuestion("What is the Halo?",
            new String[]{"Speed device", "Driver protection", "Wing", "Cooling"},
            1, "Halo protects the driver's head."),

        new QuizQuestion("What is downforce?",
            new String[]{"Lift", "Grip force", "Fuel pressure", "Engine power"},
            1, "Downforce pushes car into track."),

        new QuizQuestion("What is undercut?",
            new String[]{"Pit later", "Pit earlier for advantage", "Skip pit", "Change engine"},
            1, "Undercut gains time using fresh tires."),

        new QuizQuestion("What is oversteer?",
            new String[]{"Front slides", "Rear slides", "No grip", "Engine loss"},
            1, "Rear loses grip causing oversteer."),

        new QuizQuestion("What is MGU-K?",
            new String[]{"Cooling", "Energy recovery", "Fuel system", "Brake"},
            1, "MGU-K recovers braking energy."),

        new QuizQuestion("What is parc fermé?",
            new String[]{"Open setup", "Restricted setup", "Pit closed", "Race stop"},
            1, "Car setup is restricted after qualifying."),

        new QuizQuestion("What is slipstream?",
            new String[]{"Driving alone", "Following closely", "Pit strategy", "DRS"},
            1, "Slipstream reduces drag behind cars."),

        new QuizQuestion("What is a safety car?",
            new String[]{"Medical", "Leads race", "Backup", "Practice"},
            1, "Safety car controls pace in danger."),

        new QuizQuestion("What is race strategy?",
            new String[]{"Drive slow", "Optimize race", "Avoid racing", "Fuel only"},
            1, "Strategy decides pit stops and pace.")
    );

    /** Returns level string based on score (0–15). */
    public static String levelForScore(int score) {
        if (score <= 4)  return "rookie";
        if (score <= 8)  return "casual";
        if (score <= 12) return "enthusiast";
        return "insider";
    }

    public static String messageForLevel(String level) {
        switch (level) {
            case "casual":      return "Good knowledge of F1 basics.";
            case "enthusiast":  return "You understand F1 deeply.";
            case "insider":     return "You're an F1 expert.";
            default:            return "You're just getting started.";
        }
    }
}
