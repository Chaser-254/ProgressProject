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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        paybillAPI = new paybillAPI();

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
        Executor executor = ContextCompat.getMainExecutor(requireActivity());
        BiometricPrompt biometricPrompt = new BiometricPrompt(PaybillFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity(), "Authentication error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                String paymentMethod = "PayBill";

                paybillAPI.payBill(userId, paymentMethod, accountNumber, amount);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm your fingerprints to complete the transfer")
                .setSubtitle("Log in using your biometrics credentials")
                .setNegativeButtonText("Use Account Password")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }
}
