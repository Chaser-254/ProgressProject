package com.example.lightpay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lightpay.API.paybillAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class PaybillFragment extends Fragment {

    ImageButton lipaMpesa, billMpesa;
    private paybillAPI paybillAPI;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paybill, container, false);

        mAuth = FirebaseAuth.getInstance();
        paybillAPI = new paybillAPI(requireContext());

        lipaMpesa = view.findViewById(R.id.lipaNaMpesa);
        billMpesa = view.findViewById(R.id.lipaNaPaybill);

        lipaMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LipaNaMpesa();
            }
        });
        billMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payBillMpesa();
            }
        });

        return view;
    }

    private void payBillMpesa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pay With PayBill");
        builder.setIcon(R.drawable.download);

        final EditText inputBusinessNo = new EditText(getActivity());
        inputBusinessNo.setHint("Enter Business No.");
        inputBusinessNo.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAccNo = new EditText(getActivity());
        inputAccNo.setHint("Enter Account Number");

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("PIN");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputBusinessNo);
        layout.addView(inputAccNo);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics(inputBusinessNo.getText().toString().trim(), inputAmount.getText().toString().trim());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void LipaNaMpesa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("LIPA NA MPESA");
        builder.setIcon(R.drawable.lipampesa);

        final EditText inputTillNo = new EditText(getActivity());
        inputTillNo.setHint("Enter Till No.");
        inputTillNo.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("PIN");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputTillNo);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics(inputTillNo.getText().toString().trim(), inputAmount.getText().toString().trim());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void authorizeBiometrics(final String accountNumber, final String amount) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Executor executor = ContextCompat.getMainExecutor(requireActivity());
            BiometricPrompt biometricPrompt = new BiometricPrompt(PaybillFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getActivity(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference userRef = db.collection("users").document(currentUser.getUid());

                    // Create a new transaction record
                    Map<String, Object> transactionData = new HashMap<>();
                    transactionData.put("paymentMethod", "PayBill");
                    transactionData.put("accountNumber", accountNumber);
                    transactionData.put("amount", Double.parseDouble(amount));
                    transactionData.put("timestamp", FieldValue.serverTimestamp());

                    // Add the transaction record to the 'transactions' collection
                    db.collection("payBill").add(transactionData)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getActivity(), "Transaction successful", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to save transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getActivity(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Confirm your fingerprints to complete the transaction")
                    .setSubtitle("Log in using your biometrics credentials")
                    .setNegativeButtonText("Use Account Password")
                    .build();
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}

