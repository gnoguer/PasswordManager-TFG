package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PasswordsVaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords_vault);

        FloatingActionButton fab = findViewById(R.id.passwordsFab);

        fab.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Intent intent = new Intent(PasswordsVaultActivity.this, AddPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}