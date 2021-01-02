package com.example.passwordmanager.activites.notes;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.passwordmanager.R;
import com.example.passwordmanager.core.Note;
import com.example.passwordmanager.core.Service;

import java.util.Objects;

public class NotesDialog extends AppCompatDialogFragment {

    private final Note note;

    public NotesDialog(Note note) {
        this.note = note;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.note_dialog, null);

        builder.setView(view)
                .setTitle(note.getName())
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        TextView noteTextView = view.findViewById(R.id.noteDialogTextView);

        noteTextView.setText(note.getNote());

        return builder.create();

    }
}
