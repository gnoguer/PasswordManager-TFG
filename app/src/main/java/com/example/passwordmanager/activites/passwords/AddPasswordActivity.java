package com.example.passwordmanager.activites.passwords;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
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
import com.example.passwordmanager.core.Service;
import com.example.passwordmanager.user.SharedPrefManager;
import com.example.passwordmanager.requests.URLs;
import com.example.passwordmanager.user.User;
import com.example.passwordmanager.requests.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPasswordActivity extends AppCompatActivity {

    int ADD = 1;
    int EDIT = 2;
    int START_GENERATOR = 3;

    private int requestCode;
    private int position;
    private Service service;

    EditText serviceName;
    EditText username;
    EditText password;
    Button generatePassBtn;
    EditText note;
    TextView passwordStrength;
    Switch expirationSwitch;
    EditText expirationDays;
    TextView daysTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        setTitle("Add password");

        Toolbar toolbar = findViewById(R.id.addToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serviceName = findViewById(R.id.editTextServiceName);
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        generatePassBtn = findViewById(R.id.buttonGeneratePassword);
        note = findViewById(R.id.editTextPassNote);
        passwordStrength = findViewById(R.id.passwordStrengthTextView);
        expirationSwitch = findViewById(R.id.expirationSwitch);
        expirationDays = findViewById(R.id.setExpirationEditTextNumber);
        daysTextView = findViewById(R.id.daysTextView);

        password.addTextChangedListener(passwordEditorWatcher);

        expirationDays.setEnabled(false);

        requestCode = getIntent().getExtras().getInt("requestCode");


        generatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(AddPasswordActivity.this, PasswordGeneratorActivity.class);

                startActivityForResult(intent, START_GENERATOR);
            }
        });

        expirationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    expirationDays.setEnabled(true);
                }else{
                    expirationDays.setEnabled(false);
                }
            }

        });

        if(requestCode == EDIT){
            position = getIntent().getExtras().getInt("position");
            service = (Service) getIntent().getExtras().get("service");

            serviceName.setText(service.getName());
            username.setText(service.getUsername());
            password.setText(service.getPassword());
            note.setText(service.getNote());
            expirationSwitch.setVisibility(View.GONE);
            expirationDays.setVisibility(View.GONE);
            daysTextView.setVisibility(View.GONE);
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
        if (item.getItemId() == R.id.save) {
            if(requestCode == ADD){
                try {
                    savePassword();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
            if(requestCode == EDIT){
                try {
                    editPassword();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void editPassword() throws GeneralSecurityException, IOException {

        if(validInputs()){

            String strPass = password.getText().toString();
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            String strEncryptedPass = Crypter.getInstance(getApplicationContext()).encrypt(strPass);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_SERVICE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //converting response to json object
                                JSONObject obj = new JSONObject(response);
                                //if no error in response
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    int serviceCode = obj.getInt("code");
                                    Service newService = new Service(serviceCode, serviceName.getText().toString(),
                                            username.getText().toString(),
                                            password.getText().toString(),
                                            note.getText().toString());

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("newService", newService);
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
                    params.put("code", String.valueOf(service.getCode()));
                    params.put("name", String.valueOf(serviceName.getText()));
                    params.put("username", String.valueOf(username.getText()));
                    params.put("password", strEncryptedPass);
                    params.put("note", String.valueOf(note.getText()));

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
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

        if (TextUtils.isEmpty(strPassword)) {
            password.setError("Please enter your password");
            password.requestFocus();
            return false;
        }

        return true;
    }

    public void savePassword() throws GeneralSecurityException, IOException {
        if(validInputs()){

            String strPass = password.getText().toString();
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            String strEncryptedPass = Crypter.getInstance(getApplicationContext()).encrypt(strPass);
            String strDate = "";

            if(expirationSwitch.isChecked()){

                int days = Integer.parseInt(expirationDays.getText().toString());
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, days);

                Date date = calendar.getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strDate = dateFormat.format(date);
            }

            String finalStrDate = strDate;

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

                                    int serviceCode = obj.getInt("code");
                                    Service newService = new Service(serviceCode, serviceName.getText().toString(),
                                            username.getText().toString(),
                                            password.getText().toString(),
                                            note.getText().toString(),
                                            finalStrDate);

                                    if(expirationSwitch.isChecked()){
                                        int days = Integer.parseInt(expirationDays.getText().toString());
                                        setExpirationAlarm(days, newService);
                                    }

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("newService", newService);
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
                    params.put("name", String.valueOf(serviceName.getText()));
                    params.put("username", String.valueOf(username.getText()));
                    params.put("password", strEncryptedPass);
                    params.put("note", String.valueOf(note.getText()));
                    params.put("expirationDate", finalStrDate);

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        }
    }

    private void setExpirationAlarm(int days, Service service){

        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 1);


        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("serviceName", service.getName());
        intent.putExtra("code", 1);
        intent.putExtra("notificationId", service.getCode());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), service.getCode(), intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_GENERATOR) {
            if(resultCode == Activity.RESULT_OK){
                String newGeneratedPass = data.getStringExtra("result");
                password.setText(newGeneratedPass);
            }
        }
    }

    private final TextWatcher passwordEditorWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {

            if (calculateStrength(s.toString()) <= 1){
                passwordStrength.setText("EASY");
                passwordStrength.setTextColor(Color.parseColor("#61ad85"));
            }
            else if (calculateStrength(s.toString()) == 2){
                passwordStrength.setText("MEDIUM");
                passwordStrength.setTextColor(Color.parseColor("#4d8a6a"));
            }
            else if(calculateStrength(s.toString()) == 3){
                passwordStrength.setText("STRONG");
                passwordStrength.setTextColor(Color.parseColor("#3a674f"));
            }
            else if(calculateStrength(s.toString()) == 4){
                passwordStrength.setText("VERY STRONG");
                passwordStrength.setTextColor(Color.parseColor("#264535"));
            }

        }
    };

    public int calculateStrength(String password){
        int score = 0;
        boolean upper = false;
        boolean lower = false;
        boolean digit = false;
        boolean specialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (!specialChar  &&  !Character.isLetterOrDigit(c)) {
                score++;
                specialChar = true;
            } else {
                if (!digit  &&  Character.isDigit(c)) {
                    score++;
                    digit = true;
                } else {
                    if (!upper || !lower) {
                        if (Character.isUpperCase(c)) {
                            upper = true;
                        } else {
                            lower = true;
                        }

                        if (upper && lower) {
                            score++;
                        }
                    }
                }
            }
        }
        int length = password.length();

        if (length >= 8) {
            score++;
        }

        return score;
    }

}