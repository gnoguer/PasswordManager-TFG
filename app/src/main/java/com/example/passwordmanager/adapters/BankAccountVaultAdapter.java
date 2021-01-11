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
import com.example.passwordmanager.core.BankAccount;

import java.util.ArrayList;
import java.util.List;

public class BankAccountVaultAdapter extends RecyclerView.Adapter<BankAccountVaultAdapter.BankAccountVaultViewHolder> implements Filterable {

    private final ArrayList<BankAccount> bankAccounts;
    private final ArrayList<BankAccount> bankAccountsFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onPreviewClick(int position);
        void onOptionsClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class BankAccountVaultViewHolder extends  RecyclerView.ViewHolder{

        public TextView nameTextView;
        public ImageButton viewImageView;
        public ImageButton optionsImageView;


        public BankAccountVaultViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.bankAccNameTextView);
            viewImageView = itemView.findViewById(R.id.viewBankAccImageView);
            optionsImageView = itemView.findViewById(R.id.bankAccOptionsImageView);

            viewImageView.setOnClickListener(new View.OnClickListener() {
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

            optionsImageView.setOnClickListener(new View.OnClickListener() {
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

    public BankAccountVaultAdapter(ArrayList<BankAccount> bankAccounts){
        this.bankAccounts = bankAccounts;
        this.bankAccountsFull = new ArrayList<>(bankAccounts);
    }


    @NonNull
    @Override
    public BankAccountVaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_account_list_element, parent, false);
        return new BankAccountVaultViewHolder(view, listener);
    }


    @Override
    public void onBindViewHolder(@NonNull BankAccountVaultViewHolder holder, int position) {
        BankAccount currentBankAccount = bankAccounts.get(position);

        holder.nameTextView.setText(currentBankAccount.getName());
    }

    @Override
    public int getItemCount() {

        return bankAccounts.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BankAccount> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(bankAccountsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(BankAccount bankAccount : bankAccountsFull){
                    if(bankAccount.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(bankAccount);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bankAccounts.clear();
            bankAccounts.addAll((List) results.values);

            notifyDataSetChanged();

        }
    };

}
