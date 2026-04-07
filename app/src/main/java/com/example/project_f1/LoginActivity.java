package com.example.project_f1;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.applyTheme(this);
        FavoriteRepository.migrateLegacyPrefs(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null && current.isEmailVerified()) {
            goToMain(current.getDisplayName(), current.getEmail());
            return;
        }

        setContentView(R.layout.activity_login);
        ThemeManager.applyStatusBar(this);

        TextInputEditText etEmail    = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton    btnLogin   = findViewById(R.id.btnLogin);
        MaterialButton    btnSignUp  = findViewById(R.id.btnSignUp);
        MaterialButton    btnForgot  = findViewById(R.id.btnForgotPassword);

        SpringAnimationHelper.attachPressSpring(btnLogin, 0.95);
        btnLogin.setOnClickListener(v -> {
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false);
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    btnLogin.setEnabled(true);
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        mAuth.signOut();
                        Toast.makeText(this, "Please verify your email first", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (user != null) {
                        goToMain(user.getDisplayName(), user.getEmail());
                    }
                });
        });

        btnForgot.setOnClickListener(v -> showForgotPasswordDialog());

        SpringAnimationHelper.attachPressSpring(btnSignUp, 0.95);
        btnSignUp.setOnClickListener(v -> navigate(new Intent(this, SignUpActivity.class)));
    }

    private void showForgotPasswordDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter your email");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("We'll send a reset link to your email.")
            .setView(input)
            .setPositiveButton("Send", (d, w) -> {
                String email = input.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(this, "Enter an email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void goToMain(String name, String email) {
        getSharedPreferences("F1Prefs", MODE_PRIVATE).edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", email)
            .putString("user_name", name != null ? name : "")
            .apply();
        navigate(new Intent(this, MainActivity.class));
        finish();
    }

    private void navigate(Intent intent) {
        startActivity(intent,
            ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_right).toBundle());
    }
}
