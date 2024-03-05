package com.example.lightpay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;


public class LogoutFragment extends Fragment {

    private Button button;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_logout,container,false);
         button = view.findViewById(R.id.logout_button);
         progressBar = view.findViewById(R.id.progressBar);
         mAuth = FirebaseAuth.getInstance();

         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 progressBar.setVisibility(View.VISIBLE);
                 mAuth.signOut();
                 Intent intent = new Intent(getActivity(),LoginActivity.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 startActivity(intent);
                 getActivity().finish();
             }
         });
         return view;
    }
}