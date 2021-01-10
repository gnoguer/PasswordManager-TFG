package com.example.passwordmanager.user;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.passwordmanager.AlarmReceiver;
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.R;
import com.example.passwordmanager.activites.VaultActivity;
import com.example.passwordmanager.requests.URLs;
import com.example.passwordmanager.requests.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    Button logInButton;
    EditText editTextEmail;
    EditText editTextMasterPassword;
    TextView textViewCreateAccount;

    String email;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInButton = findViewById(R.id.loginButton);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMasterPassword = findViewById(R.id.editTextMasterPassword);
        textViewCreateAccount = findViewById(R.id.textViewCreateAccount);


       logInButton.setOnClickListener(new View.OnClickListener(){
           @RequiresApi(api = Build.VERSION_CODES.O)
           public void onClick(View view){
               try {
                   login();
               } catch (GeneralSecurityException | IOException e) {
                   e.printStackTrace();
               }
           }
       });

       textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(), SignupActivity.class));
           }
       });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void login() throws GeneralSecurityException, IOException {

        final String email = editTextEmail.getText().toString();
        final String password = editTextMasterPassword.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your username");
            editTextMasterPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextEmail.setError("Please enter your password");
            editTextMasterPassword.requestFocus();
            return;
        }

        String secretKeyString = Crypter.getInstance(getApplicationContext()).generateKey(password, email.getBytes());
        SharedPrefManager.getInstance(getApplicationContext()).saveUserKey(secretKeyString);

        String encryptedPassword = Crypter.getInstance(getApplicationContext()).encrypt(password);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getBoolean("error")) {

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("email"),
                                        secretKeyString
                                );

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                finish();
                                startActivity(new Intent(getApplicationContext(), VaultActivity.class));

                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException | GeneralSecurityException | IOException e) {
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
                params.put("email", email);
                params.put("password", encryptedPassword);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }




}