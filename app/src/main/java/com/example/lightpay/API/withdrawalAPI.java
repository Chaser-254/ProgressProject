package com.example.lightpay.API;


import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class withdrawalAPI {
    private final FirebaseFirestore db;

    public withdrawalAPI() {
        db = FirebaseFirestore.getInstance();
    }

    public void withdraw(String userId,String paymentMethod,String accountNumber,double amount) {
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> withdrawalData = new HashMap<>();
        withdrawalData.put("paymentMethod", paymentMethod);
        withdrawalData.put("accountNumber", accountNumber);
        withdrawalData.put("amount", amount);
        withdrawalData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("withdrawals")
                .add(withdrawalData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String withdrawalId = documentReference.getId();
                        userRef.update("withdrawals", FieldValue.arrayUnion(withdrawalId))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "Withdrawal successful", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Failed to update user document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to withdraw: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Context getActivity() {
        return null;
    }
}
