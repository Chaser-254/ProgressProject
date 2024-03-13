package com.example.lightpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import java.util.concurrent.Executor;

public class HomeFragment extends Fragment {

    ImageButton linkDeposit, linkTransfer, linkWithdraw;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        linkDeposit = view.findViewById(R.id.linkDeposit);
        linkTransfer = view.findViewById(R.id.linkTransfer);
        linkWithdraw = view.findViewById(R.id.linkWithdrawal);

        linkDeposit.setOnClickListener(v -> PerformDeposit());
        linkWithdraw.setOnClickListener(v -> PerformWithdrawal());
        linkTransfer.setOnClickListener(v -> PerformTransfer());

        return view;
    }

    private void PerformDeposit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Deposit");

        final EditText inputAccountNumber = new EditText(getActivity());
        inputAccountNumber.setHint("Account Number");
        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Amount");
        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("PIN");

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccountNumber);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            authorizeBiometrics();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void sendSmsNotification(String accountNumber, double amount, double accountBalance, String transfer) {
        SmsManager smsManager = SmsManager.getDefault();
        String message = "";
        String phoneNumber = "1234567890";
        
        if (transfer.equals("deposit")) {
            message = "Your account " + accountNumber + " has been credited with " + amount + " and your new balance is " + accountBalance;
        } else if (transfer.equals("withdraw")) {
            message = "Your account " + accountNumber + " has been debited with " + amount + " and your new balance is " + accountBalance;
        } else if (transfer.equals("transfer")) {
            message = "Your account " + accountNumber + " has been debited with " + amount + " and your new balance is " + accountBalance + ". You have transferred " + amount + " to account " + transfer;
        } else {
            message = "Invalid transfer type";
        }
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(getContext(), "SMS notification sent", Toast.LENGTH_SHORT).show();
    }

    private void PerformTransfer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Transfer From: ");
         final EditText inputTransferFrom = new EditText(getActivity());
         inputTransferFrom.setHint("Transfer from Account");
         builder.setView(inputTransferFrom);
         builder.setPositiveButton("Next", (dialog, which) -> PromptTransferTo());
         builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
         builder.show();
    }

    private void PromptTransferTo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Transfer To:");

        final EditText inputTransferTo = new EditText(getActivity());
        inputTransferTo.setHint("Transfer To Account");
        builder.setView(inputTransferTo);

        builder.setPositiveButton("Next", (dialog, which) -> PromptAmount());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void PromptAmount() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        builder.setTitle("Amount:");

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");
        builder.setView(inputAmount);

        builder.setPositiveButton("Authorize", (dialog, which) -> authorizeBiometrics());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void authorizeBiometrics() {
        Executor executor = ContextCompat.getMainExecutor(requireActivity());
        BiometricPrompt biometricPrompt = new BiometricPrompt(HomeFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity(), "Authentication error:" + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getActivity(), "Authentication successfully", Toast.LENGTH_SHORT).show();

                String fromAccount="123456789";
                String toAccount = "98765432";
                double amount = 300.00;
                double accountBalance = 200.00;

                sendSmsNotification(fromAccount, amount,accountBalance,"transfer");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm your fingerprints to complete the transfer")
                .setSubtitle("Log in using your biometrics credentials")
                .setNegativeButtonText("Use Account Password")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    private void PerformWithdrawal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Withdrawal");

        final EditText inputAccountNumber = new EditText(getActivity());
        inputAccountNumber.setHint("Enter Account Number to withdraw from");
         final EditText inputAmount = new EditText(getActivity());
         inputAmount.setHint("Enter Amount");

         LinearLayout layout = new LinearLayout(getActivity());
         layout.setOrientation(LinearLayout.VERTICAL);
         layout.addView(inputAccountNumber);
         layout.addView(inputAmount);
         builder.setView(layout);

         builder.setPositiveButton("Authorize", (dialog, which) -> {
             authorizeBiometrics();

             String accountNumber = "123456789";
             double amount = 30.00;
             double accountBalance = 420.00;

             sendSmsNotification(accountNumber,amount,accountBalance,"withdrawal");
         });
         builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
         builder.show();
    }
}