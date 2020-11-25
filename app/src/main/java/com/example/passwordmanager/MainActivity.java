package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button logInButton = findViewById(R.id.loginButton);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextMasterPassword = findViewById(R.id.editTextMasterPassword);

       logInButton.setOnClickListener(new View.OnClickListener(){
           public void onClick(View view){


           }
       });
    }
}