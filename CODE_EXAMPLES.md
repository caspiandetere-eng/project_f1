# Code Examples & Usage Patterns

## Basic Usage

### Load Sessions with Cache-First Strategy
```java
DataSyncManager.loadSessions(this, 2026, new DataSyncManager.SyncCallback<List<OpenF1Session>>() {
    @Override
    public void onSuccess(List<OpenF1Session> sessions) {
        // Data is either from cache or freshly fetched
        for (OpenF1Session session : sessions) {
            Log.d("Session", session.circuitShortName);
        }
    }

    @Override
    public void onFailure(String error) {
        Log.e("Session", "Failed to load: " + error);
    }
});
```

### Load Standings with Cache-First Strategy
```java
DataSyncManager.loadStandings(this, 2026, new DataSyncManager.SyncCallback<JolpicaStandingsResponse>() {
    @Override
    public void onSuccess(JolpicaStandingsResponse response) {
        // Data is either from cache or freshly fetched
        List<UserRepository.StandingRow> standings = UserRepository.getStandings(this, 2026);
        updateUI(standings);
    }

    @Override
    public void onFailure(String error) {
        Log.e("Standings", "Failed to load: " + error);
    }
});
```

## Cache Management

### Save Data to Cache
```java
// Save with default TTL (24 hours)
CacheManager.saveCache(context, "my_key", jsonData);

// Save with custom TTL (6 hours)
CacheManager.saveCache(context, "my_key", jsonData, 6 * 60 * 60 * 1000);

// Save with short TTL (30 minutes)
CacheManager.saveCache(context, "my_key", jsonData, 30 * 60 * 1000);
```

### Retrieve Data from Cache
```java
String cachedData = CacheManager.getCache(context, "my_key");
if (cachedData != null) {
    // Data is valid and not expired
    MyObject obj = new Gson().fromJson(cachedData, MyObject.class);
} else {
    // Cache miss or expired
    fetchFromApi();
}
```

### Clear Cache
```java
// Clear all cache
CacheManager.clearAllCache(context);

// Clear specific entry
CacheManager.clearCache(context, "sessions_2026");

// Clear multiple entries
CacheManager.clearCache(context, "sessions_2026");
CacheManager.clearCache(context, "standings_2026");
```

### Monitor Cache Size
```java
long cacheSize = CacheManager.getCacheSize(context);
Log.d("Cache", "Total size: " + (cacheSize / 1024) + " KB");

if (cacheSize > 500 * 1024) { // 500 KB
    Log.w("Cache", "Cache size exceeds limit!");
    CacheManager.clearAllCache(context);
}
```

## Debugging & Monitoring

### Log Cache Statistics
```java
// In MainActivity.onCreate() or any Activity
CacheDebugger.logCacheStats(this);

// Output:
// D/CacheDebugger: === CACHE STATISTICS ===
// D/CacheDebugger: Total entries: 2
// D/CacheDebugger: Total size: 125.45 KB
// D/CacheDebugger: ========================
```

### Log All Cache Entries
```java
CacheDebugger.logAllCacheEntries(this);

// Output:
// D/CacheDebugger: === ALL CACHE ENTRIES ===
// D/CacheDebugger: === CACHE ENTRY: sessions_2026 ===
// D/CacheDebugger: Size: 85.32 KB
// D/CacheDebugger: Created: 2024-01-15 10:30:45
// D/CacheDebugger: Age: 2 h
// D/CacheDebugger: TTL: 6 h
// D/CacheDebugger: Remaining: 4 h
// D/CacheDebugger: Expired: false
// D/CacheDebugger: ============================
```

### Log Specific Cache Entry
```java
CacheDebugger.logCacheEntry(this, "sessions_2026");

// Output:
// D/CacheDebugger: === CACHE ENTRY: sessions_2026 ===
// D/CacheDebugger: Size: 85.32 KB
// D/CacheDebugger: Created: 2024-01-15 10:30:45
// D/CacheDebugger: Age: 2 h
// D/CacheDebugger: TTL: 6 h
// D/CacheDebugger: Remaining: 4 h
// D/CacheDebugger: Expired: false
// D/CacheDebugger: ============================
```

### Check Cache Validity
```java
boolean isValid = CacheDebugger.isCacheValid(this, "sessions_2026");
if (isValid) {
    Log.d("Cache", "Cache is valid");
} else {
    Log.d("Cache", "Cache is expired or missing");
}
```

### Get Cache Age
```java
long ageMs = CacheDebugger.getCacheAge(this, "sessions_2026");
if (ageMs > 0) {
    long ageHours = ageMs / (60 * 60 * 1000);
    Log.d("Cache", "Cache age: " + ageHours + " hours");
}
```

### Get Remaining TTL
```java
long remainingMs = CacheDebugger.getRemainingTtl(this, "sessions_2026");
if (remainingMs > 0) {
    long remainingHours = remainingMs / (60 * 60 * 1000);
    Log.d("Cache", "Cache expires in: " + remainingHours + " hours");
}
```

### Simulate Cache Access
```java
CacheDebugger.simulateCacheAccess(this, "sessions_2026");

// Output:
// D/CacheDebugger: === CACHE ACCESS: sessions_2026 ===
// D/CacheDebugger: Status: HIT
// D/CacheDebugger: Age: 2 h
// D/CacheDebugger: Remaining TTL: 4 h
// D/CacheDebugger: ============================
```

## Advanced Patterns

### Conditional Cache Loading
```java
public void loadDataIfNeeded() {
    // Check if cache is still valid
    if (CacheDebugger.isCacheValid(this, "sessions_2026")) {
        // Use cached data
        String cached = CacheManager.getCache(this, "sessions_2026");
        List<OpenF1Session> sessions = new Gson().fromJson(cached,
                com.google.gson.reflect.TypeToken.getParameterized(List.class, OpenF1Session.class).getType());
        updateUI(sessions);
    } else {
        // Fetch fresh data
        DataSyncManager.loadSessions(this, 2026, callback);
    }
}
```

### Cache with Fallback
```java
public void loadWithFallback() {
    DataSyncManager.loadSessions(this, 2026, new DataSyncManager.SyncCallback<List<OpenF1Session>>() {
        @Override
        public void onSuccess(List<OpenF1Session> sessions) {
            updateUI(sessions);
        }

        @Override
        public void onFailure(String error) {
            // Try to use stale cache as fallback
            String staleCache = CacheManager.getCache(this, "sessions_2026");
            if (staleCache != null) {
                Log.w("Cache", "Using stale cache as fallback");
                List<OpenF1Session> sessions = new Gson().fromJson(staleCache,
                        com.google.gson.reflect.TypeToken.getParameterized(List.class, OpenF1Session.class).getType());
                updateUI(sessions);
            } else {
                showError("No data available");
            }
        }
    });
}
```

### Batch Cache Operations
```java
public void preloadAllData() {
    AtomicInteger completed = new AtomicInteger(0);
    int total = 2;

    DataSyncManager.loadSessions(this, 2026, new DataSyncManager.SyncCallback<List<OpenF1Session>>() {
        @Override
        public void onSuccess(List<OpenF1Session> data) {
            if (completed.incrementAndGet() == total) {
                onAllDataLoaded();
            }
        }

        @Override
        public void onFailure(String error) {
            if (completed.incrementAndGet() == total) {
                onAllDataLoaded();
            }
        }
    });

    DataSyncManager.loadStandings(this, 2026, new DataSyncManager.SyncCallback<JolpicaStandingsResponse>() {
        @Override
        public void onSuccess(JolpicaStandingsResponse data) {
            if (completed.incrementAndGet() == total) {
                onAllDataLoaded();
            }
        }

        @Override
        public void onFailure(String error) {
            if (completed.incrementAndGet() == total) {
                onAllDataLoaded();
            }
        }
    });
}

private void onAllDataLoaded() {
    Log.d("Cache", "All data loaded");
    navigateToMainActivity();
}
```

### Cache Expiration Monitoring
```java
public void monitorCacheExpiration() {
    long remainingMs = CacheDebugger.getRemainingTtl(this, "sessions_2026");
    
    if (remainingMs < 60 * 60 * 1000) { // Less than 1 hour
        Log.w("Cache", "Sessions cache expiring soon");
        // Optionally refresh early
        DataSyncManager.loadSessions(this, 2026, callback);
    }
}
```

### Custom Cache TTL
```java
// Save with very short TTL (5 minutes) for real-time data
CacheManager.saveCache(this, "live_data", jsonData, 5 * 60 * 1000);

// Save with long TTL (24 hours) for static data
CacheManager.saveCache(this, "static_data", jsonData, 24 * 60 * 60 * 1000);
```

## Integration Examples

### In SplashActivity
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    preloadEssentialData();
}

private void preloadEssentialData() {
    AtomicInteger tasksCompleted = new AtomicInteger(0);

    DataSyncManager.loadSessions(this, 2026, new DataSyncManager.SyncCallback<List<OpenF1Session>>() {
        @Override
        public void onSuccess(List<OpenF1Session> data) {
            if (tasksCompleted.incrementAndGet() >= 2) {
                navigateToMainActivity();
            }
        }

        @Override
        public void onFailure(String error) {
            if (tasksCompleted.incrementAndGet() >= 2) {
                navigateToMainActivity();
            }
        }
    });

    DataSyncManager.loadStandings(this, 2026, new DataSyncManager.SyncCallback<JolpicaStandingsResponse>() {
        @Override
        public void onSuccess(JolpicaStandingsResponse data) {
            if (tasksCompleted.incrementAndGet() >= 2) {
                navigateToMainActivity();
            }
        }

        @Override
        public void onFailure(String error) {
            if (tasksCompleted.incrementAndGet() >= 2) {
                navigateToMainActivity();
            }
        }
    });
}
```

### In MainActivity
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Data is already cached from SplashActivity
    loadCachedData();
}

private void loadCachedData() {
    // Load sessions from cache
    String sessionCache = CacheManager.getCache(this, "sessions_2026");
    if (sessionCache != null) {
        List<OpenF1Session> sessions = new Gson().fromJson(sessionCache,
                com.google.gson.reflect.TypeToken.getParameterized(List.class, OpenF1Session.class).getType());
        displaySessions(sessions);
    }

    // Load standings from database
    List<UserRepository.StandingRow> standings = UserRepository.getStandings(this, 2026);
    displayStandings(standings);
}
```

## Testing Patterns

### Unit Test Cache
```java
@Test
public void testCacheSaveAndRetrieve() {
    String key = "test_key";
    String data = "test_data";
    
    CacheManager.saveCache(context, key, data);
    String retrieved = CacheManager.getCache(context, key);
    
    assertEquals(data, retrieved);
}
```

### Test Cache Expiration
```java
@Test
public void testCacheExpiration() {
    String key = "test_key";
    String data = "test_data";
    long ttl = 100; // 100ms
    
    CacheManager.saveCache(context, key, data, ttl);
    String retrieved1 = CacheManager.getCache(context, key);
    assertEquals(data, retrieved1);
    
    Thread.sleep(150); // Wait for expiration
    String retrieved2 = CacheManager.getCache(context, key);
    assertNull(retrieved2);
}
```

---

These examples cover common use cases and patterns for working with the cache optimization system.
