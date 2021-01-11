package com.example.passwordmanager.activites.passwords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.passwordmanager.R;
import com.example.passwordmanager.core.PasswordGenerator;


public class PasswordGeneratorActivity extends AppCompatActivity {

    private int passLength = 8;
    private String newGeneratedPass;
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);
        setTitle("Password Generator");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button generateBtn = findViewById(R.id.generateButton);
        TextView generatedPass = findViewById(R.id.generatedPassTextView);
        SeekBar seekBar = findViewById(R.id.lengthSeekBar);
        TextView lengthNum = findViewById(R.id.lengthNumberTextView);
        Button savePassBtn = findViewById(R.id.saveGeneratedPassButton);

        Switch upperCaseSwitch = findViewById(R.id.AZSwitch);
        Switch lowerCaseSwitch = findViewById(R.id.azSwitch);
        Switch digitsSwitch = findViewById(R.id.digitsSwitch);
        Switch specialSwitch = findViewById(R.id.specialSwitch);


        PasswordGenerator generator = new PasswordGenerator();
        newGeneratedPass = generator.generateStrongPassword(passLength, true, true, true ,true);
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
                boolean upperBoolean = upperCaseSwitch.isChecked();
                boolean lowerBoolean = lowerCaseSwitch.isChecked();
                boolean digitBoolean = digitsSwitch.isChecked();
                boolean specialBoolean = specialSwitch.isChecked();

                newGeneratedPass = generator.generateStrongPassword(passLength, upperBoolean, lowerBoolean, digitBoolean, specialBoolean);
                generatedPass.setText(newGeneratedPass);
            }
        });


        savePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", newGeneratedPass);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }
}