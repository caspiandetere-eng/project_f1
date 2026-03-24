# Favorite Team & Driver Selection - Quick Implementation Guide

## 🚀 Quick Start

### Step 1: Verify Dependencies
Ensure your `build.gradle` includes:
```gradle
dependencies {
    // Glide for image loading
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.15.1'
    
    // Material Design
    implementation 'com.google.android.material:material:1.9.0'
    
    // RecyclerView
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    
    // Retrofit & Gson (should already exist)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

### Step 2: Update AndroidManifest.xml
Add the new activity:
```xml
<activity
    android:name=".FavoriteSelectionActivity"
    android:exported="false" />
```

### Step 3: Build and Test
```bash
./gradlew clean build
./gradlew installDebug
```

## 📋 Files Created

| File | Purpose |
|------|---------|
| FavoriteRepository.java | Manage favorite selections |
| Team.java | Team model with data |
| TeamSelectionAdapter.java | Team card adapter |
| DriverSelectionAdapter.java | Driver card adapter |
| FavoriteSelectionActivity.java | Selection UI |
| item_team_card.xml | Team card layout |
| item_driver_card.xml | Driver card layout |
| activity_favorite_selection.xml | Activity layout |

## 🔧 Files Modified

| File | Changes |
|------|---------|
| OnboardingActivity.java | Added favorite selection check |
| SettingsActivity.java | Added change favorites button |
| activity_settings.xml | Added favorites section |

## 🎯 How It Works

### Onboarding Flow
```
User completes onboarding
    ↓
Check: Has favorites?
    ├─ Yes → Go to MainActivity
    └─ No → Show FavoriteSelectionActivity
    ↓
User selects team and driver
    ↓
Save to SharedPreferences
    ↓
Go to MainActivity
```

### Settings Flow
```
User opens Settings
    ↓
Click "Change Team & Driver"
    ↓
Show FavoriteSelectionActivity
    ↓
User selects new team/driver
    ↓
Update SharedPreferences
    ↓
Return to Settings
```

## 💾 Data Storage

### SharedPreferences (F1Favorites)
```
favorite_team: "red_bull"
favorite_team_name: "Red Bull Racing"
favorite_driver: "hamilton"
favorite_driver_name: "hamilton"
```

### Cache (F1Cache)
```
drivers_2026: [JSON array of drivers]
drivers_2026_time: [timestamp]
drivers_2026_ttl: [24 hours in ms]
```

## 🎨 UI Features

### Team Cards
- 2-column grid layout
- Team logo (80x80dp)
- Team name
- Team color background
- Selection indicator (checkmark)
- Red border when selected

### Driver Cards
- 2-column grid layout
- Driver photo (120x120dp)
- Driver name
- Nationality
- Loading progress bar
- Selection indicator (checkmark)
- Red border when selected

### Activity
- Header with back button
- Scrollable content
- Team selection section
- Driver selection section
- Continue button (disabled until both selected)

## 🔌 Integration Points

### Access Favorites Anywhere
```java
// Check if user has favorites
if (FavoriteRepository.hasBothFavorites(context)) {
    String teamId = FavoriteRepository.getFavoriteTeamId(context);
    String driverId = FavoriteRepository.getFavoriteDriverId(context);
}

// Get favorite names
String teamName = FavoriteRepository.getFavoriteTeamName(context);
String driverName = FavoriteRepository.getFavoriteDriverName(context);
```

### Display in MainActivity
```java
// In MainActivity.onCreate()
if (FavoriteRepository.hasBothFavorites(this)) {
    String teamName = FavoriteRepository.getFavoriteTeamName(this);
    String driverName = FavoriteRepository.getFavoriteDriverName(this);
    // Display in UI
}
```

## 📊 Teams Available

**2026 F1 Season (10 Teams):**
1. McLaren (#FF8700)
2. Ferrari (#DC0000)
3. Red Bull Racing (#0600EF)
4. Mercedes-AMG (#00D2BE)
5. Aston Martin (#006F62)
6. Alpine (#0082FA)
7. Haas F1 Team (#FFFFFF)
8. Williams (#005AFF)
9. Kick Sauber (#00D2BE)
10. Racing Bulls (#0082FA)

## 🌐 Driver Data Source

**API:** Jolpica (Ergast F1 API)
**Endpoint:** `/2026/driverStandings.json`
**Cache:** 24 hours
**Photos:** Formula1.com CDN

## ⚙️ Configuration

### Change Cache Duration
In `FavoriteSelectionActivity.java`:
```java
// Change from 24 hours to 6 hours
CacheManager.saveCache(this, "drivers_2026", json, 6 * 60 * 60 * 1000);
```

### Add More Teams
In `Team.java`:
```java
teams.add(new Team("team_id", "Team Name", "#HEXCOLOR", R.drawable.logo));
```

### Change Driver Photo URL
In `DriverSelectionAdapter.java`:
```java
String photoUrl = "https://your-custom-url/" + driver.driverId.toLowerCase() + ".png";
```

## 🧪 Testing

### Test Onboarding Flow
1. Uninstall app
2. Install fresh
3. Complete onboarding
4. Verify favorite selection appears
5. Select team and driver
6. Verify saved in SharedPreferences

### Test Settings Flow
1. Open Settings
2. Click "Change Team & Driver"
3. Select different team/driver
4. Verify update in SharedPreferences

### Test Offline Mode
1. Enable airplane mode
2. Open favorite selection
3. Verify cached drivers display
4. Disable airplane mode
5. Verify fresh data loads

## 🐛 Troubleshooting

### Driver Photos Not Loading
- Check internet connection
- Verify Glide is properly configured
- Check Formula1.com CDN availability
- Check logcat for Glide errors

### Favorites Not Saving
- Verify SharedPreferences permissions
- Check FavoriteRepository methods
- Check logcat for exceptions

### Slow Loading
- Check API response time
- Verify cache is working
- Monitor network connectivity

## 📱 User Experience

### First Time User
1. Completes onboarding
2. Sees favorite selection screen
3. Selects team (instant)
4. Selects driver (1-3 seconds)
5. Clicks continue
6. Goes to MainActivity

### Returning User
1. Opens app
2. Skips onboarding (favorites already selected)
3. Goes directly to MainActivity

### Changing Favorites
1. Opens Settings
2. Clicks "Change Team & Driver"
3. Selects new team/driver
4. Clicks continue
5. Returns to Settings

## 🎯 Next Steps

1. ✅ Build and test the feature
2. ✅ Verify team logos display correctly
3. ✅ Verify driver photos load
4. ✅ Test onboarding flow
5. ✅ Test settings flow
6. ✅ Test offline mode
7. ✅ Monitor cache performance

## 📞 Support

For issues or questions:
- Check FAVORITE_SELECTION_FEATURE.md for detailed documentation
- Review code comments in each file
- Check logcat for error messages
- Verify all dependencies are installed

---

**Ready to implement!** 🏁
