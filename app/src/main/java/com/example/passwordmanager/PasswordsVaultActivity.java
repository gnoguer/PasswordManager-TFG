package com.example.passwordmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordsVaultActivity extends AppCompatActivity {

    private ArrayList<Service> services = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords_vault);
        setTitle("Passwords Vault");

        FloatingActionButton fab = findViewById(R.id.passwordsFab);
        Toolbar toolbar = findViewById(R.id.passVaultToolbar);

        setSupportActionBar(toolbar);



        try {
            getItems();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Intent intent = new Intent(PasswordsVaultActivity.this, AddPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getItems() throws GeneralSecurityException, IOException {

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_PASSWORDS + "&userId=" + user.getId(),
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

                                //getting the user from the response

                                JSONArray passwordsArray = obj.getJSONArray("passwords");

                                for (int i = 0; i < passwordsArray.length(); i++){

                                    JSONObject passwordJson = passwordsArray.getJSONObject(i);

                                    String name = passwordJson.getString("name");
                                    String username = passwordJson.getString("username");
                                    String password = passwordJson.getString("password");
                                    String note = passwordJson.getString("note");

                                    services.add(new Service(name, username, password, note));
                                }

                                //starting the activity
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
                params.put("userId", String.valueOf(user.getId()));
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}