package com.example.passwordmanager.activites.passwords;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.passwordmanager.adapters.PasswordsVaultAdapter;
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.R;
import com.example.passwordmanager.core.Service;
import com.example.passwordmanager.user.SharedPrefManager;
import com.example.passwordmanager.requests.URLs;
import com.example.passwordmanager.user.User;
import com.example.passwordmanager.requests.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PasswordsVaultActivity extends AppCompatActivity {

    private final ArrayList<Service> services = new ArrayList<>();

    private RecyclerView recyclerView;
    private PasswordsVaultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwords_vault);
        setTitle("Passwords Vault");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.passwordsFab);

        try {
            getItems();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(PasswordsVaultActivity.this, AddPasswordActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        SearchView searchView = findViewById(R.id.servicesSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Service newService = (Service) data.getExtras().getSerializable("newService");
                services.add(newService);
                buildRecycleView();
            }
        }
    }

    public void getItems() throws GeneralSecurityException, IOException {

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_SERVICES + "&userId=" + user.getId(),
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

                                JSONArray passwordsArray = obj.getJSONArray("passwords");
                                createPasswordsList(passwordsArray);
                                buildRecycleView();

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
                params.put("userId", String.valueOf(user.getId()));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createPasswordsList(JSONArray passwordsArray) throws JSONException, GeneralSecurityException, IOException {

        for (int i = 0; i < passwordsArray.length(); i++){

            JSONObject passwordJson = passwordsArray.getJSONObject(i);

            String name = passwordJson.getString("name");
            String username = passwordJson.getString("username");
            String password = passwordJson.getString("password");
            String note = passwordJson.getString("note");

            String decryptedPass = Crypter.getInstance(getApplicationContext()).decrypt(password);

            services.add(new Service(name, username, decryptedPass, note));
        }
    }

    public void buildRecycleView(){

        recyclerView = findViewById(R.id.passwordsRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new PasswordsVaultAdapter(services);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PasswordsVaultAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCopyClick(int position) throws GeneralSecurityException, IOException {
                String password = services.get(position).getPassword();
                copyPassword(password);
            }

            @Override
            public void onPreviewClick(int position) {
                openDialog(services.get(position));
            }
        });
    }

    public void openDialog(Service service){
        PasswordsDialog passwordsDialog = new PasswordsDialog(service);
        passwordsDialog.show(getSupportFragmentManager(), "Example dialog");

    }

    public void copyPassword(String password){
        ClipboardManager myClipboard;
        ClipData myClip;

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        myClip = ClipData.newPlainText("text", password);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Text Copied",Toast.LENGTH_SHORT).show();
    }
}