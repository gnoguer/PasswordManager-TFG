package com.example.passwordmanager.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.passwordmanager.R;
import com.example.passwordmanager.activites.bankaccounts.BankAccsVaultActivity;
import com.example.passwordmanager.activites.notes.NotesVaultActivity;
import com.example.passwordmanager.activites.passwords.PasswordsVaultActivity;
import com.example.passwordmanager.activites.paymentcards.PaymentCardVaultActivity;

public class VaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);
        setTitle("Vault");

        Toolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        CardView passwordsCardView = findViewById(R.id.passwordsCardView);
        CardView notesCardView = findViewById(R.id.notesCardView);
        CardView paymentCardsCardView = findViewById(R.id.paymentCardsCardView);
        CardView bankAccountsCardView = findViewById(R.id.bankAccountsCardView);
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

        bankAccountsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VaultActivity.this, BankAccsVaultActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsItem) {
            startActivity(new Intent(VaultActivity.this, LeaksSettingsActivity.class));
            }

        return true;
    }
}