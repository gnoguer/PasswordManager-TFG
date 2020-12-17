package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PasswordsVaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords_vault);
        setTitle("Passwords Vault");

        FloatingActionButton fab = findViewById(R.id.passwordsFab);
        Toolbar toolbar = findViewById(R.id.passVaultToolbar);

        setSupportActionBar(toolbar);

        ArrayList<Service> services = new ArrayList<>();


        fab.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Intent intent = new Intent(PasswordsVaultActivity.this, AddPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}