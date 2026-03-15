package com.example.project_f1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class TechAnalysisActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_tech_analysis);
    }
}