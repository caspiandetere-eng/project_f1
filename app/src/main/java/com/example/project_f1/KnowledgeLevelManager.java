package com.example.project_f1;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 20-level XP system: 4 tiers × 5 levels each.
 *
 * Tiers:  ROOKIE (L1–L5) | CASUAL (L6–L10) | ENTHUSIAST (L11–L15) | INSIDER (L16–L20)
 * XP per correct answer: scales with current tier (10 / 15 / 20 / 25).
 * XP needed per level: 100 XP each (so 500 XP per tier).
 *
 * After a quiz:
 *  - Score ≥ 70%  → award full XP, possibly level up
 *  - Score 40–69% → award half XP, no change
 *  - Score < 40%  → no XP, demote one level (floor: L1)
 */
public class KnowledgeLevelManager {

    private static final String PREFS = "F1Prefs";
    private static final String KEY_LEVEL  = "xp_level";   // 1–20
    private static final String KEY_XP     = "xp_points";  // XP within current level (0–99)
    private static final String KEY_TOTAL  = "xp_total";   // all-time XP (for display)

    // Legacy key kept for backward compat reads
    private static final String KEY_KNOWLEDGE_LEVEL = "knowledge_level";

    public static final int MAX_LEVEL = 20;
    public static final int XP_PER_LEVEL = 100;

    // ── Tier helpers ──────────────────────────────────────────────────────────

    public static int getLevel(Context ctx) {
        return prefs(ctx).getInt(KEY_LEVEL, 1);
    }

    public static int getXp(Context ctx) {
        return prefs(ctx).getInt(KEY_XP, 0);
    }

    public static int getTotalXp(Context ctx) {
        return prefs(ctx).getInt(KEY_TOTAL, 0);
    }

    /** Tier name for a given level (1–20). */
    public static String tierForLevel(int level) {
        if (level <= 5)  return "rookie";
        if (level <= 10) return "casual";
        if (level <= 15) return "enthusiast";
        return "insider";
    }

    public static String tierForLevel(Context ctx) {
        return tierForLevel(getLevel(ctx));
    }

    /** Display label e.g. "ROOKIE · L3" */
    public static String displayLabel(Context ctx) {
        int lvl = getLevel(ctx);
        return tierForLevel(lvl).toUpperCase() + " · L" + lvl;
    }

    /** XP awarded per correct answer based on current tier. */
    public static int xpPerCorrect(Context ctx) {
        switch (tierForLevel(ctx)) {
            case "casual":      return 15;
            case "enthusiast":  return 20;
            case "insider":     return 25;
            default:            return 10; // rookie
        }
    }

    // ── Quiz result processing ────────────────────────────────────────────────

    public static class QuizResult {
        public final int oldLevel;
        public final int newLevel;
        public final int xpEarned;
        public final int xpAfter;   // XP within new level after quiz
        public final boolean leveledUp;
        public final boolean demoted;

        QuizResult(int oldLevel, int newLevel, int xpEarned, int xpAfter) {
            this.oldLevel  = oldLevel;
            this.newLevel  = newLevel;
            this.xpEarned  = xpEarned;
            this.xpAfter   = xpAfter;
            this.leveledUp = newLevel > oldLevel;
            this.demoted   = newLevel < oldLevel;
        }
    }

    /**
     * Call after a quiz finishes.
     * @param correct  number of correct answers
     * @param total    total questions
     */
    public static QuizResult applyQuizResult(Context ctx, int correct, int total) {
        SharedPreferences p = prefs(ctx);
        int oldLevel = p.getInt(KEY_LEVEL, 1);
        int oldXp    = p.getInt(KEY_XP, 0);
        int oldTotal = p.getInt(KEY_TOTAL, 0);

        float pct = (float) correct / total;
        int xpEarned;
        int newLevel;
        int newXp;

        if (pct >= 0.70f) {
            // Full XP — award per correct answer
            xpEarned = correct * xpPerCorrect(ctx);
            int accumulated = oldXp + xpEarned;
            int levelsGained = accumulated / XP_PER_LEVEL;
            newXp    = accumulated % XP_PER_LEVEL;
            newLevel = Math.min(oldLevel + levelsGained, MAX_LEVEL);
            if (newLevel == MAX_LEVEL) newXp = Math.max(newXp, oldXp); // cap at max
        } else if (pct >= 0.40f) {
            // Half XP — no level change
            xpEarned = (correct * xpPerCorrect(ctx)) / 2;
            newLevel = oldLevel;
            newXp    = Math.min(oldXp + xpEarned, XP_PER_LEVEL - 1);
        } else {
            // Poor score — demote one level, no XP
            xpEarned = 0;
            newLevel = Math.max(oldLevel - 1, 1);
            newXp    = newLevel < oldLevel ? XP_PER_LEVEL / 2 : oldXp; // reset to 50 on demotion
        }

        // Persist
        p.edit()
            .putInt(KEY_LEVEL, newLevel)
            .putInt(KEY_XP, newXp)
            .putInt(KEY_TOTAL, oldTotal + xpEarned)
            .putString(KEY_KNOWLEDGE_LEVEL, tierForLevel(newLevel))
            .putString("user_level", tierForLevel(newLevel))
            .apply();

        return new QuizResult(oldLevel, newLevel, xpEarned, newXp);
    }

    // ── Legacy compat ─────────────────────────────────────────────────────────

    public static boolean isRookie(Context ctx)      { return "rookie".equals(tierForLevel(ctx)); }
    public static boolean isCasual(Context ctx)      { return "casual".equals(tierForLevel(ctx)); }
    public static boolean isEnthusiast(Context ctx)  { return "enthusiast".equals(tierForLevel(ctx)); }
    public static boolean isInsider(Context ctx)     { return "insider".equals(tierForLevel(ctx)); }

    public static int getDataDensity(Context ctx) {
        switch (tierForLevel(ctx)) {
            case "casual":      return 2;
            case "enthusiast":  return 3;
            case "insider":     return 4;
            default:            return 1;
        }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
