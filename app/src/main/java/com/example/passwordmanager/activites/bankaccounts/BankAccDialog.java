package com.example.passwordmanager.activites.bankaccounts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.passwordmanager.R;
import com.example.passwordmanager.core.BankAccount;
import com.example.passwordmanager.core.PaymentCard;

import java.util.Objects;

public class BankAccDialog extends AppCompatDialogFragment {

    private final BankAccount bankAccount;

    public BankAccDialog(BankAccount bankAccount){
        this.bankAccount = bankAccount;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bank_account_dialog, null);

        builder.setView(view)
                .setTitle(bankAccount.getName())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        TextView IBAN = view.findViewById(R.id.dialogIBANTextView);
        TextView PIN = view.findViewById(R.id.dialogPINTextView);


        IBAN.setText(bankAccount.getIBAN());
        PIN.setText(bankAccount.getPIN());

        return builder.create();

    }
}
