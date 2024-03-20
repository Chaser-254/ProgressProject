package com.example.lightpay;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SupportFragment extends Fragment {

    private FirebaseFirestore db;
    EditText emailEditText,titleEditText,bodyEditText;
    Button sendBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        db = FirebaseFirestore.getInstance();

        emailEditText = view.findViewById(R.id.emailEditText);
        titleEditText = view.findViewById(R.id.titleEditText);
        bodyEditText = view.findViewById(R.id.bodyEditText);

        sendBtn = view.findViewById(R.id.sendButton);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String title = titleEditText.getText().toString();
                String body = bodyEditText.getText().toString();

                if (email.isEmpty() || title.isEmpty() || body.isEmpty()){
                    Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String,Object> user = new HashMap<>();
                user.put("email",email);
                user.put("title",title);
                user.put("body",body);
                
                db.collection("Emails")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "onSuccess: Email sent!" + documentReference.getId());
                                emailEditText.setText("");
                                titleEditText.setText("");
                                bodyEditText.setText("");

                                Toast.makeText(getContext(), "Email sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG,"Error sending message",e);
                                Toast.makeText(getContext(), "Failed to send email. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return view;
    }
}