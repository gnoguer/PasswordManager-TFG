package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AddPasswordActivity extends AppCompatActivity {

    int LAUNCH_SECOND_ACTIVITY = 1;

    EditText serviceName;
    EditText username;
    EditText password;
    Button generatePassBtn;
    EditText note;
    TextView passwordStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        setTitle("Add password");

        Toolbar toolbar = findViewById(R.id.addToolbar);
        setSupportActionBar(toolbar);

        serviceName = findViewById(R.id.editTextServiceName);
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        generatePassBtn = findViewById(R.id.buttonGeneratePassword);
        note = findViewById(R.id.editTextPassNote);
        passwordStrength = findViewById(R.id.passwordStrengthTextView);

        password.addTextChangedListener(passwordEditorWatcher);

        generatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(AddPasswordActivity.this, PasswordGeneratorActivity.class);
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addItem) {
            startActivity(new Intent(getApplicationContext(), PasswordsVaultActivity.class));
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            byte[] decodedKey = Base64.getDecoder().decode(user.getSecret());
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            Log.d("user",user.getSecret());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                String newGeneratedPass = data.getStringExtra("result");
                password.setText(newGeneratedPass);
            }
        }
    }

    private final TextWatcher passwordEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            passwordStrength.setText("Not Entered");
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                passwordStrength.setText("Not Entered");
            else if (s.length() < 6){
                passwordStrength.setText("EASY");
                passwordStrength.setTextColor(Color.rgb(255,165,0));
            }
            else if (s.length() < 10){
                passwordStrength.setText("MEDIUM");
                passwordStrength.setTextColor(Color.YELLOW);
            }
            else{
                passwordStrength.setText("STRONG");
                passwordStrength.setTextColor(Color.GREEN);
            }

        }
    };
}