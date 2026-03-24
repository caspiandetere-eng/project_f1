# DriverCardView - Quick Reference Guide

## 5-Minute Setup

### Step 1: Add Files to Project
```
app/src/main/java/com/example/project_f1/
├── DriverCardView.java
├── DriverCardAdapter.java
└── DriverCardHelper.java

app/src/main/res/layout/
└── driver_card.xml
```

### Step 2: Ensure Dependencies Exist
- `TeamThemeHelper.java` ✓
- `Driver.java` model ✓
- `res/drawable/` with driver images ✓
- `res/drawable/` with flag images ✓

### Step 3: Use in Activity

#### Option A: RecyclerView (Recommended)
```java
public class DriversActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);
        
        RecyclerView recyclerView = findViewById(R.id.driverRecyclerView);
        
        DriverCardHelper.setupDriverCardRecyclerView(
            this,
            recyclerView,
            Driver.getAllDrivers(),
            (driver, position) -> {
                Toast.makeText(this, driver.getFullName(), Toast.LENGTH_SHORT).show();
            }
        );
    }
}
```

#### Option B: Single Card
```java
LinearLayout container = findViewById(R.id.driverContainer);
Driver driver = Driver.getDriverById("russell");

DriverCardView cardView = new DriverCardView(this);
cardView.updateDriverCard(driver);
cardView.setOnClickListener(v -> {
    cardView.animateClick();
    // Handle click
});
container.addView(cardView);
```

#### Option C: Team Drivers
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

## Common Tasks

### Display All Drivers
```java
DriverCardHelper.setupDriverCardRecyclerView(
    this,
    recyclerView,
    Driver.getAllDrivers(),
    listener
);
```

### Display Specific Team
```java
DriverCardHelper.setupDriverCardRecyclerView(
    this,
    recyclerView,
    Driver.getDriversByTeam("ferrari"),
    listener
);
```

### Highlight Selected Driver
```java
DriverCardAdapter adapter = (DriverCardAdapter) recyclerView.getAdapter();
adapter.setSelectedPosition(0);  // Highlight first driver
```

### Get Selected Driver
```java
DriverCardAdapter adapter = (DriverCardAdapter) recyclerView.getAdapter();
Driver selected = adapter.getSelectedDriver();
```

### Update Card Data
```java
cardView.updateDriverCard(newDriver);
```

### Animate Click
```java
cardView.animateClick();
```

### Set Highlighted State
```java
cardView.setHighlighted(true);   // Highlight
cardView.setHighlighted(false);  // Normal
```

## Layout Integration

### In XML Layout File
```xml
<RecyclerView
    android:id="@+id/driverRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clipToPadding="false"
    android:paddingBottom="24dp" />
```

### Or with LinearLayout
```xml
<LinearLayout
    android:id="@+id/driverContainer"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical" />
```

## Customization

### Change Card Size
Edit `driver_card.xml`:
```xml
android:layout_height="220dp"  <!-- Was 200dp -->
```

### Change Corner Radius
Edit `driver_card.xml`:
```xml
app:cardCornerRadius="32dp"  <!-- Was 24dp -->
```

### Change Elevation
Edit `driver_card.xml`:
```xml
app:cardElevation="16dp"  <!-- Was 12dp -->
```

### Change Animation Speed
Edit `DriverCardView.java`:
```java
animatorSet.setDuration(400);  // Was 300ms
```

### Change Gradient Opacity
Edit `DriverCardView.java`:
```java
gradientOverlay.setAlpha(0.7f);  // Was 0.6f
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Images not showing | Verify `R.drawable.driver_name` exists |
| Flags not showing | Verify `R.drawable.flag_country` exists |
| Gradient not visible | Check alpha value, verify colors differ |
| Animations not playing | Check developer settings, verify view attached |
| Cards not clickable | Ensure `android:clickable="true"` in XML |

## Performance Tips

1. Use RecyclerView for lists (not LinearLayout)
2. Don't create cards in loops
3. Reuse adapter instead of creating new ones
4. Let Android cache drawables
5. Use `setDrivers()` for batch updates

## File Checklist

- [ ] `DriverCardView.java` - Main component
- [ ] `DriverCardAdapter.java` - RecyclerView adapter
- [ ] `DriverCardHelper.java` - Helper methods
- [ ] `driver_card.xml` - Layout file
- [ ] `TeamThemeHelper.java` - Already exists
- [ ] `Driver.java` - Model class
- [ ] Driver images in `res/drawable/`
- [ ] Flag images in `res/drawable/`

## Example: Complete Activity

```java
public class DriversActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private DriverCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);
        
        recyclerView = findViewById(R.id.driverRecyclerView);
        
        // Setup with all drivers
        DriverCardHelper.setupDriverCardRecyclerView(
            this,
            recyclerView,
            Driver.getAllDrivers(),
            this::onDriverClick
        );
    }

    private void onDriverClick(Driver driver, int position) {
        Toast.makeText(this, driver.getFullName(), Toast.LENGTH_SHORT).show();
        // Navigate to driver details, etc.
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
```

## API Reference

### DriverCardView
```java
updateDriverCard(Driver driver)           // Update with new data
setHighlighted(boolean highlighted)       // Set highlight state
animateClick()                            // Animate click
getDriver() -> Driver                     // Get current driver
isHighlighted() -> boolean                // Check highlight state
```

### DriverCardAdapter
```java
setDrivers(List<Driver> driverList)       // Set driver list
addDriver(Driver driver)                  // Add single driver
setSelectedPosition(int position)         // Select driver
getSelectedDriver() -> Driver             // Get selected driver
```

### DriverCardHelper
```java
setupDriverCardRecyclerView(...)          // Setup RecyclerView
createAndAddDriverCard(...)               // Create single card
getDriversByTeam(String teamId)           // Get team drivers
getAllDrivers()                           // Get all drivers
```

## Notes

- All images loaded locally (no Glide/Picasso)
- Fonts unchanged (uses existing project fonts)
- Team colors from TeamThemeHelper
- Smooth animations (300ms entry, 200ms click)
- RecyclerView optimized for performance
- No external dependencies required
