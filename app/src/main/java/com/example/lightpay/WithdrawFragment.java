package com.example.lightpay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class WithdrawFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageButton selectedPaymentMethod;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);
        ImageButton withdrawMpesa = view.findViewById(R.id.withdrawMpesa);
        ImageButton withdrawBank = view.findViewById(R.id.withdrawBank);
        ImageButton withdrawAirtel = view.findViewById(R.id.withdrawAirtel);

        withdrawMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMpesaWithdrawalDialog();
            }
        });
        withdrawBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankWithdrawalDialog();
            }
        });
        withdrawAirtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAirtelWithdrawalDialog();
            }
        });

        return view;
    }

    private void showAirtelWithdrawalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Withdraw Using Airtel Money");
        builder.setIcon(R.drawable.airtel);

        final EditText inputAccount = new EditText(getActivity());
        inputAccount.setHint("Enter Account Number");
        inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPhone = new EditText(getActivity());
        inputPhone.setHint("Enter phone number");
        inputPhone.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("Enter PIN");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccount);
        layout.addView(inputPhone);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics();
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

    private void showBankWithdrawalDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Withdraw to Bank Account");
        builder.setIcon(R.drawable.ic_mastercard);

        final EditText inputAccount = new EditText(getActivity());
        inputAccount.setHint("Enter Account Number");
        inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputBankNo = new EditText(getActivity());
        inputBankNo.setHint("Enter Debit card No.");
        inputBankNo.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("Enter PIN");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccount);
        layout.addView(inputBankNo);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics();
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

    private void showMpesaWithdrawalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Withdraw using MPESA");
        builder.setIcon(R.drawable.pesa);

        final EditText inputAccount = new EditText(getActivity());
        inputAccount.setHint("Enter Account Number to withdraw");
        inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPhone = new EditText(getActivity());
        inputPhone.setHint("Enter Mobile Number");
        inputPhone.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");
        inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("Enter PIN");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccount);
        layout.addView(inputPhone);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics();
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

    private void authorizeBiometrics() {
        Executor executor = ContextCompat.getMainExecutor(requireActivity());
        BiometricPrompt biometricPrompt = new BiometricPrompt(WithdrawFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity(), "Authentication error" + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getActivity(), "Authentication successfully", Toast.LENGTH_SHORT).show();
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

    //API o handle withdrawal and update the balances in the user account
    private void withdrawToFirestore(String paymentMethod, String accountNumber, String amount) {

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DocumentReference userRef = db.collection("users").document(userId);


        Map<String, Object> withdrawalData = new HashMap<>();
        withdrawalData.put("paymentMethod", paymentMethod);
        withdrawalData.put("accountNumber", accountNumber);
        withdrawalData.put("amount", Double.parseDouble(amount));
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