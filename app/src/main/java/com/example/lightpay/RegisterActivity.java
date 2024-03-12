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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

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
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();

        if (userEmail.isEmpty()){
            email.setError("Email cannot be empty");
        }
        if (userPass.isEmpty()){
            password.setError("Password cannot be empty");
        } else {
            mAuth.createUserWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
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

                        saveToPostgreSQL(userEmail,userPass,userPhone);

                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this, "Registration is not successful" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void saveToPostgreSQL(String userEmail, String userPass, String userPhone) {
        String jdbcUrl = "jdbc:postgresql://localhost:5432/lightPAY";
        String username = "postgres";
        String password = "Manu@254#";
        
        try{
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl,username,password);
            
            String sql = "INSERT INTO users(email,password,phone) VALUES(?,?,?)";
            
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,userEmail);
            statement.setString(2,userPass);
            statement.setString(3,userPhone);
            
            int rowsInserted = statement.executeUpdate();
            if(rowsInserted > 0){
                Toast.makeText(RegisterActivity.this, "Registration saved to database.", Toast.LENGTH_SHORT).show();
            }
            
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e){
            Toast.makeText(RegisterActivity.this, "PostgreSQL JDBC driver not found.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (SQLException e){
            Toast.makeText(RegisterActivity.this, "Database access error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}