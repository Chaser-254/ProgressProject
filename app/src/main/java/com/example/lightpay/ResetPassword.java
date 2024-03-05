package com.example.lightpay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ResetPassword extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText userEmail;
    private Button forgetPassword;
    private final static String TAG = "ResetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        progressBar = findViewById(R.id.progressBar);
        userEmail = findViewById(R.id.reset_email);
        forgetPassword = findViewById(R.id.btnForgotPass);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Toast.makeText(ResetPassword.this, "Password has been send to your Email address", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(ResetPassword.this, LoginActivity.class);

                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }else{
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                userEmail.setError("User does not exist!! Invalid user email address");
                            }catch (Exception e){
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(ResetPassword.this, "Something went wrong,try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }
}