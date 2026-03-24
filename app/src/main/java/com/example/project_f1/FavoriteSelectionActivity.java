package com.example.project_f1;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_f1.models.Driver;
import com.example.project_f1.models.Team;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class FavoriteSelectionActivity extends AppCompatActivity {
    private RecyclerView rvTeams, rvDrivers;
    private TeamSelectionAdapter teamAdapter;
    private DriverSelectionAdapter driverAdapter;
    private Button btnContinue;
    private ProgressBar progressBar;
    private TextView tvTeamTitle, tvDriverTitle;
    private String selectedTeamId, selectedDriverId;
    private boolean isFromOnboarding = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_favorite_selection);
        ThemeManager.TeamTheme theme = ThemeManager.applyFullTheme(this);
        View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{theme.bgTop, theme.bgBottom});
        rootView.setBackground(bg);

        isFromOnboarding = getIntent().getBooleanExtra("from_onboarding", true);

        rvTeams = findViewById(R.id.rvTeams);
        rvDrivers = findViewById(R.id.rvDrivers);
        btnContinue = findViewById(R.id.btnContinue);
        progressBar = findViewById(R.id.progressBar);
        tvTeamTitle = findViewById(R.id.tvTeamTitle);
        tvDriverTitle = findViewById(R.id.tvDriverTitle);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        // Tint buttons with team theme
        if (btnContinue instanceof MaterialButton)
            ((MaterialButton) btnContinue).setBackgroundTintList(ColorStateList.valueOf(theme.buttonBg));
        if (btnBack != null)
            btnBack.setBackgroundTintList(ColorStateList.valueOf(theme.buttonBg));

        selectedTeamId = FavoriteRepository.getFavoriteTeamId(this);
        selectedDriverId = FavoriteRepository.getFavoriteDriverId(this);

        setupTeamRecyclerView();
        setupDriverRecyclerView();

        btnContinue.setOnClickListener(v -> {
            if (selectedTeamId == null || selectedDriverId == null) {
                Toast.makeText(this, "Please select both team and driver", Toast.LENGTH_SHORT).show();
                return;
            }
            saveFavorites();
            if (isFromOnboarding) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
            finish();
        });

        // Hide back button during mandatory onboarding flow
        if (btnBack != null) {
            if (isFromOnboarding) {
                btnBack.setVisibility(View.GONE);
            } else {
                btnBack.setOnClickListener(v -> finish());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFromOnboarding) super.onBackPressed();
        // Block back during mandatory flow
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setupTeamRecyclerView() {
        List<Team> teams = Team.getAllTeams();
        teamAdapter = new TeamSelectionAdapter(this, teams, selectedTeamId, team -> {
            selectedTeamId = team.id;
            updateContinueButton();
            // Live preview: update background to selected team's theme
            ThemeManager.TeamTheme preview = ThemeManager.getTeamTheme(team.id);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable(
                    android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{preview.bgTop, preview.bgBottom});
            getWindow().setBackgroundDrawable(bg);
            if (btnContinue instanceof MaterialButton)
                ((MaterialButton) btnContinue).setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(preview.buttonBg));
        });

        LinearLayoutManager teamLayout = new LinearLayoutManager(this);
        rvTeams.setLayoutManager(teamLayout);
        rvTeams.setAdapter(teamAdapter);
    }

    private void setupDriverRecyclerView() {
        List<Driver> drivers = Driver.getAllDrivers();
        driverAdapter = new DriverSelectionAdapter(this, drivers, selectedDriverId, driver -> {
            selectedDriverId = driver.id;
            updateContinueButton();
        });

        LinearLayoutManager driverLayout = new LinearLayoutManager(this);
        rvDrivers.setLayoutManager(driverLayout);
        rvDrivers.setAdapter(driverAdapter);
        
        progressBar.setVisibility(View.GONE);
        tvDriverTitle.setVisibility(View.VISIBLE);
        rvDrivers.setVisibility(View.VISIBLE);
    }

    private void updateContinueButton() {
        boolean bothSelected = selectedTeamId != null && selectedDriverId != null;
        btnContinue.setEnabled(bothSelected);
        btnContinue.setAlpha(bothSelected ? 1.0f : 0.5f);
    }

    private void saveFavorites() {
        Team team = Team.getTeamById(selectedTeamId);
        Driver driver = Driver.getDriverById(selectedDriverId);

        FavoriteRepository.setFavoriteTeam(this, selectedTeamId, team != null ? team.name : selectedTeamId);
        FavoriteRepository.setFavoriteDriver(this, selectedDriverId, driver != null ? driver.getFullName() : selectedDriverId);
        ThemeManager.setThemeMode(this, ThemeManager.THEME_TEAM);
    }
}
