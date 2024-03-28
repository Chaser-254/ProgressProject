package com.example.lightpay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

import com.example.lightpay.API.withdrawalAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class WithdrawFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ImageButton selectedPaymentMethod;
    private com.example.lightpay.API.withdrawalAPI withdrawalAPI;
    private String paymentMethod;
    private String accountNumber;
    private String amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        withdrawalAPI = new withdrawalAPI();

        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);
        ImageButton withdrawMpesa = view.findViewById(R.id.withdrawMpesa);
        ImageButton withdrawBank = view.findViewById(R.id.withdrawBank);
        ImageButton withdrawAirtel = view.findViewById(R.id.withdrawAirtel);

        withdrawMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentMethod = withdrawMpesa;
                showMpesaWithdrawalDialog();
            }
        });
        withdrawBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentMethod = withdrawBank;
                showBankWithdrawalDialog();
            }
        });
        withdrawAirtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentMethod = withdrawAirtel;
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
                authorizeBiometrics(paymentMethod, accountNumber, amount, amount);
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
                authorizeBiometrics(paymentMethod, accountNumber, amount, amount);
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
                authorizeBiometrics(paymentMethod, accountNumber, amount, amount);
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
    private void authorizeBiometrics(String paymentMethod, String accountNumber, String amount, String s) {
        this.paymentMethod = paymentMethod;
        this.accountNumber = accountNumber;
        this.amount = amount;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
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
                    Log.d("WithdrawFragment", "Biometric authentication succeeded");

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference userRef = db.collection("users").document(currentUser.getUid());

                    Query withdrawalQuery = db.collection("deposits").whereEqualTo("userId", currentUser.getUid());

                    withdrawalQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                        double totalDeposits = 0.0;
                        for (QueryDocumentSnapshot depositSnapshot : queryDocumentSnapshots) {
                            Double depositAmount = depositSnapshot.getDouble("amount");
                            totalDeposits += depositAmount != null ? depositAmount : 0.0;
                        }
                        double withdrawalAmount = 0.0; // Default value
                        if (amount != null && !amount.trim().isEmpty()) {
                            withdrawalAmount = Double.parseDouble(amount);
                        } else {
                            // Handle the case where amount is null or empty
                            Toast.makeText(getActivity(), "Invalid amount", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double updatedBalance = totalDeposits - withdrawalAmount;

                        if (updatedBalance < 0) {
                            Toast.makeText(getActivity(), "Insufficient funds", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create a new withdrawal record
                        Map<String, Object> withdrawalData = new HashMap<>();
                        withdrawalData.put("paymentMethod", paymentMethod);
                        withdrawalData.put("accountNumber", accountNumber);
                        withdrawalData.put("amount", withdrawalAmount);
                        withdrawalData.put("timestamp", FieldValue.serverTimestamp());

                        // Add the withdrawal record to the 'withdrawals' collection
                        db.collection("withdrawals").add(withdrawalData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("WithdrawFragment", "Withdrawal data added to Firestore");

                                    // Update the user's deposit balance
                                    userRef.update("amount", updatedBalance)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getActivity(), "Withdrawal Successful", Toast.LENGTH_SHORT).show();
                                                Log.d("WithdrawFragment", "User balance updated successfully");
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "Failed to update balance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.e("WithdrawFragment", "Failed to update user balance: " + e.getMessage());
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to save withdrawal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("WithdrawFragment", "Failed to save withdrawal data: " + e.getMessage());
                                });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to get deposit documents: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("WithdrawFragment", "Failed to get deposit documents: " + e.getMessage());
                    });
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getActivity(), "Authentication failed!", Toast.LENGTH_SHORT).show();
                    Log.d("WithdrawFragment", "Biometric authentication failed");
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Confirm your fingerprints to complete the transaction")
                    .setSubtitle("Log in using your fingerprints credentials")
                    .setNegativeButtonText("Use your Phone/Account Password")
                    .build();
            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(getActivity(), "User not Authenticated", Toast.LENGTH_SHORT).show();
            Log.e("WithdrawFragment", "User not authenticated");
        }
    }

}