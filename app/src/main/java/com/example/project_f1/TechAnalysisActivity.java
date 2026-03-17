package com.example.project_f1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TechAnalysisActivity extends AppCompatActivity {

    private LinearLayout tabContainer;
    private LinearLayout contentContainer;
    private String currentTab = "Power Unit";

    private static final int[][] TAB_CONTENT = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_tech_analysis);
        ThemeManager.applyStatusBar(this);

        tabContainer = findViewById(R.id.tabContainer);
        contentContainer = findViewById(R.id.contentContainer);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupTabs();
        updateContent();
    }

    private void setupTabs() {
        String[] tabs = {"Power Unit", "Aerodynamics", "Chassis", "Sustainability"};
        for (String tab : tabs) {
            TextView tv = new TextView(this);
            tv.setText(tab);
            tv.setPadding(dp(20), dp(10), dp(20), dp(10));
            tv.setTextSize(14);
            applyTabStyle(tv, tab.equals(currentTab));
            tv.setOnClickListener(v -> {
                currentTab = tab;
                updateTabs();
                updateContent();
            });
            tabContainer.addView(tv);
        }
    }

    private void applyTabStyle(TextView tv, boolean selected) {
        tv.setTextColor(selected ? Color.WHITE : Color.parseColor("#666666"));
        tv.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
        tv.setBackgroundColor(selected ? Color.parseColor("#1A1A1A") : Color.TRANSPARENT);
        if (selected) {
            tv.setPadding(dp(20), dp(10), dp(20), dp(8));
            // bottom border via compound drawable workaround — use background layer instead
            tv.setBackground(getDrawable(R.drawable.gradient_red));
            tv.setBackgroundColor(Color.parseColor("#1A1A1A"));
        }
    }

    private void updateTabs() {
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            TextView tv = (TextView) tabContainer.getChildAt(i);
            boolean selected = tv.getText().toString().equals(currentTab);
            tv.setTextColor(selected ? Color.WHITE : Color.parseColor("#666666"));
            tv.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
            tv.setBackgroundColor(selected ? Color.parseColor("#1A1A1A") : Color.TRANSPARENT);
        }
    }

    private void updateContent() {
        contentContainer.removeAllViews();
        switch (currentTab) {
            case "Power Unit":
                addCard("V6 Turbo Hybrid Evolution",
                        "The 2026 Power Unit retains the 1.6-litre V6 ICE but removes the MGU-H. Electric output is tripled to 350kW (~475hp), creating an even 50/50 split between electrical and combustion power for the first time in F1 history.",
                        R.drawable.tech_card_power_unit, "#E10600");
                addCard("100% Sustainable Bio-Fuel",
                        "Developed with global energy partners, these drop-in fuels are derived from non-food biological sources or captured carbon, ensuring zero net carbon emissions during combustion while maintaining peak performance.",
                        R.drawable.tech_card_power_unit, "#FF6600");
                addCard("Advanced Energy Recovery (ERS)",
                        "The new MGU-K features a more robust kinetic energy recovery system. Drivers can deploy extra energy via 'Override Mode' to defend or attack — a tactical Push-to-Pass system built into the regulations.",
                        R.drawable.tech_card_power_unit, "#E10600");
                break;
            case "Aerodynamics":
                addCard("Active Aero — X-Mode & Z-Mode",
                        "'X-Mode' minimizes drag on straights by thinning wing profiles. 'Z-Mode' maximizes downforce in corners. Both front and rear wings are moveable, controlled automatically by the car's ECU.",
                        R.drawable.tech_card_aero, "#0066FF");
                addCard("Closer Racing Philosophy",
                        "Simplified front wings and a revised underbody floor allow following cars to retain up to 90% of their downforce when trailing closely, dramatically improving overtaking opportunities.",
                        R.drawable.tech_card_aero, "#3399FF");
                addCard("Reduced Ground Effect Sensitivity",
                        "The floor design is less sensitive to ride-height changes, eliminating the risk of porpoising and allowing a wider range of suspension setups across different track surfaces.",
                        R.drawable.tech_card_aero, "#0066FF");
                break;
            case "Chassis":
                addCard("Nimble Dimensions",
                        "The 'Agile Car' concept reduces maximum wheelbase by 200mm (to 3400mm) and width by 100mm (to 1900mm). This improves racing on tighter street circuits like Monaco and Singapore.",
                        R.drawable.tech_card_chassis, "#C0C0C0");
                addCard("Aggressive Weight Reduction",
                        "Through optimized use of recycled carbon fiber and lightweight alloys, the FIA targets a 30kg reduction in minimum weight, bringing cars closer to 768kg — the lightest since 2021.",
                        R.drawable.tech_card_chassis, "#AAAAAA");
                addCard("Reinforced Safety Cell",
                        "New impact tests simulate higher G-force scenarios. The roll-hoop withstands much higher vertical loads, and side-impact structures are reinforced with energy-absorbing honeycomb matrices.",
                        R.drawable.tech_card_chassis, "#C0C0C0");
                break;
            case "Sustainability":
                addCard("Net Zero Carbon by 2030",
                        "F1 is reshaping every aspect of the sport — carbon-neutral travel, 100% renewable energy at all team factories, and a dramatic reduction in single-use plastics across the entire paddock.",
                        R.drawable.tech_card_sustainability, "#00AA00");
                addCard("Regionalized Calendar Logistics",
                        "The calendar is grouped by region (Middle East, European, Asian legs) to reduce air freight emissions. This significantly shortens total travel distance across the 24-race season.",
                        R.drawable.tech_card_sustainability, "#009900");
                addCard("Circular Economy Components",
                        "Teams are mandated to use a percentage of recycled materials in non-structural components. Brake dust collection and end-of-life composite recycling are being trialed for 2026.",
                        R.drawable.tech_card_sustainability, "#00AA00");
                break;
        }
    }

    private void addCard(String title, String desc, int imageRes, String accentHex) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(16));
        card.setLayoutParams(params);
        card.setRadius(dp(16));
        card.setCardBackgroundColor(Color.parseColor("#1A1A1A"));
        card.setCardElevation(dp(6));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Image banner
        ImageView img = new ImageView(this);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(140));
        img.setLayoutParams(imgParams);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setImageResource(imageRes);
        layout.addView(img);

        // Accent bar
        View accent = new View(this);
        LinearLayout.LayoutParams accentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(3));
        accent.setLayoutParams(accentParams);
        accent.setBackgroundColor(Color.parseColor(accentHex));
        layout.addView(accent);

        // Text content
        LinearLayout text = new LinearLayout(this);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setPadding(dp(20), dp(16), dp(20), dp(20));

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(17);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setLetterSpacing(0.02f);
        text.addView(tvTitle);

        // Accent label
        TextView tvAccent = new TextView(this);
        tvAccent.setText("2026 REGULATION");
        tvAccent.setTextColor(Color.parseColor(accentHex));
        tvAccent.setTextSize(11);
        tvAccent.setTypeface(null, Typeface.BOLD);
        tvAccent.setLetterSpacing(0.1f);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelParams.setMargins(0, dp(4), 0, dp(10));
        tvAccent.setLayoutParams(labelParams);
        text.addView(tvAccent);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(desc);
        tvDesc.setTextColor(Color.parseColor("#CCCCCC"));
        tvDesc.setTextSize(14);
        tvDesc.setLineSpacing(0, 1.4f);
        text.addView(tvDesc);

        layout.addView(text);
        card.addView(layout);

        card.setAlpha(0f);
        contentContainer.addView(card);
        card.animate().alpha(1f).setDuration(300).setStartDelay(contentContainer.getChildCount() * 80L).start();
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }
}
