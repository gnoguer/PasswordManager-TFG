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
import com.example.passwordmanager.core.PaymentCard;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PaymentCardsVaultAdapter extends RecyclerView.Adapter<PaymentCardsVaultAdapter.PaymentCardsVaultViewHolder> implements Filterable {

    private final ArrayList<PaymentCard> paymentCards;
    private final ArrayList<PaymentCard> paymentCardsFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onPreviewClick(int position);
        void onOptionsClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class PaymentCardsVaultViewHolder extends  RecyclerView.ViewHolder{

        public TextView nameTextView;
        public ImageButton viewImageView;
        public ImageButton copyImageView;
        public ImageButton optionsImageView;


        public PaymentCardsVaultViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.paymentCardNameTextView);
            viewImageView = itemView.findViewById(R.id.viewPaymentCardImageView);
            optionsImageView = itemView.findViewById(R.id.paymentCardOptionsImageView);

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

    public PaymentCardsVaultAdapter(ArrayList<PaymentCard> paymentCards){
        this.paymentCards = paymentCards;
        this.paymentCardsFull = new ArrayList<>(paymentCards);
    }


    @NonNull
    @Override
    public PaymentCardsVaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_cards_list_element, parent, false);
        return new PaymentCardsVaultViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentCardsVaultViewHolder holder, int position) {
        PaymentCard currentPaymentCard = paymentCards.get(position);

        holder.nameTextView.setText(currentPaymentCard.getName());
    }

    @Override
    public int getItemCount() {

        return paymentCards.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PaymentCard> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(paymentCardsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(PaymentCard paymentCard : paymentCardsFull){
                    if(paymentCard.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(paymentCard);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            paymentCards.clear();
            paymentCards.addAll((List) results.values);

            notifyDataSetChanged();

        }
    };

}
