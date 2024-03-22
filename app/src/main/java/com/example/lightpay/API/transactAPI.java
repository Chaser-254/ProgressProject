package com.example.lightpay.API;


import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

public class transactAPI {

    private final FirebaseFirestore db;

    public transactAPI() {
        db = FirebaseFirestore.getInstance();
    }

    private void transactionData(String userId,String account, String receiver, String amount, String paymentMethod) {
        DocumentReference userRef = db.collection("users").document(userId);
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("account", account);
        transactionData.put("paymentMethod", paymentMethod);
        transactionData.put("receiver", receiver);
        transactionData.put("amount", amount);
        transactionData.put("timestamp", FieldValue.serverTimestamp());
        db.collection("transactions")
                .add(transactionData)
                .addOnSuccessListener(documentReference -> {
                    String transactionId = documentReference.getId();
                    userRef.update("transactions",FieldValue.arrayUnion(transactionId))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Transaction successful", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Transaction unsuccessfully! Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private Context getActivity() {
        return null;
    }
    public void makeTransaction(String account, String receiver, String amount, String paymentMethod) {

        if (Double.parseDouble(amount)<0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }else{
            FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser!=null;
            String uid=currentUser.getUid();
            this.transactionData(uid, account, receiver, amount, paymentMethod);


        }
    }
}

