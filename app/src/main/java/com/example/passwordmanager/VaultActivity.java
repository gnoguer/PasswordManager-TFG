package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class VaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);
        setTitle("Vault");

        Toolbar toolbar = findViewById(R.id.vaultToolbar);
        setSupportActionBar(toolbar);

        CardView passwordsCardView = findViewById(R.id.passwordsCardView);

        passwordsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VaultActivity.this, PasswordsVaultActivity.class);
                startActivity(intent);
            }
        });
    }
}