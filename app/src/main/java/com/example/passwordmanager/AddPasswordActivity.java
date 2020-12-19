package com.example.passwordmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

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
            try {
                savePassword();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validInputs(){

        final String strServiceName = serviceName.getText().toString();
        final String strUserName = username.getText().toString();
        final String strPassword = password.getText().toString();
        final String strNote = note.getText().toString();

        if (TextUtils.isEmpty(strServiceName)) {
            serviceName.setError("Please enter your service name");
            serviceName.requestFocus();
            return false;
        }

//        if (TextUtils.isEmpty(strUserName)) {
//            username.setError("Please enter your username");
//            username.requestFocus();
//            return false;
//        }

        if (TextUtils.isEmpty(strPassword)) {
            password.setError("Please enter your password");
            password.requestFocus();
            return false;
        }

//        if (TextUtils.isEmpty(strNote)) {
//            note.setError("Please enter your password");
//            note.requestFocus();
//            return false;
//        }

        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void savePassword() throws GeneralSecurityException, IOException {

        if(validInputs()){

            String strPass = password.getText().toString();
            Log.d("user", strPass);

            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            Log.d("user",user.getSecret());

            //Encryption of the password
            Crypter crypter = new Crypter();
            String strEncryptedPass = crypter.encrypt(strPass,user.getSecret());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SAVE_PASSWORD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);
                                //if no error in response
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), PasswordsVaultActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("userId", String.valueOf(user.getId()));
                    params.put("name", String.valueOf(serviceName.getText()));
                    params.put("password", strEncryptedPass);
                    params.put("note", String.valueOf(note.getText()));

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), PasswordsVaultActivity.class));
    }
}