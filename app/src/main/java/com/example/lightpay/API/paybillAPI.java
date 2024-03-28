package com.example.lightpay.API;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class paybillAPI {

    private final FirebaseFirestore db;
    private final Context context;

    public paybillAPI(Context context) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
        db = FirebaseFirestore.getInstance();
    }

    public void payBill(String userId, String paymentMethod, String accountNumber, String amount) {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Double currentBalance = document.getDouble("deposits");
                        if (currentBalance != null) {
                            double paymentAmount = Double.parseDouble(amount);

                            // Deduct payment amount from current balance
                            double updatedBalance = currentBalance - paymentAmount;

                            // Update balance in Firestore
                            userRef.update("deposits", updatedBalance)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed to update balance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "Balance field not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "User document not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to retrieve user document: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
