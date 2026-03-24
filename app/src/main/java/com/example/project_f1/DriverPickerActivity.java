package com.example.project_f1;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import com.example.project_f1.models.Driver;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class DriverPickerActivity extends BaseActivity {

    private ThemeManager.TeamTheme theme;
    private final List<Driver> allDrivers = new ArrayList<>();
    private final List<Driver> filtered   = new ArrayList<>();

    private String selectedIdA = null;
    private String selectedIdB = null;

    private LinearLayout listContainer;
    private TextView tvSlotA, tvSlotB;
    private MaterialButton btnCompare;

    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = ThemeManager.applyFullTheme(this);
        allDrivers.addAll(Driver.getAllDrivers());
        filtered.addAll(allDrivers);

        NestedScrollView scroll = new NestedScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(0, 0, 0, dp(32));
        scroll.addView(root);

        buildHeader(root);
        buildSlots(root);
        buildSearch(root);
        buildList(root);

        setContentView(scroll);
        refreshList("");
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private void buildHeader(LinearLayout root) {
        View stripe = new View(this);
        stripe.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(4)));
        stripe.setBackgroundColor(theme.accent);
        root.addView(stripe);

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bp.setMargins(dp(16), dp(48), dp(16), dp(4));
        bar.setLayoutParams(bp);

        MaterialButton btnBack = new MaterialButton(this);
        btnBack.setText("←");
        btnBack.setTextSize(18);
        btnBack.setTextColor(0xFFFFFFFF);
        btnBack.setBackgroundTintList(ColorStateList.valueOf(theme.buttonBg));
        LinearLayout.LayoutParams bbp = new LinearLayout.LayoutParams(dp(44), dp(44));
        bbp.setMargins(0, 0, dp(14), 0);
        btnBack.setLayoutParams(bbp);
        btnBack.setPadding(0, 0, 0, 0);
        btnBack.setCornerRadius(dp(22));
        btnBack.setOnClickListener(v -> finish());
        bar.addView(btnBack);

        LinearLayout titleCol = new LinearLayout(this);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        titleCol.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(this);
        title.setText("COMPARE DRIVERS");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(22);
        title.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        title.setLetterSpacing(0.08f);
        titleCol.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Select two drivers to compare");
        sub.setTextColor(0xFF888888);
        sub.setTextSize(13);
        sub.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        titleCol.addView(sub);

        bar.addView(titleCol);
        root.addView(bar);
    }

    // ── Selection slots ───────────────────────────────────────────────────────

    private void buildSlots(LinearLayout root) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.setMargins(dp(16), dp(16), dp(16), 0);
        row.setLayoutParams(rp);

        tvSlotA = buildSlot("Driver A", theme.accent);
        tvSlotB = buildSlot("Driver B", 0xFF888888);

        row.addView(tvSlotA);

        TextView vs = new TextView(this);
        vs.setText("VS");
        vs.setTextColor(0xFF555555);
        vs.setTextSize(12);
        vs.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        vs.setGravity(Gravity.CENTER);
        vs.setLayoutParams(new LinearLayout.LayoutParams(dp(40),
                LinearLayout.LayoutParams.MATCH_PARENT));
        row.addView(vs);

        row.addView(tvSlotB);

        root.addView(row);

        // Compare button
        btnCompare = new MaterialButton(this);
        btnCompare.setText("COMPARE →");
        btnCompare.setTextSize(14);
        btnCompare.setTextColor(0xFFFFFFFF);
        btnCompare.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        btnCompare.setLetterSpacing(0.1f);
        btnCompare.setBackgroundTintList(ColorStateList.valueOf(theme.buttonBg));
        btnCompare.setCornerRadius(dp(12));
        btnCompare.setEnabled(false);
        btnCompare.setAlpha(0.4f);
        LinearLayout.LayoutParams cbp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(52));
        cbp.setMargins(dp(16), dp(12), dp(16), 0);
        btnCompare.setLayoutParams(cbp);
        btnCompare.setOnClickListener(v -> launchComparison());
        root.addView(btnCompare);
    }

    private TextView buildSlot(String placeholder, int borderColor) {
        TextView tv = new TextView(this);
        tv.setText(placeholder);
        tv.setTextColor(0xFF666666);
        tv.setTextSize(13);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(dp(8), dp(10), dp(8), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(lp);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp(10));
        bg.setColor(theme.cardBg);
        bg.setStroke(dp(1), borderColor);
        tv.setBackground(bg);
        return tv;
    }

    // ── Search bar ────────────────────────────────────────────────────────────

    private void buildSearch(LinearLayout root) {
        EditText search = new EditText(this);
        search.setHint("Search driver...");
        search.setHintTextColor(0xFF555555);
        search.setTextColor(0xFFFFFFFF);
        search.setTextSize(14);
        search.setTypeface(ResourcesCompat.getFont(this, R.font.inter), Typeface.NORMAL);
        search.setPadding(dp(14), dp(12), dp(14), dp(12));
        search.setBackground(null);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        sp.setMargins(dp(16), dp(16), dp(16), dp(4));
        search.setLayoutParams(sp);

        GradientDrawable searchBg = new GradientDrawable();
        searchBg.setShape(GradientDrawable.RECTANGLE);
        searchBg.setCornerRadius(dp(12));
        searchBg.setColor(theme.cardBg);
        searchBg.setStroke(dp(1), ThemeManager.blendColors(theme.cardBg, theme.accent, 0.3f));
        search.setBackground(searchBg);

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                refreshList(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });

        searchField = search;
        root.addView(search);
    }

    // ── Driver list ───────────────────────────────────────────────────────────

    private void buildList(LinearLayout root) {
        TextView sectionLabel = new TextView(this);
        sectionLabel.setText("SELECT DRIVERS");
        sectionLabel.setTextColor(0xFF555555);
        sectionLabel.setTextSize(10);
        sectionLabel.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        sectionLabel.setLetterSpacing(0.15f);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        slp.setMargins(dp(20), dp(8), dp(20), dp(6));
        sectionLabel.setLayoutParams(slp);
        root.addView(sectionLabel);

        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lcp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lcp.setMargins(dp(16), 0, dp(16), 0);
        listContainer.setLayoutParams(lcp);
        root.addView(listContainer);
    }

    private void refreshList(String query) {
        filtered.clear();
        String q = query.toLowerCase().trim();
        for (Driver d : allDrivers) {
            if (q.isEmpty() || d.getFullName().toLowerCase().contains(q)
                    || d.teamName.toLowerCase().contains(q)) {
                filtered.add(d);
            }
        }
        listContainer.removeAllViews();
        for (Driver d : filtered) listContainer.addView(buildDriverRow(d));
    }

    private View buildDriverRow(Driver d) {
        int teamColor = ThemeManager.getTeamTheme(d.teamId).accent;
        boolean isA = d.id.equals(selectedIdA);
        boolean isB = d.id.equals(selectedIdB);

        com.google.android.material.card.MaterialCardView card =
                new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, dp(8));
        card.setLayoutParams(cp);
        card.setRadius(dp(12));
        card.setCardElevation(dp(4));
        card.setCardBackgroundColor(isA || isB
                ? ThemeManager.blendColors(theme.cardBg, teamColor, 0.15f)
                : theme.cardBg);
        card.setStrokeColor(isA ? teamColor : isB ? teamColor : theme.cardStroke);
        card.setStrokeWidth(isA || isB ? dp(2) : dp(1));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(10), dp(12), dp(10));
        card.addView(row);

        // Team color dot
        View dot = new View(this);
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(teamColor);
        dot.setBackground(dotBg);
        LinearLayout.LayoutParams dp2 = new LinearLayout.LayoutParams(dp(10), dp(10));
        dp2.setMargins(0, 0, dp(10), 0);
        dot.setLayoutParams(dp2);
        row.addView(dot);

        // Photo
        ImageView photo = new ImageView(this);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(dp(44), dp(44));
        pp.setMargins(0, 0, dp(12), 0);
        photo.setLayoutParams(pp);
        photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (d.photoResId != 0) photo.setImageResource(d.photoResId);
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(ThemeManager.blendColors(theme.cardBg, teamColor, 0.2f));
        photo.setBackground(circleBg);
        photo.setClipToOutline(true);
        photo.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);
        row.addView(photo);

        // Name + team
        LinearLayout nameCol = new LinearLayout(this);
        nameCol.setOrientation(LinearLayout.VERTICAL);
        nameCol.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView name = new TextView(this);
        name.setText(d.getFullName());
        name.setTextColor(isA || isB ? 0xFFFFFFFF : 0xFFCCCCCC);
        name.setTextSize(15);
        name.setTypeface(ResourcesCompat.getFont(this, R.font.titillium_web), Typeface.BOLD);
        nameCol.addView(name);

        TextView team = new TextView(this);
        team.setText(d.teamName.toUpperCase());
        team.setTextColor(teamColor);
        team.setTextSize(10);
        team.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
        team.setLetterSpacing(0.1f);
        nameCol.addView(team);

        row.addView(nameCol);

        // Selection badge
        if (isA || isB) {
            TextView badge = new TextView(this);
            badge.setText(isA ? "A" : "B");
            badge.setTextColor(0xFFFFFFFF);
            badge.setTextSize(11);
            badge.setTypeface(ResourcesCompat.getFont(this, R.font.barlow_condensed), Typeface.BOLD);
            badge.setGravity(Gravity.CENTER);
            badge.setPadding(dp(8), dp(4), dp(8), dp(4));
            GradientDrawable badgeBg = new GradientDrawable();
            badgeBg.setShape(GradientDrawable.RECTANGLE);
            badgeBg.setCornerRadius(dp(6));
            badgeBg.setColor(teamColor);
            badge.setBackground(badgeBg);
            row.addView(badge);
        }

        card.setOnClickListener(v -> onDriverTapped(d));
        return card;
    }

    private void onDriverTapped(Driver d) {
        if (d.id.equals(selectedIdA)) {
            selectedIdA = null;
        } else if (d.id.equals(selectedIdB)) {
            selectedIdB = null;
        } else if (selectedIdA == null) {
            selectedIdA = d.id;
        } else if (selectedIdB == null) {
            if (!d.id.equals(selectedIdA)) selectedIdB = d.id;
        } else {
            selectedIdA = d.id;
        }
        updateSlots();
        refreshList(searchField != null ? searchField.getText().toString() : "");
    }

    private void updateSlots() {
        Driver a = selectedIdA != null ? Driver.getDriverById(selectedIdA) : null;
        Driver b = selectedIdB != null ? Driver.getDriverById(selectedIdB) : null;

        int colorA = a != null ? ThemeManager.getTeamTheme(a.teamId).accent : theme.accent;
        int colorB = b != null ? ThemeManager.getTeamTheme(b.teamId).accent : 0xFF888888;

        tvSlotA.setText(a != null ? a.lastName.toUpperCase() : "Driver A");
        tvSlotA.setTextColor(a != null ? 0xFFFFFFFF : 0xFF666666);
        updateSlotBorder(tvSlotA, colorA);

        tvSlotB.setText(b != null ? b.lastName.toUpperCase() : "Driver B");
        tvSlotB.setTextColor(b != null ? 0xFFFFFFFF : 0xFF666666);
        updateSlotBorder(tvSlotB, colorB);

        boolean ready = selectedIdA != null && selectedIdB != null;
        btnCompare.setEnabled(ready);
        btnCompare.setAlpha(ready ? 1f : 0.4f);
    }

    private void updateSlotBorder(TextView tv, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp(10));
        bg.setColor(theme.cardBg);
        bg.setStroke(dp(1), color);
        tv.setBackground(bg);
    }

    private void launchComparison() {
        Intent intent = new Intent(this, DriverComparisonActivity.class);
        intent.putExtra(DriverComparisonActivity.EXTRA_DRIVER_A, selectedIdA);
        intent.putExtra(DriverComparisonActivity.EXTRA_DRIVER_B, selectedIdB);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
