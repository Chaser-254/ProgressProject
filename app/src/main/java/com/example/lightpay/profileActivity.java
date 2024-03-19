package com.example.lightpay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.Objects;

public class profileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    TextView textViewUserId,textViewEmail,textViewBalance;
    ImageView imageViewerProfilePicture;
    Button btnUploadPicture;
    ImageButton backBtn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        textViewUserId = findViewById(R.id.textViewUserId);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewBalance = findViewById(R.id.textViewBalance);
        imageViewerProfilePicture = findViewById(R.id.imageViewProfilePicture);
        btnUploadPicture = findViewById(R.id.buttonUploadPicture);
        backBtn = findViewById(R.id.back_search_btn);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            textViewUserId.setText(currentUser.getUid());
            textViewEmail.setText(currentUser.getEmail());
            retrieveBalance(currentUser.getUid());
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageViewerProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void retrieveBalance(String userId) {
        db.collection("deposits").whereEqualTo("userId",userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                         double totalBalance = 0.0;

                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                            if (document.contains("amount")){
                                double amount = document.getDouble("amount");
                                totalBalance += amount;
                            }
                        }

                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        textViewBalance.setText("Ksh." + decimalFormat.format(totalBalance));
                    } else {
                        textViewBalance.setText("Error retrieving balance");
                    }
                });
    }

    private void uploadImage() {

        if (imageUri != null){
            StorageReference fileReference = storageReference.child("profile_pictures/" + mAuth.getCurrentUser().getUid());
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(profileActivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(profileActivity.this, "Upload failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            imageViewerProfilePicture.setImageURI(imageUri);
        }
    }
}