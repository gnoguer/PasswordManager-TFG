package com.example.passwordmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PasswordsVaultAdapter extends RecyclerView.Adapter<PasswordsVaultAdapter.PasswordsVaultViewHolder> {

    private ArrayList<Service> services;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCopyClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class PasswordsVaultViewHolder extends  RecyclerView.ViewHolder{

        public TextView serviceNameTextView;
        public ImageButton viewPasswordsImageView;
        public ImageButton copyPasswordImageView;
        public ImageButton serviceOptionsImageButton;


        public PasswordsVaultViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            viewPasswordsImageView = itemView.findViewById(R.id.viewPassImageView);
            copyPasswordImageView = itemView.findViewById(R.id.copyPassImageView3);
            serviceOptionsImageButton = itemView.findViewById(R.id.serviceOptionsImageButton);

            copyPasswordImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                           listener.onCopyClick(position);
                        }
                    }
                }
            });

        }
    }

    public PasswordsVaultAdapter(ArrayList<Service> services){
        this.services = services;
    }

    @NonNull
    @Override
    public PasswordsVaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.passwords_list_element, parent, false);
        return new PasswordsVaultViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordsVaultViewHolder holder, int position) {
        Service currentService = services.get(position);

        holder.serviceNameTextView.setText(currentService.getName());
        holder.viewPasswordsImageView.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
        holder.copyPasswordImageView.setImageResource(R.drawable.ic_baseline_content_copy_24);
        holder.serviceOptionsImageButton.setImageResource(R.drawable.ic_baseline_more_vert_24);

    }

    @Override
    public int getItemCount() {

        return services.size();
    }



}
