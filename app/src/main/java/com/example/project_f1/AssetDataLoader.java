package com.example.project_f1;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetDataLoader {

    // ── Driver career stats (from updated_Drivers_2026.csv) ──────────────────
    public static class DriverCareerStats {
        public String name, team, nationality;
        public int championships, wins, podiums, poles, debutYear, careerPoints;
    }

    // ── Driver record (from updated_driver_records_2026.csv) ─────────────────
    public static class DriverRecord {
        public String driverCode, driverName, recordType, recordTitle, value, year;
    }

    // ── Team stats (from updated_Teams_2026.csv) ──────────────────────────────
    public static class TeamStats {
        public String name, yearsActive, base;
        public int championships, wins, podiums;
    }

    // ── Telemetry sample (from f1_data.json) ─────────────────────────────────
    public static class TelemetrySample {
        public int year;
        public String race, driverNumber;
        public double speed, throttle, brake;
        public int gear, drs;
    }

    // ── Aggregated telemetry per driver ───────────────────────────────────────
    public static class DriverTelemetry {
        public double avgSpeed, maxSpeed, avgThrottle, avgBrake;
        public int sampleCount;
        public String maxSpeedRace, maxSpeedCar;
        public int maxSpeedYear;
    }

    // ── Static caches ─────────────────────────────────────────────────────────
    // Key: driver name (lowercase, trimmed) → stats
    private static Map<String, DriverCareerStats> driverStatsCache;
    // Key: driver code (e.g. "VER") → list of records
    private static Map<String, List<DriverRecord>> driverRecordsCache;
    // Key: team name (lowercase) → stats
    private static Map<String, TeamStats> teamStatsCache;
    // Key: driver number string → aggregated telemetry
    private static Map<String, DriverTelemetry> telemetryCache;

    // ── Driver number → car name mapping ────────────────────────────────────
    private static final Map<String, String> NUMBER_TO_CAR = new HashMap<String, String>() {{
        put("1",  "Red Bull RB20");   put("33", "Red Bull RB16B");
        put("44", "Mercedes W12/W13/W14"); put("16", "Ferrari F1-75/SF-23");
        put("4",  "McLaren MCL38");   put("81", "McLaren MCL38");
        put("63", "Mercedes W14");    put("55", "Ferrari SF-23");
        put("14", "Aston Martin AMR23"); put("18", "Aston Martin AMR23");
        put("27", "Haas VF-23");      put("11", "Red Bull RB16B/RB18");
        put("10", "Alpine A521");     put("31", "Alpine A521");
        put("3",  "McLaren MCL35M");  put("22", "AlphaTauri AT04");
        put("77", "Alfa Romeo C41");  put("5",  "Aston Martin AMR21");
        put("7",  "Alfa Romeo C41");  put("99", "Alfa Romeo C41");
        put("47", "Haas VF-21");      put("9",  "Haas VF-21");
        put("88", "Alfa Romeo C41");  put("20", "Haas VF-22");
        put("24", "Alfa Romeo C42");  put("6",  "Williams FW43B");
        put("23", "Williams FW44");   put("2",  "Williams FW45");
    }};

    // ── Driver number → driver id mapping ────────────────────────────────────
    private static final Map<String, String> NUMBER_TO_ID = new HashMap<String, String>() {{
        put("1",  "verstappen"); put("33", "verstappen");
        put("44", "hamilton");   put("16", "leclerc");
        put("4",  "norris");     put("81", "piastri");
        put("63", "russell");    put("12", "antonelli");
        put("55", "sainz");      put("23", "albon");
        put("14", "alonso");     put("18", "stroll");
        put("27", "hulkenberg"); put("5",  "bortoleto");
        put("11", "perez");      put("77", "bottas");
        put("10", "gasly");      put("31", "colapinto");
        put("30", "lawson");     put("6",  "hadjar");
        put("87", "bearman");    put("17", "ocon");
        // legacy numbers used in historical data
        put("3",  "ricciardo");  put("22", "tsunoda");
        put("47", "mick_schumacher"); put("9", "mazepin");
        put("7",  "raikkonen");  put("99", "giovinazzi");
        put("88", "kubica");     put("20", "magnussen");
        put("24", "zhou");       put("2",  "sargeant");
    }};

    // ── Driver code → driver id mapping ──────────────────────────────────────
    private static final Map<String, String> CODE_TO_ID = new HashMap<String, String>() {{
        put("VER", "verstappen"); put("HAM", "hamilton");
        put("LEC", "leclerc");    put("NOR", "norris");
        put("PIA", "piastri");    put("RUS", "russell");
        put("ANT", "antonelli");  put("SAI", "sainz");
        put("ALB", "albon");      put("ALO", "alonso");
        put("STR", "stroll");     put("HUL", "hulkenberg");
        put("BOR", "bortoleto");  put("PER", "perez");
        put("BOT", "bottas");     put("GAS", "gasly");
        put("COL", "colapinto");  put("LAW", "lawson");
        put("LIN", "lindblad");   put("OCO", "ocon");
        put("BEA", "bearman");
    }};

    // ── Public accessors ──────────────────────────────────────────────────────

    public static DriverCareerStats getDriverStats(Context ctx, String driverId) {
        ensureDriverStats(ctx);
        // Try direct id match first
        for (Map.Entry<String, DriverCareerStats> e : driverStatsCache.entrySet()) {
            if (e.getKey().contains(driverId.toLowerCase().replace("_", " "))) return e.getValue();
        }
        return null;
    }

    public static List<DriverRecord> getDriverRecords(Context ctx, String driverId) {
        ensureDriverRecords(ctx);
        // Map driverId → code
        String code = null;
        for (Map.Entry<String, String> e : CODE_TO_ID.entrySet()) {
            if (e.getValue().equals(driverId)) { code = e.getKey(); break; }
        }
        if (code == null) return new ArrayList<>();
        List<DriverRecord> result = driverRecordsCache.get(code);
        return result != null ? result : new ArrayList<>();
    }

    public static TeamStats getTeamStats(Context ctx, String teamId) {
        ensureTeamStats(ctx);
        // Map teamId to CSV team name
        String mapped = mapTeamId(teamId);
        for (Map.Entry<String, TeamStats> e : teamStatsCache.entrySet()) {
            if (e.getKey().contains(mapped)) return e.getValue();
        }
        return null;
    }

    public static DriverTelemetry getDriverTelemetry(Context ctx, String driverId) {
        ensureTelemetry(ctx);
        // Find driver number(s) for this id
        for (Map.Entry<String, String> e : NUMBER_TO_ID.entrySet()) {
            if (e.getValue().equals(driverId)) {
                DriverTelemetry t = telemetryCache.get(e.getKey());
                if (t != null) return t;
            }
        }
        return null;
    }

    // ── Loaders ───────────────────────────────────────────────────────────────

    private static void ensureDriverStats(Context ctx) {
        if (driverStatsCache != null) return;
        driverStatsCache = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(ctx.getAssets().open("updated_Drivers_2026.csv")))) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] p = splitCsv(line);
                if (p.length < 9) continue;
                DriverCareerStats s = new DriverCareerStats();
                s.name           = p[0].trim();
                s.team           = p[1].trim();
                s.nationality    = p[2].trim();
                s.championships  = parseInt(p[3]);
                s.wins           = parseInt(p[4]);
                s.podiums        = parseInt(p[5]);
                s.poles          = parseInt(p[6]);
                s.debutYear      = parseInt(p[7]);
                s.careerPoints   = parseInt(p[8]);
                driverStatsCache.put(s.name.toLowerCase(), s);
            }
        } catch (Exception ignored) {}
    }

    private static void ensureDriverRecords(Context ctx) {
        if (driverRecordsCache != null) return;
        driverRecordsCache = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(ctx.getAssets().open("updated_driver_records_2026.csv")))) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] p = splitCsv(line);
                if (p.length < 6) continue;
                DriverRecord r = new DriverRecord();
                r.driverCode  = p[0].trim();
                r.driverName  = p[1].trim();
                r.recordType  = p[2].trim();
                r.recordTitle = p[3].trim();
                r.value       = p[4].trim();
                r.year        = p[5].trim();
                List<DriverRecord> list = driverRecordsCache.get(r.driverCode);
                if (list == null) { list = new ArrayList<>(); driverRecordsCache.put(r.driverCode, list); }
                list.add(r);
            }
        } catch (Exception ignored) {}
    }

    private static void ensureTeamStats(Context ctx) {
        if (teamStatsCache != null) return;
        teamStatsCache = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(ctx.getAssets().open("updated_Teams_2026.csv")))) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] p = splitCsv(line);
                if (p.length < 6) continue;
                TeamStats s = new TeamStats();
                s.name          = p[0].trim();
                s.championships = parseInt(p[1]);
                s.wins          = parseInt(p[2]);
                s.podiums       = parseInt(p[3]);
                s.yearsActive   = p[4].trim();
                s.base          = p[5].trim();
                teamStatsCache.put(s.name.toLowerCase(), s);
            }
        } catch (Exception ignored) {}
    }

    private static void ensureTelemetry(Context ctx) {
        if (telemetryCache != null) return;
        telemetryCache = new HashMap<>();
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(ctx.getAssets().open("f1_data.json")));
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            JSONObject root = new JSONObject(sb.toString());
            JSONArray telemetry = root.getJSONArray("telemetry");

            // Accumulate per driver number
            Map<String, double[]> acc = new HashMap<>(); // [sumSpeed, maxSpeed, sumThrottle, sumBrake, count]
            // Track max speed context: driverNum -> [maxSpeed, year, raceIndex]
            Map<String, String[]> maxCtx = new HashMap<>(); // driverNum -> [race, year]
            for (int i = 0; i < telemetry.length(); i++) {
                JSONObject t = telemetry.getJSONObject(i);
                String drv  = t.getString("driver");
                double speed    = t.getDouble("speed");
                double throttle = t.getDouble("throttle");
                double brake    = t.getDouble("brake");
                int year        = t.getInt("year");
                String race     = t.getString("race");
                double[] a = acc.get(drv);
                if (a == null) { a = new double[5]; acc.put(drv, a); }
                a[0] += speed;
                if (speed > a[1]) {
                    a[1] = speed;
                    maxCtx.put(drv, new String[]{race, String.valueOf(year)});
                }
                a[2] += throttle;
                a[3] += brake;
                a[4]++;
            }
            for (Map.Entry<String, double[]> e : acc.entrySet()) {
                double[] a = e.getValue();
                if (a[4] == 0) continue;
                DriverTelemetry dt = new DriverTelemetry();
                dt.avgSpeed    = a[0] / a[4];
                dt.maxSpeed    = a[1];
                dt.avgThrottle = a[2] / a[4];
                dt.avgBrake    = a[3] / a[4];
                dt.sampleCount = (int) a[4];
                String[] mctx = maxCtx.get(e.getKey());
                if (mctx != null) {
                    dt.maxSpeedRace = mctx[0];
                    dt.maxSpeedYear = parseInt(mctx[1]);
                    dt.maxSpeedCar  = NUMBER_TO_CAR.getOrDefault(e.getKey(), "F1 Car");
                }
                telemetryCache.put(e.getKey(), dt);
            }
        } catch (Exception ignored) {}
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String[] splitCsv(String line) {
        return line.split(",", -1);
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim().replaceAll("[^0-9]", "")); }
        catch (Exception e) { return 0; }
    }

    private static String mapTeamId(String teamId) {
        switch (teamId) {
            case "red_bull":     return "red bull";
            case "aston_martin": return "aston martin";
            case "rb":           return "rb";
            case "cadillac":     return "cadillac";
            case "audi":         return "audi";
            default:             return teamId.toLowerCase();
        }
    }
}
