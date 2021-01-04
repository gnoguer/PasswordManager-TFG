package com.example.passwordmanager.activites.paymentcards;

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
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.core.Note;
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

public class AddPaymentCardActivity extends AppCompatActivity {

    EditText paymentName;
    EditText nameOnCard;
    EditText number;
    EditText securityCode;
    EditText expirationDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_card);

        setTitle("Add payment card");

        Toolbar toolbar = findViewById(R.id.addPaymentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paymentName = findViewById(R.id.editTextPaymentName);
        nameOnCard = findViewById(R.id.editTextPaymentNameOnCard);
        number = findViewById(R.id.editTextPaymentNum);
        securityCode = findViewById(R.id.editTextPaymentSecCode);
        expirationDate = findViewById(R.id.editTextPaymentExpirationDate);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addItem) {
            try {
                savePaymentCard();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validInputs(){

        final String strPaymentName = paymentName.getText().toString();
        final String strNumber = number.getText().toString();
        final String strSecurityCode = securityCode.getText().toString();


        if (TextUtils.isEmpty(strPaymentName)) {
            paymentName.setError("Please enter your payment card name");
            paymentName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(strNumber)) {
            number.setError("Please enter your number");
            number.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(strSecurityCode)) {
            securityCode.setError("Please enter your security code");
            securityCode.requestFocus();
            return false;
        }
        return true;
    }

    public void savePaymentCard() throws GeneralSecurityException, IOException {

        if(validInputs()){

            String strNumber = number.getText().toString();
            String strSecurityCode = securityCode.getText().toString();

            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            String strEncryptedNumber = Crypter.getInstance(getApplicationContext()).encrypt(strNumber);
            String strEncryptedSecurityCode = Crypter.getInstance(getApplicationContext()).encrypt(strSecurityCode);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SAVE_PAYMENT_CARD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    int paymentCardCode = obj.getInt("code");
                                    PaymentCard newPaymentCard = new PaymentCard(paymentCardCode,
                                            paymentName.getText().toString(),
                                            nameOnCard.getText().toString(),
                                            number.getText().toString(),
                                            securityCode.getText().toString(),
                                            expirationDate.getText().toString());

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("newPaymentCard", newPaymentCard);
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
                    params.put("name", String.valueOf(paymentName.getText()));
                    params.put("nameOnCard", String.valueOf(nameOnCard.getText()));
                    params.put("number", String.valueOf(strEncryptedNumber));
                    params.put("securityCode", String.valueOf(strEncryptedSecurityCode));
                    params.put("expirationDate", String.valueOf(expirationDate.getText()));

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }

    }

}