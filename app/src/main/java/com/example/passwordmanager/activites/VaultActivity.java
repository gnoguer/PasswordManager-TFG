package com.example.passwordmanager.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.passwordmanager.R;
import com.example.passwordmanager.activites.notes.NotesVaultActivity;
import com.example.passwordmanager.activites.passwords.PasswordsVaultActivity;
import com.example.passwordmanager.activites.paymentcards.PaymentCardVaultActivity;

public class VaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);
        setTitle("Vault");
        CardView passwordsCardView = findViewById(R.id.passwordsCardView);
        CardView notesCardView = findViewById(R.id.notesCardView);
        CardView paymentCardsCardView = findViewById(R.id.paymentCardsCardView);
        passwordsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VaultActivity.this, PasswordsVaultActivity.class);
                startActivity(intent);
            }
        });

        notesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VaultActivity.this, NotesVaultActivity.class);
                startActivity(intent);
            }
        });

        paymentCardsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VaultActivity.this, PaymentCardVaultActivity.class);
                startActivity(intent);
            }
        });
    }
}