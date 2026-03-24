# Favorite Team & Driver Selection - Visual Guide

## 🎨 User Flow

### First-Time User (Onboarding)
```
┌─────────────────────────────────────────┐
│         Splash Screen                   │
│    (Pre-loads data)                     │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│      Login/Sign Up                      │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│    Onboarding (F1 Rules)                │
│    ├─ What is F1?                       │
│    ├─ Global Championship               │
│    ├─ How a Race Works                  │
│    ├─ Pit Stops                         │
│    ├─ Two Championships                 │
│    └─ Race Flags                        │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│  Favorite Selection Activity            │
│  ┌─────────────────────────────────────┐│
│  │ 🏁 Choose Your Favorite Team        ││
│  │                                     ││
│  │  ┌──────────┐  ┌──────────┐        ││
│  │  │ Red Bull │  │Mercedes  │        ││
│  │  │  Logo    │  │  Logo    │        ││
│  │  └──────────┘  └──────────┘        ││
│  │                                     ││
│  │  ┌──────────┐  ┌──────────┐        ││
│  │  │ Ferrari  │  │ McLaren  │        ││
│  │  │  Logo    │  │  Logo    │        ││
│  │  └──────────┘  └──────────┘        ││
│  │                                     ││
│  │  ... (more teams)                   ││
│  └─────────────────────────────────────┘│
│                                         │
│  ┌─────────────────────────────────────┐│
│  │ 🏎️ Choose Your Favorite Driver     ││
│  │                                     ││
│  │  ┌──────────┐  ┌──────────┐        ││
│  │  │ Driver 1 │  │ Driver 2 │        ││
│  │  │  Photo   │  │  Photo   │        ││
│  │  │ Name     │  │ Name     │        ││
│  │  └──────────┘  └──────────┘        ││
│  │                                     ││
│  │  ┌──────────┐  ┌──────────┐        ││
│  │  │ Driver 3 │  │ Driver 4 │        ││
│  │  │  Photo   │  │  Photo   │        ││
│  │  │ Name     │  │ Name     │        ││
│  │  └──────────┘  └──────────┘        ││
│  │                                     ││
│  │  ... (more drivers)                 ││
│  │                                     ││
│  │  [Continue Button]                  ││
│  └─────────────────────────────────────┘│
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│         Main Activity                   │
│    (Shows selected favorites)           │
└─────────────────────────────────────────┘
```

### Returning User (Settings)
```
┌─────────────────────────────────────────┐
│         Main Activity                   │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│         Settings Activity               │
│  ┌─────────────────────────────────────┐│
│  │ ACCOUNT                             ││
│  │ [Logout Button]                     ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ FAVORITES                           ││
│  │ [Change Team & Driver Button]       ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ DISPLAY                             ││
│  │ Dark Theme [Toggle]                 ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │ STORAGE                             ││
│  │ Cache Usage: 125.45 KB              ││
│  │ [Progress Bar]                      ││
│  │ [Clear Cache Button]                ││
│  └─────────────────────────────────────┘│
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│  Favorite Selection Activity            │
│  (Same as onboarding)                   │
└──────────────┬──────────────────────────┘
               ↓
┌─────────────────────────────────────────┐
│         Settings Activity               │
│    (Updated favorites)                  │
└─────────────────────────────────────────┘
```

## 🎴 UI Components

### Team Card
```
┌─────────────────────────────┐
│                             │
│      [Team Logo]            │
│      (80x80dp)              │
│                             │
│    Red Bull Racing          │
│                             │
│      [✓ Checkmark]          │
│      (if selected)          │
│                             │
└─────────────────────────────┘
```

### Driver Card
```
┌─────────────────────────────┐
│                             │
│    [Driver Photo]           │
│    (120x120dp)              │
│    [Loading Progress]       │
│                             │
│    Lewis Hamilton           │
│    British                  │
│                             │
│      [✓ Checkmark]          │
│      (if selected)          │
│                             │
└─────────────────────────────┘
```

### Activity Layout
```
┌─────────────────────────────────────────┐
│ ← Select Your Favorites                 │
├─────────────────────────────────────────┤
│                                         │
│ 🏁 Choose Your Favorite Team            │
│                                         │
│ ┌──────────────┐  ┌──────────────┐    │
│ │ Team Card 1  │  │ Team Card 2  │    │
│ └──────────────┘  └──────────────┘    │
│                                         │
│ ┌──────────────┐  ┌──────────────┐    │
│ │ Team Card 3  │  │ Team Card 4  │    │
│ └──────────────┘  └──────────────┘    │
│                                         │
│ ... (more teams in grid)                │
│                                         │
│ 🏎️ Choose Your Favorite Driver         │
│                                         │
│ ┌──────────────┐  ┌──────────────┐    │
│ │Driver Card 1 │  │Driver Card 2 │    │
│ └──────────────┘  └──────────────┘    │
│                                         │
│ ┌──────────────┐  ┌──────────────┐    │
│ │Driver Card 3 │  │Driver Card 4 │    │
│ └──────────────┘  └──────────────┘    │
│                                         │
│ ... (more drivers in grid)              │
│                                         │
├─────────────────────────────────────────┤
│ [Continue Button]                       │
│ (Disabled until both selected)          │
└─────────────────────────────────────────┘
```

## 🎯 Selection States

### Team Card States
```
UNSELECTED                  SELECTED
┌──────────────┐           ┌──────────────┐
│              │           │ ┌──────────┐ │
│ [Team Logo]  │           │ │Team Logo │ │
│              │           │ └──────────┘ │
│ Team Name    │           │ Team Name    │
│              │           │              │
│              │           │    [✓]       │
└──────────────┘           └──────────────┘
(Light background)         (Red border)
                          (Checkmark visible)
```

### Driver Card States
```
LOADING                     LOADED
┌──────────────┐           ┌──────────────┐
│              │           │              │
│ [Progress]   │           │ [Photo]      │
│              │           │              │
│ Driver Name  │           │ Driver Name  │
│ Nationality  │           │ Nationality  │
│              │           │              │
└──────────────┘           └──────────────┘

SELECTED
┌──────────────┐
│ ┌──────────┐ │
│ │ [Photo]  │ │
│ └──────────┘ │
│ Driver Name  │
│ Nationality  │
│    [✓]       │
└──────────────┘
(Red border)
(Checkmark visible)
```

## 📊 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────┐
│                  FavoriteSelectionActivity              │
└──────────────────────┬──────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        ↓                             ↓
┌──────────────────┐        ┌──────────────────┐
│ Team Selection   │        │ Driver Selection │
├──────────────────┤        ├──────────────────┤
│ Source: Hardcoded│        │ Source: API      │
│ Load: <100ms     │        │ Load: 1-3s       │
│ Cache: None      │        │ Cache: 24h       │
└────────┬─────────┘        └────────┬─────────┘
         │                           │
         └──────────────┬────────────┘
                        ↓
            ┌──────────────────────┐
            │ FavoriteRepository   │
            ├──────────────────────┤
            │ SharedPreferences    │
            │ (F1Favorites)        │
            └──────────────────────┘
                        ↓
            ┌──────────────────────┐
            │ MainActivity         │
            │ (Display Favorites)  │
            └──────────────────────┘
```

## 🔄 Caching Strategy

```
FIRST LOAD
┌─────────────────────────────────────────┐
│ FavoriteSelectionActivity               │
│ ├─ Check cache for drivers              │
│ │  └─ Cache miss                        │
│ ├─ Fetch from API                       │
│ │  └─ Jolpica: /2026/driverStandings   │
│ ├─ Parse response                       │
│ ├─ Save to cache (24h TTL)              │
│ └─ Display drivers                      │
└─────────────────────────────────────────┘

SUBSEQUENT LOADS (within 24h)
┌─────────────────────────────────────────┐
│ FavoriteSelectionActivity               │
│ ├─ Check cache for drivers              │
│ │  └─ Cache hit ✓                       │
│ ├─ Display cached drivers               │
│ └─ Refresh in background (silent)       │
└─────────────────────────────────────────┘

AFTER 24h
┌─────────────────────────────────────────┐
│ FavoriteSelectionActivity               │
│ ├─ Check cache for drivers              │
│ │  └─ Cache expired                     │
│ ├─ Fetch from API                       │
│ │  └─ Jolpica: /2026/driverStandings   │
│ ├─ Parse response                       │
│ ├─ Update cache (24h TTL)               │
│ └─ Display drivers                      │
└─────────────────────────────────────────┘
```

## 🎨 Color Scheme

### Team Colors
```
Red Bull Racing:    #0600EF (Blue)
Mercedes-AMG:       #00D2BE (Cyan)
Ferrari:            #DC0000 (Red)
McLaren:            #FF8700 (Orange)
Aston Martin:       #006F62 (Green)
Alpine:             #0082FA (Blue)
Haas F1 Team:       #FFFFFF (White)
Alfa Romeo:         #900000 (Dark Red)
Williams:           #005AFF (Blue)
Kick Sauber:        #00D2BE (Cyan)
```

### UI Colors
```
Background:         Dark gradient
Card Background:    #1A1A1A (Dark)
Text:               #FFFFFF (White)
Accent:             #E10600 (Red)
Secondary:          #999999 (Gray)
Selection Border:   #FF0000 (Red)
```

## 📱 Screen Sizes

### Team Cards
- Grid: 2 columns
- Card Width: ~160dp
- Card Height: ~180dp
- Logo: 80x80dp
- Padding: 8dp

### Driver Cards
- Grid: 2 columns
- Card Width: ~160dp
- Card Height: ~200dp
- Photo: 120x120dp
- Padding: 8dp

### Activity
- Header Height: 56dp
- Button Height: 56dp
- Padding: 16dp
- Margin: 8dp

## 🔄 Interaction Flow

### Team Selection
```
User sees team cards
    ↓
User clicks team card
    ↓
Card highlights (red border)
    ↓
Checkmark appears
    ↓
Continue button remains disabled
    (waiting for driver selection)
```

### Driver Selection
```
User sees loading progress
    ↓
Driver photos load
    ↓
User clicks driver card
    ↓
Card highlights (red border)
    ↓
Checkmark appears
    ↓
Continue button enables
    (both team and driver selected)
```

### Completion
```
User clicks Continue
    ↓
Save to FavoriteRepository
    ↓
If from onboarding:
    └─ Go to MainActivity
If from settings:
    └─ Return to Settings
```

## 📊 Performance Timeline

```
FIRST LOAD
0ms:    Activity created
100ms:  Teams loaded and displayed
200ms:  Driver loading starts
500ms:  First driver photos appear
1500ms: All driver photos loaded
2000ms: User can select driver
3000ms: User clicks continue
3100ms: Favorites saved
3200ms: Navigate to next screen

CACHED LOAD
0ms:    Activity created
100ms:  Teams loaded and displayed
150ms:  Cached drivers loaded
200ms:  All drivers displayed
300ms:  User can select driver
500ms:  User clicks continue
600ms:  Favorites saved
700ms:  Navigate to next screen
```

## 🎯 Key Metrics

| Metric | Value |
|--------|-------|
| Team Load Time | <100ms |
| Driver Load (cached) | <200ms |
| Driver Load (API) | 1-3s |
| Photo Load | 500ms-2s each |
| Total First Load | 2-5s |
| Total Cached Load | <1s |
| Cache Size | ~50-100KB |
| Memory Usage | ~5-10MB |

---

This visual guide helps understand the user experience and data flow of the favorite selection feature.
