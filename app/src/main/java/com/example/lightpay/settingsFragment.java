package com.example.lightpay;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class settingsFragment extends Fragment {
    private SwitchCompat privateAccountSwitch;
    private AppCompatButton edit;
    TextView userData;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    private boolean isNightModeEnabled = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userData = view.findViewById(R.id.userView);

        edit = view.findViewById(R.id.buttonsEditProfile);
        ImageView nightModeImageView = view.findViewById(R.id.night_mode);
        ImageView notificationImageView = view.findViewById(R.id.setting_notification);
        ImageView privateAccountImageView = view.findViewById(R.id.private_account);
        ImageView securityImageView = view.findViewById(R.id.security);
        ImageView textSizeImageView = view.findViewById(R.id.textSizeIcon);
        ImageView languageImageView = view.findViewById(R.id.languageIcon);
        ImageView messaegImageView = view.findViewById(R.id.messageIcon);
        ImageView faqImageView = view.findViewById(R.id.faq_Icon);
        ImageView logoutImageView = view.findViewById(R.id.icon_logout);

        SwitchCompat nightModeSwitch = view.findViewById(R.id.night_mode_switch);
        SwitchCompat notificationSwitch = view.findViewById(R.id.notification_switch);
        SwitchCompat privateAccountSwitch = view.findViewById(R.id.private_switch);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        isNightModeEnabled = prefs.getBoolean("night_mode_enabled", false);
        nightModeSwitch.setChecked(isNightModeEnabled);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            userData.setText(currentUser.getEmail());
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isNightModeEnabled = isChecked;
                applyNightMode(isChecked);
            }
        });
        notificationImageView.setOnClickListener(v -> {
            //implement method here
        });
        privateAccountImageView.setOnClickListener(v -> {
            //implement method here
        });
        securityImageView.setOnClickListener(v -> {
            //implement method here
        });
        textSizeImageView.setOnClickListener(v -> {
            //implement method here
        });
        languageImageView.setOnClickListener(v -> {
            //implement method here
        });
        messaegImageView.setOnClickListener(v -> {
            //implement method here
        });
        faqImageView.setOnClickListener(v -> {
            //implement method here
        });
        logoutImageView.setOnClickListener(v -> {
            //implement method here
        });

        return view;
    }

    private void applyNightMode(boolean isNightModeEnabled) {
        int mode = isNightModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
        editor.putBoolean("night_mode_enabled", isNightModeEnabled);
        editor.apply();
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        dialogBuilder.setView(dialogView);

        EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            editTextUsername.setText(currentUser.getDisplayName());
            editTextEmail.setText(currentUser.getEmail());
        }

        dialogBuilder.setTitle("Edit Profile");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newUsername = editTextUsername.getText().toString().trim();
                String newEmail = editTextEmail.getText().toString().trim();
                updateUserProfile(newEmail,newUsername);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    private void updateUserProfile(String newUsername, String newEmail) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.updateEmail(newEmail)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        private WindowDecorActionBar.TabImpl textViewEmail;

                        @Override
                        public void onSuccess(Void aVoid) {
                            // Email updated successfully, now update username in Firestore
                            db.collection("users").document(currentUser.getUid())
                                    .update("username", newUsername, "email", newEmail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @SuppressLint("RestrictedApi")
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            // Update TextViews with new username and email
                                            textViewEmail.setText(newEmail);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(requireContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}