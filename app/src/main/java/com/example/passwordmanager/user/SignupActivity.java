package com.example.passwordmanager.user;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.R;
import com.example.passwordmanager.requests.URLs;
import com.example.passwordmanager.requests.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText editTextSignupEmail;
    EditText editTextSignupPassword;
    EditText editTextConfirmPassword;
    Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextSignupEmail = findViewById(R.id.editTextSignupEmail);
        editTextSignupPassword = findViewById(R.id.editTextSignupPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnCreateAccount = findViewById(R.id.buttonCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    register();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean validInputs(){

        String email = editTextSignupEmail.getText().toString().trim();
        String masterPass = editTextSignupPassword.getText().toString().trim();
        String confirmMasterPass = editTextConfirmPassword.getText().toString().trim();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextSignupEmail.setError("Enter a valid email");
            editTextSignupEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(masterPass)) {
            editTextSignupPassword.setError("Enter a password");
            editTextSignupPassword.requestFocus();
            return false;
        }
        if(!isValid(masterPass)){
            editTextSignupPassword.setError("Enter a valid master password");
            editTextSignupPassword.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(confirmMasterPass)){
            editTextConfirmPassword.setError("Confirm your password");
            editTextConfirmPassword.requestFocus();
            return false;
        }
        if(!confirmMasterPass.equals(masterPass)){
            editTextConfirmPassword.setError("Confirmation password is not the same");
            editTextConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean isValid(String password)
    {
        if (password == null || password.length() < 8)
        {
            return false;
        }
        boolean containUpper = false;
        boolean containDigit = false;
        int i = 0;
        while (i < password.length())
        {
            if (containDigit && containUpper)
            {
                break;
            }
            if (Character.isUpperCase(password.charAt(i)))
            {
                containUpper = true;
            }
            if (Character.isDigit(password.charAt(i)))
            {
                containDigit = true;
            }
            i++;
        }
        return containDigit & containUpper;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void register() throws GeneralSecurityException, IOException {

        String email = editTextSignupEmail.getText().toString().trim();
        String masterPass = editTextSignupPassword.getText().toString().trim();

        if(validInputs()){
            String secretKeyString = Crypter.getInstance(getApplicationContext()).generateKey(masterPass,email.getBytes());
            SharedPrefManager.getInstance(getApplicationContext()).saveUserKey(secretKeyString);

            String encryptedMasterPass = Crypter.getInstance(getApplicationContext()).encrypt(masterPass);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(String response) {
                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);

                                //if no error in response
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    finish();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));

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
                    params.put("email", email);
                    params.put("password", encryptedMasterPass);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }
}