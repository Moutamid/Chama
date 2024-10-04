package com.moutamid.chamaaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.models.ExpenseModel;

import java.util.ArrayList;

public class HistorySpent extends RecyclerView.Adapter<HistorySpent.HistoryVH> {
    Context context;
    ArrayList<ExpenseModel> list;

    public HistorySpent(Context context, ArrayList<ExpenseModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HistoryVH(LayoutInflater.from(context).inflate(R.layout.history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryVH holder, int position) {
        ExpenseModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.name);
        if (model.isExpense){
            holder.isIncome.setText("Spent");
            holder.price.setText("$" + String.format("%,.2f", model.price));
            holder.icon.setImageResource(R.drawable.round_remove_24);
            holder.price.setTextColor(context.getResources().getColor(R.color.red));
            holder.main.setCardBackgroundColor(context.getResources().getColor(R.color.red_light));
        } else {
            holder.isIncome.setText("Income");
            holder.price.setText("$" + String.format("%,.2f", model.price));
            holder.icon.setImageResource(R.drawable.add);
            holder.price.setTextColor(context.getResources().getColor(R.color.bar_withdrawal));
            holder.main.setCardBackgroundColor(context.getResources().getColor(R.color.green_trans));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class HistoryVH extends RecyclerView.ViewHolder{
        TextView isIncome, name, price;
        ImageView icon;
        MaterialCardView main;
        public HistoryVH(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main);
            isIncome = itemView.findViewById(R.id.isIncome);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            icon = itemView.findViewById(R.id.icon);
        }
    }

}