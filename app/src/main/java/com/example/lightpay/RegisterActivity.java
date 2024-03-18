package com.example.lightpay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password, phone;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        Button btnRegister = findViewById(R.id.register);
        TextView textRegister1 = findViewById(R.id.text_register1);
        phone = findViewById(R.id.register_Phone);
        progressBar = findViewById(R.id.regProgressBar);

        btnRegister.setOnClickListener(v -> Register());

        textRegister1.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void Register() {
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();

        if (userEmail.isEmpty()) {
            email.setError("Email cannot be empty");
        }
        if (userPass.isEmpty()) {
            password.setError("Password cannot be empty");
        } else {
            mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.VISIBLE);
                    sendEmailVerification(userEmail);
                    saveToFirestore(userEmail, userPass, userPhone);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration is not successful" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sendEmailVerification(String userEmail) {
        Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration successful. Check your email to verify your account", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveToFirestore(String userEmail, String userPass, String userPhone) {
        String hashedPassword = BCrypt.hashpw(userPass,BCrypt.gensalt());
        Map<String, Object> user = new HashMap<>();
        user.put("email", userEmail);
        user.put("password", hashedPassword);
        user.put("phone", userPhone);

        db.collection("users")
                .add(user)
                .addOnCompleteListener((OnCompleteListener<com.google.firebase.firestore.DocumentReference>) task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration saved to Firestore.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error saving registration to Firestore: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}