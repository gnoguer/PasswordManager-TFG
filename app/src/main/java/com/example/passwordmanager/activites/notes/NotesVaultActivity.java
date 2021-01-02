package com.example.passwordmanager.activites.notes;

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
import com.example.passwordmanager.R;
import com.example.passwordmanager.activites.passwords.AddPasswordActivity;
import com.example.passwordmanager.activites.passwords.PasswordsDialog;
import com.example.passwordmanager.activites.passwords.PasswordsVaultActivity;
import com.example.passwordmanager.adapters.NotesVaultAdapter;
import com.example.passwordmanager.adapters.PasswordsVaultAdapter;
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.core.Note;
import com.example.passwordmanager.core.Service;
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

public class NotesVaultActivity extends AppCompatActivity {

    private final ArrayList<Note> notes = new ArrayList<>();

    private RecyclerView recyclerView;
    private NotesVaultAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_vault);
        setTitle("Notes Vault");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.notesFab);

        try {
            getItems();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(NotesVaultActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, 1);
            }


        });

        SearchView searchView = findViewById(R.id.notesSearchView);
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
                Note newNote = (Note) data.getExtras().getSerializable("newNote");
                notes.add(newNote);
                buildRecycleView();
            }
        }
    }

    public void getItems() throws GeneralSecurityException, IOException {

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_NOTES + "&userId=" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                JSONArray notesArray = obj.getJSONArray("notes");
                                createItemList(notesArray);
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

            JSONObject passwordJson = itemArray.getJSONObject(i);
            int code = passwordJson.getInt("code");
            String name = passwordJson.getString("name");
            String note = passwordJson.getString("note");

            String decryptedNote = Crypter.getInstance(getApplicationContext()).decrypt(note);

            notes.add(new Note(code, name, decryptedNote));
        }
    }

    public void buildRecycleView(){

        recyclerView = findViewById(R.id.notesRecyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new NotesVaultAdapter(notes);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PasswordsVaultAdapter.OnItemClickListener() {
            @Override
            public void onCopyClick(int position) throws GeneralSecurityException, IOException {
                String note = notes.get(position).getNote();
                clipboardCopy(note);
            }

            @Override
            public void onPreviewClick(int position) {
                openDialog(notes.get(position));
            }

            @Override
            public void onOptionsClick(int position, View view) {

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.password_popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_popup_delete) {
                            int code = notes.get(position).getCode();
                            deleteItem(code, position);
                        }
                        return  true;
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void deleteItem(int code, int position){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_DELETE_NOTE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                notes.remove(position);
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

    public void openDialog(Note note){
        NotesDialog notesDialog = new NotesDialog(note);
        notesDialog.show(getSupportFragmentManager(), "Example dialog");

    }

    public void clipboardCopy(String string){
        ClipboardManager myClipboard;
        ClipData myClip;

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        myClip = ClipData.newPlainText("text", string);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Text Copied",Toast.LENGTH_SHORT).show();
    }

}