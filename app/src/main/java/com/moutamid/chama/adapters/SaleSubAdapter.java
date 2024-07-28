package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.chama.R;
import com.moutamid.chama.models.SaleModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SaleSubAdapter extends RecyclerView.Adapter<SaleSubAdapter.SaleVH> {
    Context context;
    ArrayList<SaleModel> list;

    public SaleSubAdapter(Context context, ArrayList<SaleModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SaleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SaleVH(LayoutInflater.from(context).inflate(R.layout.table_text_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SaleVH holder, int position) {
        SaleModel model = list.get(holder.getAbsoluteAdapterPosition());
        holder.quantity.setText("x" + model.quantity);
        holder.price.setText("$" + String.format("%.2f", model.unit_price));
        holder.date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(model.date));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SaleVH extends RecyclerView.ViewHolder {
        TextView date, price,quantity;
        public SaleVH(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }

}
