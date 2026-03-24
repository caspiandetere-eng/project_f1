package com.example.project_f1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class TechAnalysisActivity extends BaseActivity {

    private android.widget.LinearLayout tabContainer;
    private RecyclerView contentRecycler;
    private String currentTab = "Power Unit";
    private ThemeManager.TeamTheme currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_tech_analysis);
        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);

        View stripe = findViewById(R.id.topStripe);
        if (stripe != null) stripe.setBackgroundColor(theme.accent);
        View headerBar = findViewById(R.id.headerAccentBar);
        if (headerBar != null) headerBar.setBackgroundColor(theme.accent);
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack instanceof com.google.android.material.button.MaterialButton)
            ((com.google.android.material.button.MaterialButton) btnBack)
                    .setBackgroundTintList(android.content.res.ColorStateList.valueOf(theme.buttonBg));
        this.currentTheme = theme;

        tabContainer = findViewById(R.id.tabContainer);
        contentRecycler = findViewById(R.id.contentRecycler);
        contentRecycler.setLayoutManager(new LinearLayoutManager(this));
        contentRecycler.setItemAnimator(null); // we handle our own animations

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
            tv.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
            tv.setLetterSpacing(0.06f);
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
        tv.setBackgroundColor(selected ? Color.parseColor("#1A1A1A") : Color.TRANSPARENT);
        if (selected) {
            tv.setPadding(dp(20), dp(10), dp(20), dp(8));
            tv.setBackground(getDrawable(R.drawable.gradient_red));
            tv.setBackgroundColor(Color.parseColor("#1A1A1A"));
        }
    }

    private void updateTabs() {
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            TextView tv = (TextView) tabContainer.getChildAt(i);
            boolean selected = tv.getText().toString().equals(currentTab);
            tv.setTextColor(selected ? Color.WHITE : Color.parseColor("#666666"));
            tv.setBackgroundColor(selected ? Color.parseColor("#1A1A1A") : Color.TRANSPARENT);
        }
    }

    private void updateContent() {
        List<ExpandableCardItem> items = new ArrayList<>();
        switch (currentTab) {
            case "Power Unit":
                items.add(new ExpandableCardItem("V6 Turbo Hybrid Evolution",
                        "The 2026 Power Unit retains the 1.6-litre V6 turbocharged internal combustion engine but introduces one of the biggest hybrid transformations in Formula 1 history. The removal of the MGU-H simplifies the system significantly, reducing costs and improving reliability, while the MGU-K's output is increased to 350kW, creating a near equal balance between electrical and combustion power. This marks a shift toward road-relevant hybrid technology, where efficiency and performance coexist. The new system also enhances energy deployment strategies, allowing drivers to manage power more actively during races. Manufacturers benefit from reduced complexity while still pushing technological boundaries, making the sport more attractive to new entrants and aligning Formula 1 with the future of high-performance automotive engineering.",
                        R.drawable.power_units, "#E10600"));
                items.add(new ExpandableCardItem("100% Sustainable Bio-Fuel",
                        "Formula 1's 2026 fuel represents a major leap toward sustainability, using advanced synthetic and bio-derived components to achieve net-zero carbon emissions. These fuels are produced from captured carbon dioxide, waste biomass, and non-food sources, ensuring that they do not compete with global food supply chains. A key advantage is their compatibility with existing combustion engines, making them a practical solution for both motorsport and everyday vehicles. This innovation allows Formula 1 to act as a testing ground for cleaner fuel technologies that can be scaled globally. Despite being environmentally friendly, these fuels maintain the high energy density required for peak racing performance, proving that sustainability and speed can go hand in hand.",
                        R.drawable.f1_biofuel, "#FF6600"));
                items.add(new ExpandableCardItem("Advanced Energy Recovery (ERS)",
                        "The Energy Recovery System in 2026 focuses entirely on kinetic energy, with a significantly upgraded MGU-K capable of harvesting and deploying much larger amounts of power. This system captures energy during braking and stores it in advanced battery systems, which can then be redeployed strategically throughout the lap. The introduction of 'Override Mode' gives drivers a tactical advantage, allowing short bursts of additional power for overtaking or defending. This adds a new layer of strategy to racing, where timing and energy management become crucial skills. The simplified ERS architecture also improves reliability and reduces weight, contributing to overall efficiency while maintaining the technological edge that defines Formula 1.",
                        R.drawable.ers, "#E10600"));
                break;
            case "Aerodynamics":
                items.add(new ExpandableCardItem("Active Aero — X-Mode & Z-Mode",
                        "Active aerodynamics redefine how Formula 1 cars interact with airflow in 2026. The introduction of X-Mode and Z-Mode allows cars to dynamically adjust their aerodynamic profiles depending on track conditions. X-Mode reduces drag on straights, enabling higher top speeds and improved efficiency, while Z-Mode increases downforce during cornering for enhanced grip and stability. These adjustments are controlled electronically, ensuring optimal performance in real time. This system not only improves lap times but also creates more opportunities for overtaking by reducing the aerodynamic disadvantage when following another car. It represents a major step toward smarter, more adaptive race car design.",
                        R.drawable.rges, "#0066FF"));
                items.add(new ExpandableCardItem("Closer Racing Philosophy",
                        "The 2026 aerodynamic regulations are designed with a clear goal: improve racing quality. By simplifying front wing designs and optimizing airflow structures, the new cars generate less turbulent wake, allowing trailing drivers to maintain up to 90% of their downforce. This significantly reduces the difficulty of following closely through corners, leading to more overtaking opportunities and tighter battles on track. The changes also make races more unpredictable and exciting, as drivers can stay within attacking range for longer periods. This philosophy prioritizes competition and spectacle, ensuring that Formula 1 remains engaging for fans worldwide.",
                        R.drawable.crp, "#3399FF"));
                items.add(new ExpandableCardItem("Reduced Ground Effect Sensitivity",
                        "Ground effect aerodynamics remain a key feature, but the 2026 design reduces sensitivity to ride height changes, addressing issues like porpoising that affected earlier cars. Engineers have refined the floor geometry to create more stable airflow under varying conditions, allowing for consistent downforce generation. This results in improved driver confidence, particularly in high-speed sections, and reduces the risk of sudden performance loss. Teams can also experiment with a wider range of setups, making cars more adaptable to different circuits. The overall effect is a smoother, safer, and more predictable driving experience.",
                        R.drawable.tech_card_aero, "#0066FF"));
                break;
            case "Chassis":
                items.add(new ExpandableCardItem("Nimble Dimensions",
                        "The 2026 chassis introduces a more compact and agile design, reducing both wheelbase and overall width. This makes the cars more responsive and easier to maneuver, particularly on tight street circuits such as Monaco and Singapore. The smaller footprint also improves overtaking opportunities by allowing drivers to take different racing lines. Despite the reduced size, the cars maintain high levels of stability and aerodynamic efficiency, ensuring that performance is not compromised. This shift brings Formula 1 closer to its roots, where agility and driver skill played a more prominent role in racing outcomes.",
                        R.drawable.monocoque_first_carbon_mclaren_mp4_1, "#C0C0C0"));
                items.add(new ExpandableCardItem("Aggressive Weight Reduction",
                        "Weight reduction is a major focus for the 2026 regulations, with a target of reducing overall car weight by approximately 30kg. This is achieved through the use of advanced materials such as recycled carbon fiber and lightweight metal alloys. A lighter car improves acceleration, braking, and tire wear, leading to more dynamic and efficient racing. It also enhances energy efficiency, aligning with sustainability goals. Engineers must carefully balance weight savings with structural integrity, ensuring that performance gains do not come at the expense of safety.",
                        R.drawable.weight_reduction_f1, "#AAAAAA"));
                items.add(new ExpandableCardItem("Reinforced Safety Cell",
                        "Safety advancements remain at the core of Formula 1 design. The 2026 chassis features a reinforced survival cell capable of withstanding higher impact forces than ever before. Improved crash structures, stronger roll hoops, and advanced energy-absorbing materials provide enhanced protection for drivers. These developments are supported by extensive simulation and real-world testing, ensuring that the cars can handle extreme scenarios. The continuous evolution of safety technology reflects Formula 1's commitment to protecting drivers while pushing the limits of performance.",
                        R.drawable.tech_card_chassis, "#C0C0C0"));
                break;
            case "Sustainability":
                items.add(new ExpandableCardItem("Net Zero Carbon by 2030",
                        "Formula 1's commitment to achieving net-zero carbon emissions by 2030 is driving innovation across the sport. This includes the use of sustainable fuels, renewable energy in team operations, and more efficient logistics. By reducing emissions at every level, F1 aims to set an example for other industries while maintaining its status as a high-performance sport. The initiative demonstrates that environmental responsibility and technological advancement can coexist, paving the way for a more sustainable future.",
                        R.drawable.f1_netzero, "#00AA00"));
                items.add(new ExpandableCardItem("Regionalized Calendar Logistics",
                        "To minimize environmental impact, Formula 1 has restructured its race calendar into regional clusters. This reduces the need for long-distance travel and lowers overall carbon emissions. Equipment and personnel can move more efficiently between races, improving logistics and reducing costs. The new approach maintains the global nature of the sport while making it more environmentally responsible. It represents a practical step toward achieving long-term sustainability goals.",
                        R.drawable.regionalized_calendar_logistics, "#009900"));
                items.add(new ExpandableCardItem("Circular Economy Components",
                        "The adoption of circular economy principles in Formula 1 focuses on reducing waste and maximizing resource efficiency. Teams are required to incorporate recycled materials into non-critical components and explore innovative recycling methods for composites. Initiatives such as brake dust capture and material reuse are being tested to minimize environmental impact. These efforts highlight Formula 1's role as a leader in sustainable innovation, demonstrating how advanced engineering can contribute to a more responsible future.",
                        R.drawable.circular_economy_f1, "#00AA00"));
                break;
        }
        contentRecycler.setAdapter(new ExpandableCardAdapter(items));
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }
}
