package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class PasswordGeneratorActivity extends AppCompatActivity {

    private int passLength = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);
        setTitle("Password Generator");


        Button generateBtn = findViewById(R.id.generateButton);
        TextView generatedPass = findViewById(R.id.generatedPassTextView);
        SeekBar seekBar = findViewById(R.id.lengthSeekBar);
        TextView lengthNum = findViewById(R.id.lengthNumberTextView);

        PasswordGenerator generator = new PasswordGenerator();
        generatedPass.setText(generator.generateStrongPassword(passLength));


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
                generatedPass.setText(generator.generateStrongPassword(passLength));
            }
        });



    }
}