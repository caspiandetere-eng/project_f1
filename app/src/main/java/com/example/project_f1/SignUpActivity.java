package com.example.project_f1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_signup);

        TextInputEditText etName = findViewById(R.id.etName);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(this, KnowledgeLevelActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }
}
