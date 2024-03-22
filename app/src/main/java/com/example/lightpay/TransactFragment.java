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

import com.example.lightpay.API.transactAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class TransactFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageButton selectedSendMoneyOptions;
    private com.example.lightpay.API.transactAPI transactAPI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transact, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        transactAPI = new transactAPI();

        ImageButton sendWithMpesa = view.findViewById(R.id.SendMpesaCardButton);
        ImageButton sendWithBank = view.findViewById(R.id.sendDebitCardButton);
        ImageButton sendWithAirtel = view.findViewById(R.id.sendAirtelButton);

        sendWithMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendMpesaDialog();
            }
        });
        sendWithBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendWithBankDialog();
            }
        });
        sendWithAirtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendWithAirtelDialog();
            }
        });

        return view;
    }

    private void showSendWithBankDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Send Money using Bank");
        builder.setIcon(R.drawable.ic_mastercard);

        final EditText inputAccount = new EditText(getActivity());
        inputAccount.setHint("Enter Account");
        inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputSendTo = new EditText(getActivity());
        inputSendTo.setHint("Enter Receiver A/C");
        inputSendTo.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("Enter your pin");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccount);
        layout.addView(inputSendTo);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics("Debit Card", inputAccount.getText().toString().trim(), inputSendTo.getText().toString().trim(), inputAmount.getText().toString().trim());
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

    private void showSendWithAirtelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Send Money using Airtel Money");
        builder.setIcon(R.drawable.airtel);

        final EditText inputAccount = new EditText(getActivity());
        inputAccount.setHint("Enter Account");
        inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputSendTo = new EditText(getActivity());
        inputSendTo.setHint("Enter Receiver No.");
        inputSendTo.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("Enter your pin");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccount);
        layout.addView(inputSendTo);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics("Airtel Money", inputAccount.getText().toString().trim(), inputSendTo.getText().toString().trim(), inputAmount.getText().toString().trim());
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

    private void showSendMpesaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Send Money using MPESA");
        builder.setIcon(R.drawable.mpesa);

        final EditText inputAccount = new EditText(getActivity());
        inputAccount.setHint("Enter Account");
        inputAccount.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputSendTo = new EditText(getActivity());
        inputSendTo.setHint("Enter Receiver No.");
        inputSendTo.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText inputAmount = new EditText(getActivity());
        inputAmount.setHint("Enter Amount");

        final EditText inputPin = new EditText(getActivity());
        inputPin.setHint("Enter your pin");
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputAccount);
        layout.addView(inputSendTo);
        layout.addView(inputAmount);
        layout.addView(inputPin);
        builder.setView(layout);

        builder.setPositiveButton("Authorize", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authorizeBiometrics("MPESA", inputAccount.getText().toString().trim(), inputSendTo.getText().toString().trim(), inputAmount.getText().toString().trim());
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

    private void authorizeBiometrics(final String paymentMethod, final String account, final String receiver, final String amount) {
        Executor executor = ContextCompat.getMainExecutor(requireActivity());
        BiometricPrompt biometricPrompt = new BiometricPrompt(TransactFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity(), "Authentication error" + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                transactAPI.makeTransaction(account, receiver, amount, paymentMethod);
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
