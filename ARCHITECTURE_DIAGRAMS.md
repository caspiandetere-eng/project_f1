# Cache Architecture Diagrams

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         F1 APP                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   SplashActivity                         │  │
│  │  (Pre-loads essential data on startup)                   │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ↓                                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                  DataSyncManager                         │  │
│  │  (Orchestrates cache-first data loading)                │  │
│  │  ├─ loadSessions()                                       │  │
│  │  └─ loadStandings()                                      │  │
│  └──────────────────────────────────────────────────────────┘  │
│         ↓                                    ↓                  │
│  ┌─────────────────┐              ┌──────────────────────┐    │
│  │  CacheManager   │              │  API Clients         │    │
│  │  (SharedPrefs)  │              │  ├─ JolpicaApiClient │    │
│  │  ├─ saveCache() │              │  └─ OpenF1ApiClient  │    │
│  │  ├─ getCache()  │              └──────────────────────┘    │
│  │  └─ clearCache()│                      ↓                    │
│  └─────────────────┘              ┌──────────────────────┐    │
│         ↓                          │   External APIs      │    │
│  ┌─────────────────┐              │  ├─ Jolpica (F1)     │    │
│  │  SQLiteDatabase │              │  └─ OpenF1           │    │
│  │  (Standings)    │              └──────────────────────┘    │
│  └─────────────────┘                                           │
│         ↓                                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                   MainActivity                           │  │
│  │  (Displays cached data instantly)                        │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow - First Launch

```
START
  ↓
SplashActivity.onCreate()
  ↓
Check login status
  ├─ Not logged in → LoginActivity
  └─ Logged in → Continue
  ↓
DataSyncManager.loadSessions()
  ├─ Check cache
  │   └─ Cache miss → Fetch from API
  │       ├─ API call: OpenF1ApiClient.getSessions()
  │       ├─ Parse response
  │       ├─ Save to SharedPreferences
  │       └─ Return data
  └─ Display in SplashActivity
  ↓
DataSyncManager.loadStandings()
  ├─ Check cache
  │   └─ Cache miss → Fetch from API
  │       ├─ API call: JolpicaApiClient.getDriverStandings()
  │       ├─ Parse response
  │       ├─ Save to SharedPreferences
  │       ├─ Save to SQLiteDatabase
  │       └─ Return data
  └─ Display in SplashActivity
  ↓
All data loaded
  ↓
Navigate to MainActivity
  ↓
MainActivity displays cached data
  ↓
Background refresh (silent)
  ├─ Refresh sessions
  └─ Refresh standings
  ↓
END
```

## Data Flow - Subsequent Launch (Cache Hit)

```
START
  ↓
SplashActivity.onCreate()
  ↓
Check login status
  ├─ Not logged in → LoginActivity
  └─ Logged in → Continue
  ↓
DataSyncManager.loadSessions()
  ├─ Check cache
  │   ├─ Cache hit (not expired)
  │   │   ├─ Return cached data immediately ⚡
  │   │   └─ Schedule background refresh
  │   └─ Background refresh (async)
  │       ├─ Fetch from API
  │       ├─ Update cache
  │       └─ No UI update (silent)
  └─ Display in SplashActivity
  ↓
DataSyncManager.loadStandings()
  ├─ Check cache
  │   ├─ Cache hit (not expired)
  │   │   ├─ Return cached data immediately ⚡
  │   │   └─ Schedule background refresh
  │   └─ Background refresh (async)
  │       ├─ Fetch from API
  │       ├─ Update cache
  │       └─ No UI update (silent)
  └─ Display in SplashActivity
  ↓
All data loaded (from cache)
  ↓
Navigate to MainActivity
  ↓
MainActivity displays cached data instantly
  ↓
END
```

## Cache Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│                    CACHE LIFECYCLE                          │
└─────────────────────────────────────────────────────────────┘

1. CREATION
   ├─ API call made
   ├─ Response received
   ├─ Data serialized to JSON
   ├─ Saved to SharedPreferences
   ├─ Timestamp recorded
   └─ TTL set

2. ACTIVE (Valid)
   ├─ Age < TTL
   ├─ Data available for use
   ├─ Cache hit on access
   └─ Background refresh scheduled

3. STALE (Expired)
   ├─ Age >= TTL
   ├─ Data marked invalid
   ├─ Cache miss on access
   └─ Fresh API fetch triggered

4. REFRESH
   ├─ New API call made
   ├─ Response received
   ├─ Old data replaced
   ├─ Timestamp updated
   └─ TTL reset

5. DELETION
   ├─ Manual clear requested
   ├─ Data removed from SharedPreferences
   ├─ Timestamp removed
   └─ TTL removed
```

## Cache Storage Structure

```
SharedPreferences (F1Cache)
├─ sessions_2026 (JSON string)
│  ├─ sessions_2026_time (timestamp)
│  └─ sessions_2026_ttl (TTL in ms)
│
├─ standings_2026 (JSON string)
│  ├─ standings_2026_time (timestamp)
│  └─ standings_2026_ttl (TTL in ms)
│
└─ [Other cache entries...]

SQLiteDatabase (f1_app.db)
├─ drivers table
│  ├─ driver_id (PK)
│  ├─ given_name
│  ├─ family_name
│  ├─ nationality
│  └─ date_of_birth
│
└─ standings table
   ├─ id (PK)
   ├─ season
   ├─ position
   ├─ driver_id (FK)
   ├─ points
   ├─ wins
   ├─ constructor_name
   └─ updated_at (timestamp)
```

## Performance Comparison

```
BEFORE OPTIMIZATION
┌─────────────────────────────────────────┐
│ First Launch:  3-5 seconds (API fetch)  │
│ Second Launch: 3-5 seconds (API fetch)  │
│ Cache Size:    Unstructured, variable   │
│ UX:            Loading screens every    │
│                time                     │
└─────────────────────────────────────────┘

AFTER OPTIMIZATION
┌─────────────────────────────────────────┐
│ First Launch:  1-2 seconds (API + cache)│
│ Second Launch: <500ms (cache hit)       │
│ Cache Size:    80-150 KB (minimal)      │
│ UX:            Instant data display     │
└─────────────────────────────────────────┘

IMPROVEMENT
┌─────────────────────────────────────────┐
│ Speed:         6-10x faster             │
│ Cache:         Structured & minimal     │
│ UX:            Significantly better     │
└─────────────────────────────────────────┘
```

## TTL Timeline

```
SESSIONS CACHE (6 hours)
├─ 0:00 - Created
├─ 3:00 - Valid (50% TTL remaining)
├─ 5:00 - Valid (17% TTL remaining)
├─ 5:59 - Valid (1% TTL remaining)
├─ 6:00 - EXPIRED ❌
└─ 6:01 - Fresh fetch triggered

STANDINGS CACHE (1 hour)
├─ 0:00 - Created
├─ 0:30 - Valid (50% TTL remaining)
├─ 0:59 - Valid (1% TTL remaining)
├─ 1:00 - EXPIRED ❌
└─ 1:01 - Fresh fetch triggered
```

## Component Interaction

```
┌──────────────────────────────────────────────────────────┐
│                   SplashActivity                         │
│  ┌────────────────────────────────────────────────────┐  │
│  │ onCreate()                                         │  │
│  │ ├─ Check login                                     │  │
│  │ └─ preloadEssentialData()                          │  │
│  └────────────────────────────────────────────────────┘  │
│                      ↓                                    │
│  ┌────────────────────────────────────────────────────┐  │
│  │ DataSyncManager                                    │  │
│  │ ├─ loadSessions()                                  │  │
│  │ │  ├─ CacheManager.getCache()                      │  │
│  │ │  ├─ OpenF1ApiClient.getSessions()                │  │
│  │ │  └─ CacheManager.saveCache()                     │  │
│  │ │                                                  │  │
│  │ └─ loadStandings()                                 │  │
│  │    ├─ CacheManager.getCache()                      │  │
│  │    ├─ JolpicaApiClient.getDriverStandings()        │  │
│  │    ├─ CacheManager.saveCache()                     │  │
│  │    └─ UserRepository.saveStandings()               │  │
│  └────────────────────────────────────────────────────┘  │
│                      ↓                                    │
│  ┌────────────────────────────────────────────────────┐  │
│  │ MainActivity                                       │  │
│  │ ├─ onCreate()                                      │  │
│  │ ├─ Display cached data                             │  │
│  │ └─ Background refresh continues                    │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

## Decision Tree - Data Loading

```
                    Need Data?
                        ↓
                  Check Cache
                    ↙     ↘
              Cache Hit   Cache Miss
                ↓             ↓
            Valid?        Fetch from
              ↓           API
            YES            ↓
              ↓         Parse &
          Return        Validate
          Cached          ↓
          Data ⚡      Save to
              ↓         Cache
          Schedule        ↓
          Background    Return
          Refresh       Data
              ↓             ↓
          (Silent)    Display to
                      User
```

---

These diagrams illustrate how the cache optimization system works to provide instant data display while minimizing cache consumption.
