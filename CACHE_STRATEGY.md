# F1 App Cache Management Strategy

## Overview
The app now implements a **cache-first, background-refresh** strategy to minimize initial data consumption while ensuring users see data instantly.

## Architecture

### 1. **DataSyncManager** (New)
Orchestrates intelligent data loading with cache-first approach:
- **loadSessions()**: Returns cached sessions immediately, refreshes in background
- **loadStandings()**: Returns cached standings immediately, refreshes in background
- Automatic cache invalidation based on TTL (Time-To-Live)

### 2. **CacheManager** (Enhanced)
Manages SharedPreferences-based caching:
- **saveCache(key, data, ttlMs)**: Store data with custom TTL
- **getCache(key)**: Retrieve data if not expired
- **clearCache(key)**: Remove specific cache entry
- **getCacheSize()**: Monitor cache consumption

### 3. **SplashActivity** (New)
Pre-loads essential data on app startup:
- Loads sessions and standings from cache or API
- Shows progress to user
- Navigates to MainActivity once data is ready
- Minimal initial data consumption

## Data Flow

```
App Start
    ↓
SplashActivity
    ├─ Load Sessions (cache-first)
    │   └─ If cached: return immediately + refresh in background
    │   └─ If not cached: fetch from API + cache
    │
    ├─ Load Standings (cache-first)
    │   └─ If cached: return immediately + refresh in background
    │   └─ If not cached: fetch from API + cache
    │
    └─ Navigate to MainActivity
        ↓
    MainActivity displays cached data instantly
    Background tasks refresh data silently
```

## Cache Configuration

### Session Cache
- **Key**: `sessions_2026`
- **TTL**: 6 hours
- **Storage**: SharedPreferences
- **Size**: ~50-100 KB (typical)

### Standings Cache
- **Key**: `standings_2026`
- **TTL**: 1 hour
- **Storage**: SharedPreferences + SQLite Database
- **Size**: ~30-50 KB (typical)

### Total Initial Cache Size
- **Typical**: 80-150 KB
- **Maximum**: ~200 KB
- **Minimal impact** on app startup

## Integration Steps

### Step 1: Update AndroidManifest.xml
Change the launcher activity from `MainActivity` to `SplashActivity`:

```xml
<activity
    android:name=".SplashActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name=".MainActivity"
    android:exported="true" />
```

### Step 2: Verify Dependencies
Ensure `build.gradle` includes:
```gradle
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}
```

### Step 3: Test Cache Behavior
1. First launch: App fetches from API and caches
2. Second launch (within TTL): App loads from cache instantly
3. Background refresh happens silently
4. After TTL expires: Fresh API fetch on next launch

## Benefits

✅ **Instant Data Display**: Users see cached data immediately
✅ **Minimal Initial Load**: Only essential data cached at startup
✅ **Background Refresh**: Data updates silently without blocking UI
✅ **Reduced Bandwidth**: Reuses cached data within TTL
✅ **Better UX**: No loading screens after first launch
✅ **Offline Support**: Works with cached data when offline

## Monitoring Cache

### Check Cache Size
```java
long cacheSize = CacheManager.getCacheSize(context);
Log.d("Cache", "Size: " + cacheSize + " bytes");
```

### Clear Cache Manually
```java
CacheManager.clearAllCache(context);
```

### Clear Specific Cache
```java
CacheManager.clearCache(context, "sessions_2026");
```

## Future Enhancements

1. **Selective Cache Invalidation**: Clear only stale data
2. **Compression**: Gzip compress cached JSON
3. **Incremental Sync**: Only fetch changed data
4. **Offline Queue**: Queue user actions when offline
5. **Cache Analytics**: Track cache hit/miss rates

## Troubleshooting

### Data Not Updating
- Check TTL values in DataSyncManager
- Verify API endpoints are accessible
- Check SharedPreferences for corrupted data

### High Cache Size
- Reduce TTL values
- Implement cache size limits
- Add compression for large responses

### Slow Initial Load
- Verify network connectivity
- Check API response times
- Consider pre-caching on app install
