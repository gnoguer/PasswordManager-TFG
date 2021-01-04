package com.example.passwordmanager.activites.paymentcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.passwordmanager.R;
import com.example.passwordmanager.core.PaymentCard;

import java.util.Objects;

public class PaymentCardDialog extends AppCompatDialogFragment {

    private final PaymentCard paymentCard;

    public PaymentCardDialog(PaymentCard paymentCard){
        this.paymentCard = paymentCard;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_payment_card_dialog, null);

        builder.setView(view)
                .setTitle(paymentCard.getName())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        TextView nameOnCard = view.findViewById(R.id.dialogNameOnCardTextView);
        TextView number = view.findViewById(R.id.dialogNumberTextView);
        TextView securityCode = view.findViewById(R.id.dialogSecurityCodeTextView);
        TextView expirationDate = view.findViewById(R.id.dialogExpirationDateTextView);

        nameOnCard.setText(paymentCard.getNameOnCard());
        number.setText(paymentCard.getNumber());
        securityCode.setText(paymentCard.getSecurityCode());
        expirationDate.setText(paymentCard.getExpirationDate());

        return builder.create();

    }

}