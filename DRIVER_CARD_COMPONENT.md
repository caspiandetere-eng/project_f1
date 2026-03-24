# F1 Driver Card Component Documentation

## Overview
A reusable, dynamic F1-style driver card UI component that displays driver information with team-specific theming, animations, and local image loading.

## Components

### 1. DriverCardView.java
Main custom view class that extends MaterialCardView.

**Key Features:**
- Dynamic team-based gradient theming
- Local drawable image loading (no external libraries)
- Smooth entry animations (alpha, translation, scale)
- Click animations with ripple effect
- Highlighted state support
- Glow effect behind driver image

**Public Methods:**

```java
// Update card with driver data
updateDriverCard(Driver driver)

// Set highlighted state
setHighlighted(boolean highlighted)

// Animate click interaction
animateClick()

// Get current driver
getDriver() -> Driver

// Check if highlighted
isHighlighted() -> boolean
```

### 2. driver_card.xml
Layout file defining the card structure.

**Layout Structure:**
```
MaterialCardView (200dp height, 24dp radius)
├── ConstraintLayout
│   ├── Gradient Overlay (left→right fade)
│   ├── Texture Overlay (checkered pattern, 8% alpha)
│   ├── Background Number (large, 15% alpha)
│   ├── Info Container (left side)
│   │   ├── Driver Name (Titillium Web, 22sp)
│   │   └── Team Name (Barlow Condensed, 13sp)
│   ├── Flag Icon (44x44dp, bottom-left)
│   ├── Glow Behind (oval, accent color)
│   └── Driver Image (140x140dp, bottom-right)
```

### 3. DriverCardAdapter.java
RecyclerView adapter for efficient card display.

**Features:**
- Efficient view reuse
- Selection tracking
- Click callbacks
- Smooth animations on bind

**Usage:**
```java
DriverCardAdapter adapter = new DriverCardAdapter((driver, position) -> {
    // Handle driver click
});
adapter.setDrivers(driverList);
recyclerView.setAdapter(adapter);
```

### 4. TeamThemeHelper.java
Helper class for team-specific colors (already exists).

**Returns:**
- `gradientStart`: Left gradient color
- `gradientEnd`: Right gradient color
- `accentColor`: Team accent color

**Supported Teams:**
- Mercedes, Ferrari, McLaren, Red Bull Racing
- Aston Martin, Audi, Cadillac, Alpine
- Williams, Racing Bulls, Haas

### 5. DriverCardHelper.java
Convenience helper for integration.

**Methods:**
```java
// Setup RecyclerView with driver cards
setupDriverCardRecyclerView(context, recyclerView, drivers, listener)

// Create single card
createAndAddDriverCard(context, container, driver, listener)

// Get drivers by team
getDriversByTeam(teamId)

// Get all drivers
getAllDrivers()
```

## Usage Examples

### Example 1: RecyclerView with All Drivers
```java
RecyclerView recyclerView = findViewById(R.id.driverRecyclerView);

DriverCardHelper.setupDriverCardRecyclerView(
    this,
    recyclerView,
    Driver.getAllDrivers(),
    (driver, position) -> {
        Toast.makeText(this, driver.getFullName(), Toast.LENGTH_SHORT).show();
    }
);
```

### Example 2: Display Team Drivers
```java
List<Driver> mercedesDrivers = Driver.getDriversByTeam("mercedes");

DriverCardHelper.setupDriverCardRecyclerView(
    this,
    recyclerView,
    mercedesDrivers,
    (driver, position) -> {
        // Handle click
    }
);
```

### Example 3: Single Card in LinearLayout
```java
LinearLayout container = findViewById(R.id.driverContainer);
Driver driver = Driver.getDriverById("russell");

DriverCardView cardView = DriverCardHelper.createAndAddDriverCard(
    this,
    container,
    driver,
    v -> {
        // Handle click
    }
);

// Later, highlight the card
cardView.setHighlighted(true);
```

### Example 4: Programmatic Card Creation
```java
DriverCardView cardView = new DriverCardView(context);
cardView.updateDriverCard(driver);
cardView.setOnClickListener(v -> {
    cardView.animateClick();
    // Handle click
});
container.addView(cardView);
```

## Data Structure

Driver model:
```java
public class Driver {
    public String id;              // "russell"
    public String firstName;       // "George"
    public String lastName;        // "Russell"
    public String teamId;          // "mercedes"
    public String teamName;        // "Mercedes-AMG"
    public String nationality;     // "British"
    public int photoResId;         // R.drawable.george_russell
}
```

## Image Loading

### Driver Images
- Loaded from drawable resources using `photoResId`
- No external libraries (Glide, Picasso, etc.)
- Fallback to placeholder if resource not found

### Flag Images
- Loaded from drawable using naming convention: `flag_<nationality>`
- Example: `flag_british`, `flag_german`, `flag_italian`
- Automatically hidden if flag not found

## Theming

### Team Colors
Each team has:
- **Gradient Start**: Left side color
- **Gradient End**: Right side color
- **Accent Color**: Primary team color

### Gradient Application
- Direction: LEFT → RIGHT
- Applied to overlay with 60% opacity
- Creates depth effect

### Glow Effect
- Generated from accent color
- Semi-transparent (80 alpha)
- Positioned behind driver image
- Creates halo effect

## Animations

### Entry Animation (300ms)
- Alpha: 0 → 1
- TranslationY: 20dp → 0
- Scale: 0.95 → 1.0

### Click Animation (200ms)
- Scale: 1.0 → 0.97 → 1.0
- Provides tactile feedback

### Highlight State
- Increased stroke width (2dp → 3dp)
- Increased elevation (12dp → 20dp)
- Smooth transition

## Performance Optimizations

1. **RecyclerView Reuse**: ViewHolder pattern for efficient memory usage
2. **Drawable Caching**: Android system caches drawable resources
3. **Minimal Nesting**: Flat ConstraintLayout hierarchy
4. **No Network Calls**: All images loaded locally
5. **Efficient Animations**: Hardware-accelerated transforms

## Customization

### Change Card Height
Edit `driver_card.xml`:
```xml
android:layout_height="220dp"  <!-- Default: 200dp -->
```

### Change Corner Radius
Edit `driver_card.xml` and `DriverCardView.java`:
```xml
app:cardCornerRadius="32dp"  <!-- Default: 24dp -->
```

### Change Elevation
Edit `driver_card.xml`:
```xml
app:cardElevation="16dp"  <!-- Default: 12dp -->
```

### Modify Gradient Opacity
Edit `DriverCardView.java`:
```java
gradientOverlay.setAlpha(0.7f);  // Default: 0.6f
```

### Change Animation Duration
Edit `DriverCardView.java`:
```java
animatorSet.setDuration(400);  // Default: 300ms
```

## Troubleshooting

### Images Not Showing
1. Verify drawable resource exists: `R.drawable.driver_name`
2. Check `photoResId` is set correctly in Driver model
3. Ensure image file is in `res/drawable/` folder

### Flags Not Showing
1. Verify flag drawable exists: `R.drawable.flag_country`
2. Check nationality string matches flag naming
3. Flag will be hidden if not found (no error)

### Gradient Not Visible
1. Check `gradientOverlay` alpha value
2. Verify team colors are different
3. Ensure gradient overlay is not behind other views

### Animations Not Playing
1. Check if animations are disabled in developer settings
2. Verify view is attached to window
3. Check for animation duration conflicts

## Best Practices

1. **Use RecyclerView**: For lists of drivers, use DriverCardAdapter
2. **Handle Clicks**: Always provide click listener for user interaction
3. **Highlight Selection**: Use `setHighlighted()` to show selected driver
4. **Batch Updates**: Use `setDrivers()` instead of adding one by one
5. **Memory**: Don't hold references to cards longer than needed

## Integration Checklist

- [ ] Add `driver_card.xml` to `res/layout/`
- [ ] Add `DriverCardView.java` to project
- [ ] Add `DriverCardAdapter.java` to project
- [ ] Add `DriverCardHelper.java` to project
- [ ] Verify `TeamThemeHelper.java` exists
- [ ] Verify `Driver.java` model is complete
- [ ] Add flag drawables to `res/drawable/`
- [ ] Add driver photo drawables to `res/drawable/`
- [ ] Test with RecyclerView
- [ ] Test with single card
- [ ] Test animations
- [ ] Test theme switching

## Files Created

1. `res/layout/driver_card.xml` - Card layout
2. `DriverCardView.java` - Main component
3. `DriverCardAdapter.java` - RecyclerView adapter
4. `DriverCardHelper.java` - Integration helper
5. `TeamThemeHelper.java` - Already exists (updated)

## Dependencies

- AndroidX Material Design
- AndroidX ConstraintLayout
- AndroidX RecyclerView
- Android Framework (no external libraries)

## License

Part of Project F1 application.
