package com.example.lightpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email,password,phone;
    private Button btnRegister;
    private TextView textRegister1;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        btnRegister = findViewById(R.id.register);
        textRegister1 = findViewById(R.id.text_register1);
        phone = findViewById(R.id.register_Phone);
        progressBar = findViewById(R.id.regProgressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
        textRegister1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        return;
    }

    private void Register() {
        String user = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (user.isEmpty()){
            email.setError("Email is required");
        }
        if (pass.isEmpty()){
            password.setError("Password cannot be empty");
        } else {
            mAuth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    Toast.makeText(RegisterActivity.this, "Registration successfully,check your email to verify your account", Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this, "Registration is not successful" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}