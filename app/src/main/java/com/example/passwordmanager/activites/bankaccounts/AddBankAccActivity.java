package com.example.passwordmanager.activites.bankaccounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.passwordmanager.R;
import com.example.passwordmanager.core.BankAccount;
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.core.PaymentCard;
import com.example.passwordmanager.requests.URLs;
import com.example.passwordmanager.requests.VolleySingleton;
import com.example.passwordmanager.user.SharedPrefManager;
import com.example.passwordmanager.user.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class AddBankAccActivity extends AppCompatActivity {

    int ADD = 1;
    int EDIT = 2;

    private int requestCode;
    private int position;
    private BankAccount bankAccount;

    EditText name;
    EditText PIN;
    EditText IBAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_acc);

        setTitle("Add payment card");

        Toolbar toolbar = findViewById(R.id.addBankAccToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.editTextBankAccName);
        IBAN = findViewById(R.id.editTextIBAN);
        PIN = findViewById(R.id.editTextPIN);

        requestCode = getIntent().getExtras().getInt("requestCode");

        if(requestCode == EDIT){

            position = getIntent().getExtras().getInt("position");
            bankAccount = (BankAccount) getIntent().getExtras().get("bankAccount");

            name.setText(bankAccount.getName());
            IBAN.setText(bankAccount.getIBAN());
            PIN.setText(bankAccount.getIBAN());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save){
            if (requestCode == ADD) {
                try {
                    saveBankAccount();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == EDIT) {
                try {
                    editBankAccount();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean validInputs(){

        final String strName = name.getText().toString();
        final String strIBAN = IBAN.getText().toString();
        final String strPIN = PIN.getText().toString();


        if (TextUtils.isEmpty(strName)) {
            name.setError("Please enter your payment card name");
            name.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(strIBAN)) {
            IBAN.setError("Please enter your number");
            IBAN.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(strPIN)) {
            PIN.setError("Please enter your security code");
            PIN.requestFocus();
            return false;
        }
        return true;
    }

    public void editBankAccount() throws GeneralSecurityException, IOException {
        if(validInputs()){

            String strIBAN = IBAN.getText().toString();
            String strPIN = PIN.getText().toString();

            String strEncryptedIBAN = Crypter.getInstance(getApplicationContext()).encrypt(strIBAN);
            String strEncryptedPIN = Crypter.getInstance(getApplicationContext()).encrypt(strPIN);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_BANK_ACC,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    int paymentCardCode = obj.getInt("code");
                                    BankAccount newBankAccount = new BankAccount(paymentCardCode,
                                            name.getText().toString(),
                                            IBAN.getText().toString(),
                                            PIN.getText().toString());

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("newBankAccount", newBankAccount);
                                    bundle.putInt("position", position);
                                    intent.putExtras(bundle);

                                    setResult(Activity.RESULT_OK, intent);
                                    finish();

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
                    params.put("code", String.valueOf(bankAccount.getCode()));
                    params.put("name", String.valueOf(name.getText()));
                    params.put("IBAN", String.valueOf(strEncryptedIBAN));
                    params.put("PIN", String.valueOf(strEncryptedPIN));


                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    public void saveBankAccount() throws GeneralSecurityException, IOException {

        if(validInputs()){
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            String strIBAN = IBAN.getText().toString();
            String strPIN = PIN.getText().toString();

            String strEncryptedIBAN = Crypter.getInstance(getApplicationContext()).encrypt(strIBAN);
            String strEncryptedPIN = Crypter.getInstance(getApplicationContext()).encrypt(strPIN);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SAVE_BANK_ACC,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    int paymentCardCode = obj.getInt("code");
                                    BankAccount newBankAccount = new BankAccount(paymentCardCode,
                                            name.getText().toString(),
                                            IBAN.getText().toString(),
                                            PIN.getText().toString());

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("newBankAccount", newBankAccount);
                                    intent.putExtras(bundle);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();

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
                    params.put("name", String.valueOf(name.getText()));
                    params.put("IBAN", String.valueOf(strEncryptedIBAN));
                    params.put("PIN", String.valueOf(strEncryptedPIN));

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }

    }
}