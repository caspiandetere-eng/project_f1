package com.example.project_f1;

import android.content.Context;
import android.content.SharedPreferences;

public class KnowledgeLevelManager {
    private static final String PREFS_NAME = "F1Prefs";
    private static final String KEY_KNOWLEDGE_LEVEL = "knowledge_level";
    
    public enum KnowledgeLevel {
        ROOKIE(0),
        CASUAL(1),
        ENTHUSIAST(2),
        INSIDER(3);
        
        public final int level;
        
        KnowledgeLevel(int level) {
            this.level = level;
        }
    }
    
    public static KnowledgeLevel getKnowledgeLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String level = prefs.getString(KEY_KNOWLEDGE_LEVEL, "rookie");
        
        try {
            return KnowledgeLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return KnowledgeLevel.ROOKIE;
        }
    }
    
    public static boolean isRookie(Context context) {
        return getKnowledgeLevel(context) == KnowledgeLevel.ROOKIE;
    }
    
    public static boolean isCasual(Context context) {
        return getKnowledgeLevel(context) == KnowledgeLevel.CASUAL;
    }
    
    public static boolean isEnthusiast(Context context) {
        return getKnowledgeLevel(context) == KnowledgeLevel.ENTHUSIAST;
    }
    
    public static boolean isInsider(Context context) {
        return getKnowledgeLevel(context) == KnowledgeLevel.INSIDER;
    }
    
    public static int getCardElevation(Context context) {
        return isInsider(context) ? 16 : 12;
    }
    
    public static int getDataDensity(Context context) {
        KnowledgeLevel level = getKnowledgeLevel(context);
        switch (level) {
            case ROOKIE:
                return 1; // Sparse
            case CASUAL:
                return 2; // Normal
            case ENTHUSIAST:
                return 3; // Dense
            case INSIDER:
                return 4; // Very dense
            default:
                return 2;
        }
    }
}
