# F1 App Typography System

## Font Family Mapping

### 1. **Titillium Web** - App Branding & Primary Headings
- **Usage**: Main app title, page headers, race names
- **Weights**: Regular (400), SemiBold (600), Bold (700), Black (900)
- **Color**: `#FFFFFF` (white) with red glow shadow
- **Examples**: 
  - "F1 COMPANION" (app title)
  - "BAHRAIN GRAND PRIX" (race names)
  - "WELCOME BACK" (login header)

### 2. **Rajdhani** - Secondary Headings
- **Usage**: Card titles, section headers
- **Weights**: Regular (400), SemiBold (600), Bold (700)
- **Color**: `#FFFFFF` (white)
- **Examples**:
  - "F1 History" (card titles)
  - "Tech Analysis" (card titles)
  - "Lap Details" (card titles)

### 3. **Barlow Condensed** - Compact List Headers
- **Usage**: Section labels, status indicators, buttons
- **Weights**: Regular (400), SemiBold (600), Bold (700)
- **Color**: `#E10600` (F1 red) for labels, `#FFFFFF` for buttons
- **Examples**:
  - "UPCOMING RACE" (section labels)
  - "LIVE POSITIONS" (list headers)
  - "🔴 LIVE" (status indicators)
  - "LOGIN" (button text)

### 4. **Inter** - Article Body Text
- **Usage**: Descriptions, subtitles, body content
- **Weights**: Regular (400), Medium (500), SemiBold (600)
- **Color**: `#999999` (gray) for secondary text
- **Examples**:
  - "Legends and Eras" (card descriptions)
  - "2024 Season" (subtitles)
  - Input field text

### 5. **JetBrains Mono** - Live Telemetry Data
- **Usage**: Lap counters, live positions, timing data
- **Weights**: Regular (400), Bold (700)
- **Color**: `#FFFFFF` (white) or `#999999` (gray)
- **Examples**:
  - "LAP 1 / 57" (lap counter)
  - "1  M. Verstappen  LEADER" (live positions)
  - Race timing data

### 6. **Roboto Mono** - Tabular Data Tables
- **Usage**: Standings tables, data grids (future use)
- **Weights**: Regular (400), Medium (500), Bold (700)
- **Color**: `#FFFFFF` (white)
- **Note**: System font, no download needed

## Color Palette

### Primary Colors
- **F1 Red**: `#E10600` - Accent color, borders, labels
- **Pure White**: `#FFFFFF` - Primary text, headers
- **Dark Gray**: `#999999` - Secondary text, descriptions
- **Card Background**: `#1A1A1A` - Card backgrounds
- **App Background**: `#0D0D0D` - Main background

### Accent Colors
- **Gold**: `#FFD700` - P1 position badges
- **Silver**: `#C0C0C0` - P2 position badges
- **Bronze**: `#CD7F32` - P3 position badges

## Implementation

### XML Layout
```xml
<!-- Primary Heading -->
<TextView
    android:fontFamily="@font/titillium_web"
    android:textFontWeight="900"
    android:textColor="#FFFFFF"
    android:textSize="28sp" />

<!-- Section Label -->
<TextView
    android:fontFamily="@font/barlow_condensed"
    android:textFontWeight="700"
    android:textColor="#E10600"
    android:textSize="13sp" />

<!-- Body Text -->
<TextView
    android:fontFamily="@font/inter"
    android:textColor="#999999"
    android:textSize="14sp" />

<!-- Telemetry Data -->
<TextView
    android:fontFamily="@font/jetbrains_mono"
    android:textColor="#FFFFFF"
    android:textSize="16sp" />
```

### Java Code
```java
// Apply font to dynamic TextView
Typeface typeface = ResourcesCompat.getFont(context, R.font.jetbrains_mono);
if (typeface != null) textView.setTypeface(typeface);
```

## Font Files Required

Download from Google Fonts and place in `/res/font/`:

1. **titillium_web_regular.ttf**
2. **titillium_web_semibold.ttf**
3. **titillium_web_bold.ttf**
4. **titillium_web_black.ttf**
5. **rajdhani_regular.ttf**
6. **rajdhani_semibold.ttf**
7. **rajdhani_bold.ttf**
8. **barlow_condensed_regular.ttf**
9. **barlow_condensed_semibold.ttf**
10. **barlow_condensed_bold.ttf**
11. **inter_regular.ttf**
12. **inter_medium.ttf**
13. **inter_semibold.ttf**
14. **jetbrains_mono_regular.ttf**
15. **jetbrains_mono_bold.ttf**

## Typography Hierarchy

1. **Level 1**: 32-36sp (Titillium Web Black) - Page titles
2. **Level 2**: 24-28sp (Titillium Web Bold) - Race names
3. **Level 3**: 18sp (Rajdhani Bold) - Card titles
4. **Level 4**: 13-16sp (Barlow Condensed Bold) - Labels
5. **Level 5**: 13-14sp (Inter Regular) - Body text
6. **Level 6**: 14-16sp (JetBrains Mono) - Data

## Text Effects

- **Glow Effect**: `shadowColor="#E10600"` + `shadowRadius="20"`
- **Letter Spacing**: `0.08-0.15` for racing aesthetic
- **Text Transform**: `textAllCaps="true"` for headers

All fonts configured with proper weights and colors for professional F1 racing aesthetic!
