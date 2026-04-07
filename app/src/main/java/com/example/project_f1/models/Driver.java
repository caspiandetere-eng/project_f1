package com.example.project_f1.models;

import com.example.project_f1.R;
import java.util.ArrayList;
import java.util.List;

public class Driver {
    public String id;
    public String apiId;   // Ergast/Jolpica driver ID (e.g. "max_verstappen")
    public String firstName;
    public String lastName;
    public String teamId;
    public String teamName;
    public String nationality;
    public int photoResId;
    public int number;

    public Driver(String id, String apiId, String firstName, String lastName, String teamId, String teamName, String nationality, int photoResId, int number) {
        this.id = id;
        this.apiId = apiId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.nationality = nationality;
        this.photoResId = photoResId;
        this.number = number;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPhotoUrl() {
        // Use the official F1 media URL pattern with the driver's API ID
        return "https://media.formula1.com/image/upload/f_auto,c_thumb,w_500,ar_1:1/" + apiId + ".png";
    }

    public static List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();

        // McLaren
        drivers.add(new Driver("norris",     "norris",            "Lando",    "Norris",     "mclaren",      "McLaren",                 "British",      R.drawable.lando_norris,     1));
        drivers.add(new Driver("piastri",    "piastri",           "Oscar",    "Piastri",    "mclaren",      "McLaren",                 "Australian",   R.drawable.oscar_piastri,    81));

        // Red Bull Racing
        drivers.add(new Driver("verstappen", "max_verstappen",    "Max",      "Verstappen", "red_bull",     "Red Bull Racing",         "Dutch",        R.drawable.max_verstappen,   3));
        drivers.add(new Driver("perez",      "perez",             "Sergio",   "Perez",      "red_bull",     "Red Bull Racing",         "Mexican",      R.drawable.sergio_perez,     11));

        // Ferrari
        drivers.add(new Driver("hamilton",   "hamilton",          "Lewis",    "Hamilton",   "ferrari",      "Ferrari",                 "British",      R.drawable.lewis_hamilton,   44));
        drivers.add(new Driver("leclerc",    "leclerc",           "Charles",  "Leclerc",    "ferrari",      "Ferrari",                 "Monegasque",   R.drawable.charles_leclerc,  16));

        // Mercedes
        drivers.add(new Driver("russell",    "george_russell",    "George",   "Russell",    "mercedes",     "Mercedes-AMG",            "British",      R.drawable.george_russell,   63));
        drivers.add(new Driver("antonelli",  "antonelli",         "Kimi",     "Antonelli",  "mercedes",     "Mercedes-AMG",            "Italian",      R.drawable.kimi_antonelli,   12));

        // Racing Bulls
        drivers.add(new Driver("lawson",     "lawson",            "Liam",     "Lawson",     "rb",           "Racing Bulls",            "New Zealander",R.drawable.liam_lawson,      30));
        drivers.add(new Driver("lindblad",   "lindblad",          "Arvid",    "Lindblad",   "rb",           "Racing Bulls",            "Swedish",      R.drawable.arvid_lindblad,   41));

        // Alpine
        drivers.add(new Driver("gasly",      "gasly",             "Pierre",   "Gasly",      "alpine",       "Alpine",                  "French",       R.drawable.pierre_gasly,     10));
        drivers.add(new Driver("colapinto",  "colapinto",         "Franco",   "Colapinto",  "alpine",       "Alpine",                  "Argentine",    R.drawable.franco_colapinto, 43));

        // Audi
        drivers.add(new Driver("bortoleto",  "gabriel_bortoleto", "Gabriel",  "Bortoleto",  "audi",         "Audi",                    "Brazilian",    R.drawable.gabriel_bortoleto,5));
        drivers.add(new Driver("hulkenberg", "hulkenberg",        "Nico",     "Hulkenberg", "audi",         "Audi",                    "German",       R.drawable.nico_hulkenberg,  27));

        // Haas
        drivers.add(new Driver("ocon",       "ocon",              "Esteban",  "Ocon",       "haas",         "Haas F1 Team",            "French",       R.drawable.esteban_ocon,     31));
        drivers.add(new Driver("bearman",    "bearman",           "Oliver",   "Bearman",    "haas",         "Haas F1 Team",            "British",      R.drawable.ollie_bearman,    87));

        // Cadillac
        drivers.add(new Driver("bottas",     "bottas",            "Valtteri", "Bottas",     "cadillac",     "Cadillac Formula 1 Team", "Finnish",      R.drawable.valtteri_bottas,  77));
        drivers.add(new Driver("hadjar",     "isack_hadjar",      "Isack",    "Hadjar",     "cadillac",     "Cadillac Formula 1 Team", "French",       R.drawable.isack_hadjar,     6));

        // Aston Martin
        drivers.add(new Driver("alonso",     "alonso",            "Fernando", "Alonso",     "aston_martin", "Aston Martin",            "Spanish",      R.drawable.fernando_alonso,  14));
        drivers.add(new Driver("stroll",     "stroll",            "Lance",    "Stroll",     "aston_martin", "Aston Martin",            "Canadian",     R.drawable.lance_stroll,     18));

        // Williams
        drivers.add(new Driver("sainz",      "sainz",             "Carlos",   "Sainz",      "williams",     "Williams",                "Spanish",      R.drawable.carlos_sainz,     55));
        drivers.add(new Driver("albon",      "albon",             "Alexander","Albon",      "williams",     "Williams",                "Thai",         R.drawable.alex_albon,       23));

        return drivers;
    }

    public static Driver getDriverById(String id) {
        for (Driver driver : getAllDrivers()) {
            if (driver.id.equals(id)) {
                return driver;
            }
        }
        return null;
    }

    public static List<Driver> getDriversByTeam(String teamId) {
        List<Driver> teamDrivers = new ArrayList<>();
        for (Driver driver : getAllDrivers()) {
            if (driver.teamId.equals(teamId)) {
                teamDrivers.add(driver);
            }
        }
        return teamDrivers;
    }
}
