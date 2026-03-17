package com.example.project_f1;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        SharedPreferences prefs = getSharedPreferences("F1Prefs", MODE_PRIVATE);
        if (prefs.getBoolean("is_logged_in", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_login);
        ThemeManager.applyStatusBar(this);

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserRepository.User user = UserRepository.login(this, email, password);
                if (user == null) {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                prefs.edit()
                    .putBoolean("is_logged_in", true)
                    .putInt("user_id", user.id)
                    .putString("user_email", user.email)
                    .putString("user_name", user.name)
                    .putString("knowledge_level", user.level)
                    .apply();

                navigate(new Intent(this, MainActivity.class));
                finish();
            });
        });

        btnSignUp.setOnClickListener(v -> navigate(new Intent(this, SignUpActivity.class)));
    }

    private void navigate(Intent intent) {
        startActivity(intent,
            ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.fade_in).toBundle());
    }
}
