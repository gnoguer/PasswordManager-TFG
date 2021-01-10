package com.example.passwordmanager.activites.notes;

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

public class AddNoteActivity extends AppCompatActivity {

    int ADD = 1;
    int EDIT = 2;

    private int requestCode;
    private int position;
    private Note editableNote;

    EditText noteName;
    EditText note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        setTitle("Add note");

        Toolbar toolbar = findViewById(R.id.addNoteToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteName = findViewById(R.id.editTextNoteName);
        note = findViewById(R.id.editTextNote);

        requestCode = getIntent().getExtras().getInt("requestCode");


        if(requestCode == EDIT){
            position = getIntent().getExtras().getInt("position");

            editableNote = (Note) getIntent().getExtras().get("note");

            noteName.setText(editableNote.getName());
            note.setText(editableNote.getNote());
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
            if (requestCode == ADD) {
                try {
                    saveNote();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == EDIT) {
                try {
                    editNote();
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void editNote() throws GeneralSecurityException, IOException {

        if(validInputs()){
            String strNote = note.getText().toString();

            String strEncryptedNote = Crypter.getInstance(getApplicationContext()).encrypt(strNote);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_NOTE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    int noteCode = obj.getInt("code");
                                    Note newNote = new Note(noteCode, noteName.getText().toString(),
                                            note.getText().toString());

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("newNote", newNote);
                                    intent.putExtras(bundle);
                                    bundle.putInt("position", position);

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
                    params.put("code", String.valueOf(editableNote.getCode()));
                    params.put("name", String.valueOf(noteName.getText()));
                    params.put("note", strEncryptedNote);

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }

    }
    public boolean validInputs(){

        final String strNoteName = noteName.getText().toString();
        final String strNote = note.getText().toString();

        if (TextUtils.isEmpty(strNoteName)) {
            noteName.setError("Please enter your note name");
            noteName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(strNote)) {
            note.setError("Please enter your note");
            note.requestFocus();
            return false;
        }
        return true;
    }


    public void saveNote() throws GeneralSecurityException, IOException {

        if(validInputs()){

            String strNote = note.getText().toString();
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            String strEncryptedNote = Crypter.getInstance(getApplicationContext()).encrypt(strNote);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SAVE_NOTE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    int noteCode = obj.getInt("code");
                                    Note newNote = new Note(noteCode, noteName.getText().toString(),
                                            note.getText().toString());

                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("newNote", newNote);
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
                    params.put("name", String.valueOf(noteName.getText()));
                    params.put("note", strEncryptedNote);

                    return params;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }

    }

}