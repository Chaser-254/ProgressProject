package com.example.lightpay.API;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class transactAPI {

    private final FirebaseFirestore db;
    private final Context context;

    public transactAPI(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    private void transactionData(String userId, String account, String receiver, String amount, String paymentMethod) {
        DocumentReference userRef = db.collection("users").document(userId);
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("userId", userId);
        transactionData.put("account", account);
        transactionData.put("paymentMethod", paymentMethod);
        transactionData.put("receiver", receiver);
        transactionData.put("amount", amount);
        transactionData.put("timestamp", FieldValue.serverTimestamp());
        db.collection("transactions")
                .add(transactionData)
                .addOnSuccessListener(documentReference -> {
                    String transactionId = documentReference.getId();
                    userRef.update("transactions", FieldValue.arrayUnion(transactionId))
                            .addOnSuccessListener(unused -> Toast.makeText(context, "Transaction successful", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Transaction unsuccessful! Please try again", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void makeTransaction(String account, String receiver, String amount, String paymentMethod, String method) {
        if (Double.parseDouble(amount) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        } else {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DocumentReference userRef = db.collection("users").document(userId);
                userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        double currentBalance = documentSnapshot.getDouble("balance");
                        if (currentBalance >= Double.parseDouble(amount)) {
                            double newBalance = currentBalance - Double.parseDouble(amount);
                            userRef.update("balance", newBalance)
                                    .addOnSuccessListener(unused -> {
                                        transactionData(userId, account, receiver, amount, paymentMethod);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to deduct amount from the account: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "User document does not exist", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        }
    }
}