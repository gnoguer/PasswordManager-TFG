package com.example.passwordmanager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passwordmanager.R;
import com.example.passwordmanager.core.Note;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class NotesVaultAdapter extends RecyclerView.Adapter<NotesVaultAdapter.NotesVaultViewHolder> implements Filterable {

    private final ArrayList<Note> notes;
    private final ArrayList<Note> notesFull;
    private PasswordsVaultAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCopyClick(int position) throws GeneralSecurityException, IOException;
        void onPreviewClick(int position);
        void onOptionsClick(int position, View view);
    }

    public void setOnItemClickListener(PasswordsVaultAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public static class NotesVaultViewHolder extends  RecyclerView.ViewHolder{

        public TextView noteNameTextView;
        public ImageButton viewNoteImageView;
        public ImageButton copyNoteImageView;
        public ImageButton noteOptionsImageView;


        public NotesVaultViewHolder(@NonNull View itemView, PasswordsVaultAdapter.OnItemClickListener listener) {
            super(itemView);
            noteNameTextView = itemView.findViewById(R.id.noteNameTextView);
            viewNoteImageView = itemView.findViewById(R.id.viewNoteImageView);
            copyNoteImageView = itemView.findViewById(R.id.copyNoteImageView);
            noteOptionsImageView = itemView.findViewById(R.id.noteOptionsImageView);

            viewNoteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onPreviewClick(position);
                        }
                    }
                }
            });

            copyNoteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            try {
                                listener.onCopyClick(position);
                            } catch (GeneralSecurityException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            noteOptionsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onOptionsClick(position, v);
                        }
                    }
                }
            });

        }
    }

    public NotesVaultAdapter(ArrayList<Note> notes){
        this.notes = notes;
        this.notesFull = new ArrayList<>(notes);
    }

    @NonNull
    @Override
    public NotesVaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_element, parent, false);
        return new NotesVaultViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesVaultViewHolder holder, int position) {
        Note currentNote = notes.get(position);

        holder.noteNameTextView.setText(currentNote.getName());

    }

    @Override
    public int getItemCount() {

        return notes.size();
    }

    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private Filter noteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Note> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(notesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Note note : notesFull){
                    if(note.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(note);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((List) results.values);

            notifyDataSetChanged();

        }
    };
}
