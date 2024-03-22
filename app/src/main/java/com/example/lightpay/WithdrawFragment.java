package com.example.lightpay;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.lightpay.API.withdrawalAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
                authorizeBiometrics(paymentMethod,accountNumber,amount);
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
                authorizeBiometrics(paymentMethod,accountNumber,amount);
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
                authorizeBiometrics(paymentMethod,accountNumber,amount);
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

    private void authorizeBiometrics(String paymentMethod,String accountNumber,String amount) {
        this.paymentMethod = paymentMethod;
        this.accountNumber = accountNumber;
        this.amount = amount;
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
                withdrawalAPI.withdraw(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), paymentMethod, accountNumber, Double.parseDouble(amount));
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
}