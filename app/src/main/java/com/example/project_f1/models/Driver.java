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

    public Driver(String id, String apiId, String firstName, String lastName, String teamId, String teamName, String nationality, int photoResId) {
        this.id = id;
        this.apiId = apiId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamId = teamId;
        this.teamName = teamName;
        this.nationality = nationality;
        this.photoResId = photoResId;
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

        // Mercedes
        drivers.add(new Driver("russell",    "george_russell",    "George",   "Russell",    "mercedes",     "Mercedes-AMG",            "British",      R.drawable.george_russell));
        drivers.add(new Driver("antonelli",  "andrea_kimi_antonelli", "Kimi", "Antonelli",  "mercedes",     "Mercedes-AMG",            "Italian",      R.drawable.kimi_antonelli));

        // Ferrari
        drivers.add(new Driver("leclerc",    "leclerc",           "Charles",  "Leclerc",    "ferrari",      "Ferrari",                 "Monegasque",   R.drawable.charles_leclerc));
        drivers.add(new Driver("hamilton",   "hamilton",          "Lewis",    "Hamilton",   "ferrari",      "Ferrari",                 "British",      R.drawable.lewis_hamilton));

        // McLaren
        drivers.add(new Driver("norris",     "norris",            "Lando",    "Norris",     "mclaren",      "McLaren",                 "British",      R.drawable.lando_norris));
        drivers.add(new Driver("piastri",    "piastri",           "Oscar",    "Piastri",    "mclaren",      "McLaren",                 "Australian",   R.drawable.oscar_piastri));

        // Red Bull Racing
        drivers.add(new Driver("verstappen", "max_verstappen",    "Max",      "Verstappen", "red_bull",     "Red Bull Racing",         "Dutch",        R.drawable.max_verstappen));
        drivers.add(new Driver("hadjar",     "isack_hadjar",      "Isack",    "Hadjar",     "red_bull",     "Red Bull Racing",         "French",       R.drawable.isack_hadjar));

        // Aston Martin
        drivers.add(new Driver("alonso",     "alonso",            "Fernando", "Alonso",     "aston_martin", "Aston Martin",            "Spanish",      R.drawable.fernando_alonso));
        drivers.add(new Driver("stroll",     "stroll",            "Lance",    "Stroll",     "aston_martin", "Aston Martin",            "Canadian",     R.drawable.lance_stroll));

        // Audi
        drivers.add(new Driver("hulkenberg", "hulkenberg",        "Nico",     "Hulkenberg", "audi",         "Audi",                    "German",       R.drawable.nico_hulkenberg));
        drivers.add(new Driver("bortoleto",  "gabriel_bortoleto", "Gabriel",  "Bortoleto",  "audi",         "Audi",                    "Brazilian",    R.drawable.gabriel_bortoleto));

        // Cadillac
        drivers.add(new Driver("perez",      "perez",             "Sergio",   "Perez",      "cadillac",     "Cadillac Formula 1 Team", "Mexican",      R.drawable.sergio_perez));
        drivers.add(new Driver("bottas",     "bottas",            "Valtteri", "Bottas",     "cadillac",     "Cadillac Formula 1 Team", "Finnish",      R.drawable.valtteri_bottas));

        // Alpine
        drivers.add(new Driver("gasly",      "gasly",             "Pierre",   "Gasly",      "alpine",       "Alpine",                  "French",       R.drawable.pierre_gasly));
        drivers.add(new Driver("colapinto",  "colapinto",         "Franco",   "Colapinto",  "alpine",       "Alpine",                  "Argentine",    R.drawable.franco_colapinto));

        // Williams
        drivers.add(new Driver("sainz",      "sainz",             "Carlos",   "Sainz",      "williams",     "Williams",                "Spanish",      R.drawable.carlos_sainz));
        drivers.add(new Driver("albon",      "albon",             "Alexander","Albon",      "williams",     "Williams",                "Thai",         R.drawable.alex_albon));

        // Racing Bulls
        drivers.add(new Driver("lawson",     "lawson",            "Liam",     "Lawson",     "rb",           "Racing Bulls",            "New Zealander",R.drawable.liam_lawson));
        drivers.add(new Driver("lindblad",   "lindblad",          "Arvid",    "Lindblad",   "rb",           "Racing Bulls",            "Swedish",      R.drawable.arvid_lindblad));

        // Haas
        drivers.add(new Driver("ocon",       "ocon",              "Esteban",  "Ocon",       "haas",         "Haas F1 Team",            "French",       R.drawable.esteban_ocon));
        drivers.add(new Driver("bearman",    "bearman",           "Oliver",   "Bearman",    "haas",         "Haas F1 Team",            "British",      R.drawable.ollie_bearman));

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
