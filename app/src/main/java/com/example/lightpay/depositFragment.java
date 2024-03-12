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
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class depositFragment extends Fragment {

    private ImageButton selectedPaymentMethodButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deposit, container, false);

        ImageButton creditCardButton = view.findViewById(R.id.creditCardButton);
        creditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentMethodButton = creditCardButton;
                showAmountDialog();
            }
        });

        ImageButton debitCardButton = view.findViewById(R.id.debitCardButton);
        debitCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentMethodButton = debitCardButton;
                showAmountDialog();
            }
        });

        ImageButton AirtelMoney = view.findViewById(R.id.airtelButton);
        AirtelMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPaymentMethodButton = AirtelMoney;
                showAmountDialog();
            }
        });


        return view;
    }

    private void showAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Amount");

        final EditText amountEditText = new EditText(getContext());
        amountEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(amountEditText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String amount = amountEditText.getText().toString();
                Toast.makeText(getContext(), "Amount entered: Ksh." + amount, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
