package com.example.passwordmanager.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passwordmanager.R;
import com.example.passwordmanager.core.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PasswordsVaultAdapter extends RecyclerView.Adapter<PasswordsVaultAdapter.PasswordsVaultViewHolder> implements Filterable {

    private final ArrayList<Service> services;
    private final ArrayList<Service> servicesFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCopyClick(int position) throws GeneralSecurityException, IOException;
        void onPreviewClick(int position);
        void onOptionsClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class PasswordsVaultViewHolder extends  RecyclerView.ViewHolder{

        public TextView serviceNameTextView;
        public ImageButton viewPasswordsImageView;
        public ImageButton copyPasswordImageView;
        public ImageButton serviceOptionsImageButton;
        public ImageView expiredImageView;


        public PasswordsVaultViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            viewPasswordsImageView = itemView.findViewById(R.id.viewPassImageView);
            copyPasswordImageView = itemView.findViewById(R.id.copyPassImageView3);
            serviceOptionsImageButton = itemView.findViewById(R.id.serviceOptionsImageButton);
            expiredImageView = itemView.findViewById(R.id.expiredImageView);

            viewPasswordsImageView.setOnClickListener(new View.OnClickListener() {
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

            copyPasswordImageView.setOnClickListener(new View.OnClickListener() {
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

            serviceOptionsImageButton.setOnClickListener(new View.OnClickListener() {
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



    public PasswordsVaultAdapter(ArrayList<Service> services){
        this.services = services;
        this.servicesFull = new ArrayList<>(services);
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
        if(currentService.isExpired()){
            holder.expiredImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return services.size();
    }

    @Override
    public Filter getFilter() {
        return serviceFilter;
    }

    private Filter serviceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Service> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(servicesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Service service : servicesFull){
                    if(service.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(service);
                    }
            }
        }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
    }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            services.clear();
            services.addAll((List) results.values);

            notifyDataSetChanged();

        }
    };
}
