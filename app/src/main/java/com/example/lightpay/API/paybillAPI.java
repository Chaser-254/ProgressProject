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
import java.util.Objects;

public class paybillAPI {

    private final FirebaseFirestore db;

    public paybillAPI(){
        db = FirebaseFirestore.getInstance();
    }

    public void payBill(String userId, String paymentMethod, String accountNumber, String amount) {
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> paybillData = new HashMap<>();
        paybillData.put("paymentMethod", paymentMethod);
        paybillData.put("accountNumber", accountNumber);
        paybillData.put("amount", Double.parseDouble(amount));
        paybillData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("payBills")
                .add(paybillData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String paybillId = documentReference.getId();
                        userRef.update("payBills", FieldValue.arrayUnion(paybillId))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getActivity(), "Paybill payment successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), "Paybill transaction not successful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to complete the transaction" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Context getActivity() {
        return null;
    }
}
