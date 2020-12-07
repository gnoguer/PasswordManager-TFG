package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class PasswordGeneratorActivity extends AppCompatActivity {

    private int passLength = 8;
    private String newGeneratedPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);
        setTitle("Password Generator");


        Button generateBtn = findViewById(R.id.generateButton);
        TextView generatedPass = findViewById(R.id.generatedPassTextView);
        SeekBar seekBar = findViewById(R.id.lengthSeekBar);
        TextView lengthNum = findViewById(R.id.lengthNumberTextView);
        Button savePassBtn = findViewById(R.id.saveGeneratedPassButton);

        PasswordGenerator generator = new PasswordGenerator();
        newGeneratedPass = generator.generateStrongPassword(passLength);
        generatedPass.setText(newGeneratedPass);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passLength = progress+8;
                lengthNum.setText(String.valueOf(passLength));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGeneratedPass = generator.generateStrongPassword(passLength);
                generatedPass.setText(newGeneratedPass);
            }
        });


        savePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",newGeneratedPass);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }
}