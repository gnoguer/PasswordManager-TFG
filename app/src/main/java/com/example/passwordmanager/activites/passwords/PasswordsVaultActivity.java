package com.example.passwordmanager.activites.passwords;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
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

    private static final int START_ADD = 1;
    private static final int START_EDIT = 2;

    private final ArrayList<Service> services = new ArrayList<>();

    private RecyclerView recyclerView;
    private PasswordsVaultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_vault);
        setTitle("Passwords Vault");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.vaultFab);

        try {
            getItems();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(PasswordsVaultActivity.this, AddPasswordActivity.class);
                intent.putExtra("requestCode", START_ADD);

                startActivityForResult(intent, START_ADD);
            }
        });


        SearchView searchView = findViewById(R.id.vaultSearchView);
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

        if (requestCode == START_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Service newService = (Service) data.getExtras().getSerializable("newService");
                services.add(newService);
                buildRecycleView();
            }
        }
        if (requestCode == START_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Service newService = (Service) data.getExtras().getSerializable("newService");
                int position = data.getExtras().getInt("position");

                services.set(position, newService);
                buildRecycleView();

            }
        }
    }

    public void getItems() throws GeneralSecurityException, IOException {

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_SERVICES + "&userId=" + user.getId(),
                new Response.Listener<String>() {
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

    public void createPasswordsList(JSONArray passwordsArray) throws JSONException, GeneralSecurityException, IOException {

        for (int i = 0; i < passwordsArray.length(); i++){

            JSONObject passwordJson = passwordsArray.getJSONObject(i);
            int code = passwordJson.getInt("code");
            String name = passwordJson.getString("name");
            String username = passwordJson.getString("username");
            String password = passwordJson.getString("password");
            String note = passwordJson.getString("note");

            String decryptedPass = Crypter.getInstance(getApplicationContext()).decrypt(password);

            services.add(new Service(code, name, username, decryptedPass, note));
        }
    }

    public void buildRecycleView(){

        recyclerView = findViewById(R.id.vaultRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new PasswordsVaultAdapter(services);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PasswordsVaultAdapter.OnItemClickListener() {
            @Override
            public void onCopyClick(int position) throws GeneralSecurityException, IOException {
                String password = services.get(position).getPassword();
                copyPassword(password);
            }

            @Override
            public void onPreviewClick(int position) {
                openDialog(services.get(position));
            }

            @Override
            public void onOptionsClick(int position, View view) {

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.item_popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_popup_delete) {
                            int code = services.get(position).getCode();
                            deleteItem(code, position);
                        }
                        if(item.getItemId() == R.id.action_popup_edit){

                            Intent intent = new Intent(PasswordsVaultActivity.this, AddPasswordActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("service", services.get(position));
                            bundle.putInt("requestCode", START_EDIT);
                            bundle.putInt("position", position);
                            intent.putExtras(bundle);

                            startActivityForResult(intent, START_EDIT);
                        }
                        return  true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void deleteItem(int code, int position){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_DELETE_SERVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                services.remove(position);
                                adapter.notifyItemRemoved(position);
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
                params.put("code", String.valueOf(code));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
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

        Toast.makeText(getApplicationContext(), "Password Copied",Toast.LENGTH_SHORT).show();
    }
}
