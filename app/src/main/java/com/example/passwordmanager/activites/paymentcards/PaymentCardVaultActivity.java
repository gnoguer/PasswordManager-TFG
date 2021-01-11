package com.example.passwordmanager.activites.paymentcards;

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
import com.example.passwordmanager.adapters.PaymentCardsVaultAdapter;
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

public class PaymentCardVaultActivity extends AppCompatActivity {

    private static final int START_ADD = 1;
    private static final int START_EDIT = 2;


    private final ArrayList<PaymentCard> paymentCards = new ArrayList<>();

    private RecyclerView recyclerView;
    private PaymentCardsVaultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_vault);

        setTitle("Payment Cards Vault");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.vaultFab);

        try {
            getItems();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(PaymentCardVaultActivity.this, AddPaymentCardActivity.class);
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
                PaymentCard newPaymentCard = (PaymentCard) data.getExtras().getSerializable("newPaymentCard");
                paymentCards.add(newPaymentCard);
                buildRecycleView();
            }
        }

        if (requestCode == START_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                PaymentCard newPaymentCard = (PaymentCard) data.getExtras().getSerializable("newPaymentCard");
                int position = data.getExtras().getInt("position");

                paymentCards.set(position, newPaymentCard);
                buildRecycleView();
            }
        }
    }

    public void getItems() throws GeneralSecurityException, IOException {

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_PAYMENT_CARDS + "&userId=" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                JSONArray array = obj.getJSONArray("paymentCards");
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
            String nameOnCard = json.getString("nameOnCard");
            String number = json.getString("number");
            String securityCode = json.getString("securityCode");
            String expirationDate = json.getString("expirationDate");

            String decryptedNumber = Crypter.getInstance(getApplicationContext()).decrypt(number);
            String decryptedSecurityCode = Crypter.getInstance(getApplicationContext()).decrypt(securityCode);
            paymentCards.add(new PaymentCard(code, name, nameOnCard, decryptedNumber, decryptedSecurityCode, expirationDate));
        }
    }

    public void buildRecycleView(){

        recyclerView = findViewById(R.id.vaultRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new PaymentCardsVaultAdapter(paymentCards);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PaymentCardsVaultAdapter.OnItemClickListener() {

            @Override
            public void onPreviewClick(int position) {
                openDialog(paymentCards.get(position));
            }

            @Override
            public void onOptionsClick(int position, View view) {

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.item_popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_popup_delete) {
                            int code = paymentCards.get(position).getCode();
                            deleteItem(code, position);
                        }
                        if(item.getItemId() == R.id.action_popup_edit){

                            Intent intent = new Intent(PaymentCardVaultActivity.this, AddPaymentCardActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("paymentCard", paymentCards.get(position));
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_DELETE_PAYMENT_CARD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                paymentCards.remove(position);
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

    public void openDialog(PaymentCard paymentCard){
        PaymentCardDialog dialog = new PaymentCardDialog(paymentCard);
        dialog.show(getSupportFragmentManager(), "dialog");

    }

}