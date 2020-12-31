package com.example.passwordmanager.activites.passwords;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import java.util.Calendar;
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
    Switch expirationSwitch;
    EditText expirationDays;
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

        password.addTextChangedListener(passwordEditorWatcher);

        expirationDays.setEnabled(false);

        generatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(AddPasswordActivity.this, PasswordGeneratorActivity.class);
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
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
                                            note.getText().toString());

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

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

            if(expirationSwitch.isChecked()){
                int days = Integer.parseInt(expirationDays.getText().toString());
                setExpirationAlarm(days, String.valueOf(serviceName.getText()));
            }

        }
    }

    private void setExpirationAlarm(int days, String serviceName){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("code", 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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