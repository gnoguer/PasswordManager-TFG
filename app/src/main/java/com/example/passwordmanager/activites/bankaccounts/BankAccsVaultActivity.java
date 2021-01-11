package com.example.passwordmanager.activites.bankaccounts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import com.example.passwordmanager.R;
import com.example.passwordmanager.activites.paymentcards.AddPaymentCardActivity;
import com.example.passwordmanager.activites.paymentcards.PaymentCardDialog;
import com.example.passwordmanager.activites.paymentcards.PaymentCardVaultActivity;
import com.example.passwordmanager.adapters.BankAccountVaultAdapter;
import com.example.passwordmanager.adapters.PaymentCardsVaultAdapter;
import com.example.passwordmanager.core.BankAccount;
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.core.PaymentCard;
import com.example.passwordmanager.requests.URLs;
import com.example.passwordmanager.requests.VolleySingleton;
import com.example.passwordmanager.user.SharedPrefManager;
import com.example.passwordmanager.user.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BankAccsVaultActivity extends AppCompatActivity {

    private static final int START_ADD = 1;
    private static final int START_EDIT = 2;


    private final ArrayList<BankAccount> bankAccs = new ArrayList<>();

    private RecyclerView recyclerView;
    private BankAccountVaultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_vault);

        setTitle("Bank Accounts Vault");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.vaultFab);

        try {
            getItems();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(BankAccsVaultActivity.this, AddBankAccActivity.class);
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

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                BankAccount newBankAccount = (BankAccount) data.getExtras().getSerializable("newBankAccount");
                bankAccs.add(newBankAccount);
                buildRecycleView();
            }
        }

        if (requestCode == START_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                BankAccount newBankAccount = (BankAccount) data.getExtras().getSerializable("newBankAccount");
                int position = data.getExtras().getInt("position");

                bankAccs.set(position, newBankAccount);
                buildRecycleView();
            }
        }
    }

    public void getItems() throws GeneralSecurityException, IOException {

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_BANK_ACCS + "&userId=" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                JSONArray array = obj.getJSONArray("bankAccounts");
                                createItemList(array);
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

    public void createItemList(JSONArray itemArray) throws JSONException, GeneralSecurityException, IOException {

        for (int i = 0; i < itemArray.length(); i++){

            JSONObject json = itemArray.getJSONObject(i);
            int code = json.getInt("code");
            String name = json.getString("name");
            String IBAN = json.getString("IBAN");
            String PIN = json.getString("PIN");

            String decryptedIBAN = Crypter.getInstance(getApplicationContext()).decrypt(IBAN);
            String decryptedPIN = Crypter.getInstance(getApplicationContext()).decrypt(PIN);
            bankAccs.add(new BankAccount(code, name, decryptedIBAN, decryptedPIN));
        }
    }

    public void buildRecycleView(){

        recyclerView = findViewById(R.id.vaultRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new BankAccountVaultAdapter(bankAccs);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BankAccountVaultAdapter.OnItemClickListener() {

            @Override
            public void onPreviewClick(int position) {
                openDialog(bankAccs.get(position));
            }

            @Override
            public void onOptionsClick(int position, View view) {

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.item_popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_popup_delete) {
                            int code = bankAccs.get(position).getCode();
                            deleteItem(code, position);
                        }
                        if(item.getItemId() == R.id.action_popup_edit){

                            Intent intent = new Intent(BankAccsVaultActivity.this, AddBankAccActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("bankAccount", bankAccs.get(position));
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_DELETE_BANK_ACC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                bankAccs.remove(position);
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

    public void openDialog(BankAccount bankAccount){
        BankAccDialog dialog = new BankAccDialog(bankAccount);
        dialog.show(getSupportFragmentManager(), "dialog");

    }

}