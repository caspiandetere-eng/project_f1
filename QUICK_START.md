# Quick Start Guide - Cache Optimization

## 5-Minute Setup

### Step 1: Update AndroidManifest.xml
Find your `AndroidManifest.xml` and change the launcher activity:

**Before:**
```xml
<activity android:name=".MainActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**After:**
```xml
<activity android:name=".SplashActivity" android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity android:name=".MainActivity" android:exported="true" />
```

### Step 2: Verify Dependencies
Check your `build.gradle` (Module: app) has these:

```gradle
dependencies {
    // Existing dependencies...
    
    // Ensure these are present:
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}
```

### Step 3: Build and Run
```bash
# Clean build
./gradlew clean build

# Run the app
./gradlew installDebug
```

## What Happens

### First Launch
1. **SplashActivity** appears
2. App fetches sessions and standings from API
3. Data is cached in SharedPreferences
4. App navigates to **MainActivity**
5. Data displays instantly

### Second Launch (within TTL)
1. **SplashActivity** appears briefly
2. App loads data from cache instantly
3. Background refresh happens silently
4. App navigates to **MainActivity**
5. Data displays instantly

### After TTL Expires
1. Fresh API fetch on next launch
2. Updated data cached
3. Cycle repeats

## Testing Cache Behavior

### Test 1: First Launch (API Fetch)
```
1. Uninstall app
2. Install fresh
3. Launch app
4. Watch SplashActivity load data
5. Check logcat for API calls
```

### Test 2: Second Launch (Cache Hit)
```
1. Close app
2. Relaunch within 6 hours
3. SplashActivity loads instantly
4. Data from cache displays
5. Check logcat for cache hits
```

### Test 3: Monitor Cache
```java
// Add to MainActivity.onCreate() for debugging:
CacheDebugger.logCacheStats(this);
CacheDebugger.logAllCacheEntries(this);
```

## Monitoring Cache

### View Cache Statistics
```java
// In any Activity
CacheDebugger.logCacheStats(this);
```

**Output:**
```
D/CacheDebugger: === CACHE STATISTICS ===
D/CacheDebugger: Total entries: 2
D/CacheDebugger: Total size: 125.45 KB
D/CacheDebugger: ========================
```

### Check Specific Cache Entry
```java
CacheDebugger.logCacheEntry(this, "sessions_2026");
```

**Output:**
```
D/CacheDebugger: === CACHE ENTRY: sessions_2026 ===
D/CacheDebugger: Size: 85.32 KB
D/CacheDebugger: Created: 2024-01-15 10:30:45
D/CacheDebugger: Age: 2 h
D/CacheDebugger: TTL: 6 h
D/CacheDebugger: Remaining: 4 h
D/CacheDebugger: Expired: false
D/CacheDebugger: ============================
```

## Common Issues & Solutions

### Issue: App crashes on SplashActivity
**Solution:** Ensure `activity_splash.xml` exists in `res/layout/`

### Issue: Data not loading
**Solution:** Check network connectivity and API endpoints

### Issue: Cache not working
**Solution:** Run `CacheDebugger.logAllCacheEntries()` to verify cache

### Issue: High cache size
**Solution:** Reduce TTL values in `DataSyncManager`:
```java
private static final long CACHE_VALIDITY_SESSIONS = 3 * 60 * 60 * 1000; // 3 hours
private static final long CACHE_VALIDITY_STANDINGS = 30 * 60 * 1000;    // 30 minutes
```

## Performance Metrics

### Expected Results
- **First launch:** 1-2 seconds
- **Subsequent launches:** <500ms
- **Cache size:** 80-150 KB
- **Memory impact:** Minimal

### Verify Performance
```java
// Add timing to MainActivity.onCreate()
long startTime = System.currentTimeMillis();
// ... load data ...
long loadTime = System.currentTimeMillis() - startTime;
Log.d("Performance", "Load time: " + loadTime + "ms");
```

## Customization

### Adjust Cache TTL
Edit `DataSyncManager.java`:
```java
// Sessions cache: 6 hours
private static final long CACHE_VALIDITY_SESSIONS = 6 * 60 * 60 * 1000;

// Standings cache: 1 hour
private static final long CACHE_VALIDITY_STANDINGS = 60 * 60 * 1000;
```

### Add More Data Types
In `DataSyncManager.java`:
```java
public static void loadSchedule(Context ctx, int year, SyncCallback<JolpicaScheduleResponse> callback) {
    String cached = CacheManager.getCache(ctx, "schedule_2026");
    if (cached != null) {
        // Return cached data
    }
    // Fetch from API
}
```

## Debugging Tips

### Enable Verbose Logging
Add to `DataSyncManager`:
```java
Log.d("DataSync", "Loading from cache: " + key);
Log.d("DataSync", "Refreshing in background: " + key);
```

### Check SharedPreferences
```java
// View all cached data
CacheDebugger.logAllCacheEntries(this);
```

### Clear Cache for Testing
```java
// Clear all cache
CacheManager.clearAllCache(this);

// Clear specific entry
CacheManager.clearCache(this, "sessions_2026");
```

## Next Steps

1. ✅ Update `AndroidManifest.xml`
2. ✅ Verify dependencies
3. ✅ Build and run
4. ✅ Test cache behavior
5. ✅ Monitor with `CacheDebugger`
6. ✅ Adjust TTL as needed

## Support

For detailed information:
- See `CACHE_STRATEGY.md` for architecture
- See `IMPLEMENTATION_CHECKLIST.md` for integration
- See `CACHE_OPTIMIZATION_SUMMARY.md` for overview

---

**You're all set!** Your app now has intelligent cache management. 🎉
