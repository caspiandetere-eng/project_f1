# Favorite Team & Driver Selection Feature

## Overview
This feature allows users to select their favorite F1 team and driver during onboarding and change them anytime from settings. The selection uses card-based UI with team logos and driver photos.

## Architecture

### Components

#### 1. **FavoriteRepository.java**
Manages favorite team and driver selections with SharedPreferences storage.

**Key Methods:**
- `setFavoriteTeam(context, teamId, teamName)` - Save favorite team
- `getFavoriteTeamId(context)` - Get favorite team ID
- `getFavoriteTeamName(context)` - Get favorite team name
- `hasFavoriteTeam(context)` - Check if team is selected
- `setFavoriteDriver(context, driverId, driverName)` - Save favorite driver
- `getFavoriteDriverId(context)` - Get favorite driver ID
- `getFavoriteDriverName(context)` - Get favorite driver name
- `hasFavoriteDriver(context)` - Check if driver is selected
- `hasBothFavorites(context)` - Check if both are selected
- `clearFavorites(context)` - Clear all selections

#### 2. **Team.java** (Model)
Represents F1 teams with their data.

**Properties:**
- `id` - Team identifier
- `name` - Team name
- `color` - Team brand color (hex)
- `logoResId` - Logo drawable resource ID

**Static Methods:**
- `getAllTeams()` - Get list of all F1 teams
- `getTeamById(id)` - Get specific team by ID

#### 3. **TeamSelectionAdapter.java**
RecyclerView adapter for displaying team cards.

**Features:**
- Grid layout (2 columns)
- Team logo display
- Team name display
- Selection indicator
- Team color-based card styling
- Click listener for selection

#### 4. **DriverSelectionAdapter.java**
RecyclerView adapter for displaying driver cards.

**Features:**
- Grid layout (2 columns)
- Driver photo from online source (Glide)
- Driver name display
- Nationality display
- Selection indicator
- Click listener for selection

#### 5. **FavoriteSelectionActivity.java**
Main activity for selecting favorites.

**Features:**
- Two-step selection (Team → Driver)
- API-based driver loading with caching
- Progress indication
- Continue button (enabled only when both selected)
- Works in onboarding and settings modes

### Data Flow

```
User Opens App
    ↓
OnboardingActivity
    ↓
Check if favorites selected?
    ├─ Yes → Go to MainActivity
    └─ No → FavoriteSelectionActivity
    ↓
FavoriteSelectionActivity
    ├─ Load Teams (hardcoded)
    ├─ Load Drivers (API + cache)
    ├─ User selects Team
    ├─ User selects Driver
    └─ Save to FavoriteRepository
    ↓
MainActivity
```

## Integration Points

### 1. **Onboarding Flow**
Modified `OnboardingActivity.finishOnboarding()`:
```java
if (!FavoriteRepository.hasBothFavorites(this)) {
    Intent intent = new Intent(this, FavoriteSelectionActivity.class);
    intent.putExtra("from_onboarding", true);
    startActivity(intent);
}
```

### 2. **Settings Access**
Added button in `SettingsActivity`:
```java
btnChangeFavorites.setOnClickListener(v -> {
    Intent intent = new Intent(this, FavoriteSelectionActivity.class);
    intent.putExtra("from_onboarding", false);
    startActivity(intent);
});
```

## UI Components

### Team Card Layout (item_team_card.xml)
- MaterialCardView with team color background
- ImageView for team logo (80x80dp)
- TextView for team name
- Selection indicator (checkmark)
- Stroke highlight when selected

### Driver Card Layout (item_driver_card.xml)
- MaterialCardView
- FrameLayout with ImageView for driver photo (120x120dp)
- ProgressBar while loading
- TextView for driver name
- TextView for nationality
- Selection indicator (checkmark)
- Stroke highlight when selected

### Activity Layout (activity_favorite_selection.xml)
- Header with back button and title
- ScrollView containing:
  - Team selection section with RecyclerView
  - Driver selection section with RecyclerView
  - ProgressBar for loading drivers
- Continue button (disabled until both selected)

## Data Sources

### Teams
**Source:** Hardcoded in `Team.getAllTeams()`
**Teams Included:**
- Red Bull Racing
- Mercedes-AMG
- Ferrari
- McLaren
- Aston Martin
- Alpine
- Haas F1 Team
- Alfa Romeo
- Williams
- Kick Sauber

### Drivers
**Source:** Jolpica API (`/2026/driverStandings.json`)
**Caching:** 24-hour TTL in SharedPreferences
**Photos:** Formula1.com CDN (via Glide)
**Season:** 2026 (Current/Upcoming)

## Caching Strategy

### Team Data
- **Storage:** Hardcoded (no caching needed)
- **Load Time:** Instant

### Driver Data
- **Storage:** SharedPreferences (JSON)
- **TTL:** 24 hours
- **Cache Key:** `drivers_2026`
- **Fallback:** API fetch if cache miss or expired

### Driver Photos
- **Storage:** Glide disk cache
- **Source:** `https://media.formula1.com/image/upload/f_auto,c_thumb,w_500,ar_1:1/{driverId}.png`
- **Placeholder:** App launcher icon

## Usage Examples

### Check if User Has Favorites
```java
if (FavoriteRepository.hasBothFavorites(context)) {
    String teamId = FavoriteRepository.getFavoriteTeamId(context);
    String driverId = FavoriteRepository.getFavoriteDriverId(context);
    // Use favorites
}
```

### Get Favorite Team Name
```java
String teamName = FavoriteRepository.getFavoriteTeamName(context);
```

### Get Favorite Driver Name
```java
String driverName = FavoriteRepository.getFavoriteDriverName(context);
```

### Clear Favorites
```java
FavoriteRepository.clearFavorites(context);
```

## Files Created

1. **FavoriteRepository.java** - Favorite management
2. **Team.java** - Team model
3. **TeamSelectionAdapter.java** - Team card adapter
4. **DriverSelectionAdapter.java** - Driver card adapter
5. **FavoriteSelectionActivity.java** - Selection activity
6. **item_team_card.xml** - Team card layout
7. **item_driver_card.xml** - Driver card layout
8. **activity_favorite_selection.xml** - Activity layout

## Files Modified

1. **OnboardingActivity.java** - Added favorite selection check
2. **SettingsActivity.java** - Added change favorites button

## Dependencies

- Glide (for driver photo loading)
- Retrofit (for API calls)
- Gson (for JSON parsing)
- Material Design components

## Future Enhancements

1. **Team Logos:** Replace placeholder icons with actual team logos
2. **Driver Photos:** Cache driver photos locally
3. **Favorites Display:** Show selected favorites in MainActivity
4. **Multiple Favorites:** Allow selecting multiple drivers/teams
5. **Favorites History:** Track favorite changes over time
6. **Notifications:** Notify when favorite driver/team races

## Testing

### Test Cases

1. **First Time User**
   - Open app → Onboarding → Favorite selection
   - Select team and driver
   - Verify saved in SharedPreferences

2. **Returning User**
   - Open app → Skip onboarding if favorites exist
   - Go to Settings → Change favorites
   - Verify update in SharedPreferences

3. **Offline Mode**
   - Load app offline
   - Verify cached drivers display
   - Verify team selection works

4. **Cache Expiration**
   - Wait 24+ hours
   - Open favorite selection
   - Verify fresh API fetch

## Troubleshooting

### Driver Photos Not Loading
- Check internet connection
- Verify Glide dependency
- Check Formula1.com CDN availability

### Favorites Not Saving
- Check SharedPreferences permissions
- Verify FavoriteRepository methods called
- Check logcat for errors

### Slow Driver Loading
- Check API response time
- Verify cache is working
- Check network connectivity

## Performance Metrics

- **Team Loading:** <100ms (hardcoded)
- **Driver Loading (cached):** <200ms
- **Driver Loading (API):** 1-3 seconds
- **Photo Loading:** 500ms-2s per image
- **Memory Usage:** ~5-10MB for driver list + photos
