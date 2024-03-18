package com.example.lightpay;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class depositFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deposit, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ImageButton creditCardButton = view.findViewById(R.id.creditCardButton);
        creditCardButton.setOnClickListener(v -> showMpesaDialog());

        ImageButton debitCardButton = view.findViewById(R.id.debitCardButton);
        debitCardButton.setOnClickListener(v -> showDebitDialog());

        ImageButton AirtelMoney = view.findViewById(R.id.airtelButton);
        AirtelMoney.setOnClickListener(v -> showAirtelDialog());

        return view;
    }

    private void showAirtelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deposit using Airtel Money");
        builder.setIcon(R.drawable.airtel);

        final EditText inputPhone = new EditText(getActivity());
        inputPhone.setHint("Enter your Airtel Number");
        inputPhone.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("PIN");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputPhone);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", (dialog, which) -> authorizeBiometrics(inputPhone.getText().toString().trim(), inputAmount.getText().toString().trim()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDebitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deposit with Debit card");
        builder.setIcon(R.drawable.ic_mastercard);

        final EditText inputAccount = new EditText(getActivity());
        inputAccount.setHint("Debit Card Number");
        inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPIN = new EditText(getActivity());
        inputPIN.setHint("PIN");
        inputPIN.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccount);
        layout.addView(inputAmount);
        layout.addView(inputPIN);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", (dialog, which) -> authorizeBiometrics(inputAccount.getText().toString().trim(), inputAmount.getText().toString().trim()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showMpesaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deposit with MPESA");
        builder.setIcon(R.drawable.mpesa);

        final EditText inputAccountNumber = new EditText(getActivity());
        inputAccountNumber.setHint("Account Number");
        inputAccountNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPhone = new EditText(getActivity());
        inputPhone.setHint("Enter MPESA mobile number");
        inputPhone.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("PIN");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccountNumber);
        layout.addView(inputAmount);
        layout.addView(inputPhone);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> authorizeBiometrics(inputAccountNumber.getText().toString().trim(), inputAmount.getText().toString().trim()));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void authorizeBiometrics(final String accountNumber, final String amount) {
        Executor executor = ContextCompat.getMainExecutor(requireActivity());
        BiometricPrompt biometricPrompt = new BiometricPrompt(depositFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                depositToFirestore(accountNumber, amount);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity(), "Authentication failed!", Toast.LENGTH_SHORT).show();
            }
        });
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm your fingerprints to complete the transaction")
                .setSubtitle("Log in using your fingerprints credentials")
                .setNegativeButtonText("Use your Phone/Account Password")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    private void depositToFirestore(String accountNumber, String amount) {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> depositData = new HashMap<>();
        depositData.put("accountNumber", accountNumber);
        depositData.put("amount", Double.parseDouble(amount));
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
                                        Toast.makeText(getActivity(), "Failed to update user document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to deposit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
