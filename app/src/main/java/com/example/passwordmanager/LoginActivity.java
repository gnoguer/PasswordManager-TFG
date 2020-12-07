package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    Button logInButton;
    EditText editTextEmail;
    EditText editTextMasterPassword;

    String email;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logInButton = findViewById(R.id.loginButton);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMasterPassword = findViewById(R.id.editTextMasterPassword);



       logInButton.setOnClickListener(new View.OnClickListener(){
           public void onClick(View view){
               email = String.valueOf(editTextMasterPassword.getText());
               password = String.valueOf(editTextMasterPassword.getText());
               login();

           }
       });
    }

    public void login(){

        //first getting the values
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

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("email")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                //starting the profile activity
                                finish();

                                startActivity(new Intent(getApplicationContext(), VaultActivity.class));

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
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void register(){

    }

}