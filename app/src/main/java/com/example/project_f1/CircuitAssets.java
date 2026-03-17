package com.example.project_f1;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps F1 circuit names to their Android Vector Drawable resource IDs.
 * Assets live in res/drawable/ as *_1.xml (converted from SVG).
 */
public final class CircuitAssets {

    private static final Map<String, Integer> CIRCUIT_MAP = new HashMap<>();

    static {
        // ── Current calendar ──────────────────────────────────────────────
        CIRCUIT_MAP.put("albert park",       R.drawable.melbourne_1);
        CIRCUIT_MAP.put("australia",         R.drawable.melbourne_1);
        CIRCUIT_MAP.put("melbourne",         R.drawable.melbourne_1);
        CIRCUIT_MAP.put("shanghai",          R.drawable.shanghai_1);
        CIRCUIT_MAP.put("china",             R.drawable.shanghai_1);
        CIRCUIT_MAP.put("suzuka",            R.drawable.suzuka_1);
        CIRCUIT_MAP.put("japan",             R.drawable.suzuka_1);
        CIRCUIT_MAP.put("bahrain",           R.drawable.bahrain_1);
        CIRCUIT_MAP.put("jeddah",            R.drawable.jeddah_1);
        CIRCUIT_MAP.put("saudi arabia",      R.drawable.jeddah_1);
        CIRCUIT_MAP.put("miami",             R.drawable.miami_1);
        CIRCUIT_MAP.put("montreal",          R.drawable.montreal_1);
        CIRCUIT_MAP.put("canada",            R.drawable.montreal_1);
        CIRCUIT_MAP.put("monaco",            R.drawable.monaco_1);
        CIRCUIT_MAP.put("barcelona",         R.drawable.catalunya_1);
        CIRCUIT_MAP.put("spain",             R.drawable.catalunya_1);
        CIRCUIT_MAP.put("red bull ring",     R.drawable.spielberg_1);
        CIRCUIT_MAP.put("austria",           R.drawable.spielberg_1);
        CIRCUIT_MAP.put("silverstone",       R.drawable.silverstone_1);
        CIRCUIT_MAP.put("uk",                R.drawable.silverstone_1);
        CIRCUIT_MAP.put("britain",           R.drawable.silverstone_1);
        CIRCUIT_MAP.put("spa",               R.drawable.spa_francorchamps_1);
        CIRCUIT_MAP.put("spa-francorchamps", R.drawable.spa_francorchamps_1);
        CIRCUIT_MAP.put("belgium",           R.drawable.spa_francorchamps_1);
        CIRCUIT_MAP.put("hungaroring",       R.drawable.hungaroring_1);
        CIRCUIT_MAP.put("hungary",           R.drawable.hungaroring_1);
        CIRCUIT_MAP.put("zandvoort",         R.drawable.zandvoort_1);
        CIRCUIT_MAP.put("netherlands",       R.drawable.zandvoort_1);
        CIRCUIT_MAP.put("monza",             R.drawable.monza_1);
        CIRCUIT_MAP.put("italy",             R.drawable.monza_1);
        CIRCUIT_MAP.put("madrid",            R.drawable.madring_1);
        CIRCUIT_MAP.put("baku",              R.drawable.baku_1);
        CIRCUIT_MAP.put("azerbaijan",        R.drawable.baku_1);
        CIRCUIT_MAP.put("singapore",         R.drawable.marina_bay_1);
        CIRCUIT_MAP.put("marina bay",        R.drawable.marina_bay_1);
        CIRCUIT_MAP.put("cota",              R.drawable.austin_1);
        CIRCUIT_MAP.put("austin",            R.drawable.austin_1);
        CIRCUIT_MAP.put("mexico",            R.drawable.mexico_city_1);
        CIRCUIT_MAP.put("mexico city",       R.drawable.mexico_city_1);
        CIRCUIT_MAP.put("interlagos",        R.drawable.interlagos_1);
        CIRCUIT_MAP.put("brazil",            R.drawable.interlagos_1);
        CIRCUIT_MAP.put("las vegas",         R.drawable.las_vegas_1);
        CIRCUIT_MAP.put("lusail",            R.drawable.lusail_1);
        CIRCUIT_MAP.put("qatar",             R.drawable.lusail_1);
        CIRCUIT_MAP.put("yas marina",        R.drawable.yas_marina_1);
        CIRCUIT_MAP.put("uae",               R.drawable.yas_marina_1);
        CIRCUIT_MAP.put("abu dhabi",         R.drawable.yas_marina_1);

        // ── Historic circuits ─────────────────────────────────────────────
        CIRCUIT_MAP.put("pescara",           R.drawable.pescara_1);
        CIRCUIT_MAP.put("nurburgring",       R.drawable.nurburgring_1);
        CIRCUIT_MAP.put("nürburgring",       R.drawable.nurburgring_1);
        CIRCUIT_MAP.put("sepang",            R.drawable.sepang_1);
        CIRCUIT_MAP.put("malaysia",          R.drawable.sepang_1);
        CIRCUIT_MAP.put("buddh",             R.drawable.buddh_1);
        CIRCUIT_MAP.put("india",             R.drawable.buddh_1);
        CIRCUIT_MAP.put("kyalami",           R.drawable.kyalami_1);
        CIRCUIT_MAP.put("south africa",      R.drawable.kyalami_1);
        CIRCUIT_MAP.put("adelaide",          R.drawable.adelaide_1);
        CIRCUIT_MAP.put("istanbul",          R.drawable.istanbul_1);
        CIRCUIT_MAP.put("turkey",            R.drawable.istanbul_1);
        CIRCUIT_MAP.put("imola",             R.drawable.imola_1);
        CIRCUIT_MAP.put("portimao",          R.drawable.portimao_1);
        CIRCUIT_MAP.put("portugal",          R.drawable.portimao_1);
        CIRCUIT_MAP.put("mugello",           R.drawable.mugello_1);
        CIRCUIT_MAP.put("sochi",             R.drawable.sochi_1);
        CIRCUIT_MAP.put("russia",            R.drawable.sochi_1);
        CIRCUIT_MAP.put("hockenheim",        R.drawable.hockenheimring_1);
        CIRCUIT_MAP.put("germany",           R.drawable.hockenheimring_1);
        CIRCUIT_MAP.put("magny cours",       R.drawable.magny_cours_1);
        CIRCUIT_MAP.put("france",            R.drawable.magny_cours_1);
        CIRCUIT_MAP.put("paul ricard",       R.drawable.paul_ricard_1);
        CIRCUIT_MAP.put("valencia",          R.drawable.valencia_1);
        CIRCUIT_MAP.put("estoril",           R.drawable.estoril_1);
        CIRCUIT_MAP.put("jerez",             R.drawable.jerez_1);
        CIRCUIT_MAP.put("fuji",              R.drawable.fuji_1);
        CIRCUIT_MAP.put("brands hatch",      R.drawable.brands_hatch_1);
        CIRCUIT_MAP.put("donington",         R.drawable.donington_1);
        CIRCUIT_MAP.put("detroit",           R.drawable.detroit_1);
        CIRCUIT_MAP.put("dallas",            R.drawable.dallas_1);
        CIRCUIT_MAP.put("phoenix",           R.drawable.phoenix_1);
        CIRCUIT_MAP.put("indianapolis",      R.drawable.indianapolis_1);
        CIRCUIT_MAP.put("long beach",        R.drawable.long_beach_1);
        CIRCUIT_MAP.put("caesars palace",    R.drawable.caesars_palace_1);
        CIRCUIT_MAP.put("watkins glen",      R.drawable.watkins_glen_1);
        CIRCUIT_MAP.put("mosport",           R.drawable.mosport_1);
        CIRCUIT_MAP.put("mont tremblant",    R.drawable.mont_tremblant_1);
        CIRCUIT_MAP.put("dijon",             R.drawable.dijon_1);
        CIRCUIT_MAP.put("jarama",            R.drawable.jarama_1);
        CIRCUIT_MAP.put("anderstorp",        R.drawable.anderstorp_1);
        CIRCUIT_MAP.put("zolder",            R.drawable.zolder_1);
        CIRCUIT_MAP.put("buenos aires",      R.drawable.buenos_aires_1);
        CIRCUIT_MAP.put("argentina",         R.drawable.buenos_aires_1);
        CIRCUIT_MAP.put("jacarepagua",       R.drawable.jacarepagua_1);
        CIRCUIT_MAP.put("kyalami",           R.drawable.kyalami_1);
        CIRCUIT_MAP.put("east london",       R.drawable.east_london_1);
        CIRCUIT_MAP.put("sebring",           R.drawable.sebring_1);
        CIRCUIT_MAP.put("riverside",         R.drawable.riverside_1);
        CIRCUIT_MAP.put("reims",             R.drawable.reims_1);
        CIRCUIT_MAP.put("rouen",             R.drawable.rouen_1);
        CIRCUIT_MAP.put("clermont ferrand",  R.drawable.clermont_ferrand_1);
        CIRCUIT_MAP.put("bremgarten",        R.drawable.bremgarten_1);
        CIRCUIT_MAP.put("avus",              R.drawable.avus_1);
        CIRCUIT_MAP.put("pedralbes",         R.drawable.pedralbes_1);
        CIRCUIT_MAP.put("ain diab",          R.drawable.ain_diab_1);
        CIRCUIT_MAP.put("monsanto",          R.drawable.monsanto_1);
        CIRCUIT_MAP.put("porto",             R.drawable.porto_1);
        CIRCUIT_MAP.put("montjuic",          R.drawable.montjuic_1);
        CIRCUIT_MAP.put("nivelles",          R.drawable.nivelles_1);
        CIRCUIT_MAP.put("zeltweg",           R.drawable.zeltweg_1);
        CIRCUIT_MAP.put("aintree",           R.drawable.aintree_1);
        CIRCUIT_MAP.put("aida",              R.drawable.aida_1);
        CIRCUIT_MAP.put("yeongam",           R.drawable.yeongam_1);
        CIRCUIT_MAP.put("korea",             R.drawable.yeongam_1);
        CIRCUIT_MAP.put("bugatti",           R.drawable.bugatti_1);
    }

    private CircuitAssets() {}

    /**
     * Returns the drawable resource ID for the given circuit name,
     * or null if no asset is mapped (caller should hide the ImageView).
     */
    public static Integer getCircuitDrawable(String circuitName) {
        if (circuitName == null) return null;
        return CIRCUIT_MAP.get(circuitName.trim().toLowerCase());
    }

    public static boolean hasCircuit(String circuitName) {
        return getCircuitDrawable(circuitName) != null;
    }
}
