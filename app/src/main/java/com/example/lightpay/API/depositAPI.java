package com.example.lightpay.API;


import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

public class depositAPI {

    private final FirebaseFirestore db;

    public depositAPI() {
        db = FirebaseFirestore.getInstance();
    }

    public void deposit(String userId, String paymentMethod, String accountNumber, double amount) {
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> depositData = new HashMap<>();
        depositData.put("paymentMethod", paymentMethod);
        depositData.put("accountNumber", accountNumber);
        depositData.put("amount", amount);
        depositData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("deposits")
                .add(depositData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String depositId = documentReference.getId();
                        userRef.update("deposits", FieldValue.arrayUnion(depositId))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getActivity(), "Deposit successful", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Deposit unsuccessfully: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to update user document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Context getActivity() {
        return null;
    }
}

