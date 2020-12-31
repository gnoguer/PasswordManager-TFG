package com.example.passwordmanager.activites.passwords;

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
import com.example.passwordmanager.core.Service;

import java.util.Objects;

public class PasswordsDialog extends AppCompatDialogFragment {

    private final Service service;

    public PasswordsDialog(Service service) {
        this.service = service;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_dialog, null);

        builder.setView(view)
                .setTitle(service.getName())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        TextView usernameTextView = view.findViewById(R.id.showUsernameTextView);
        TextView passwordTextView = view.findViewById(R.id.showPasswordTextView);

        usernameTextView.setText(service.getUsername());
        passwordTextView.setText(service.getPassword());

        return builder.create();

    }

}
