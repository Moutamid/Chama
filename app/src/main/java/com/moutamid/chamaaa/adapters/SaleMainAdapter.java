package com.moutamid.chamaaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.models.Sell;

import java.util.ArrayList;

public class SaleMainAdapter extends RecyclerView.Adapter<SaleMainAdapter.SaleVH> {
    Context context;
    ArrayList<Sell> list;

    public SaleMainAdapter(Context context, ArrayList<Sell> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SaleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SaleVH(LayoutInflater.from(context).inflate(R.layout.sale_main, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SaleVH holder, int position) {
        Sell model = list.get(holder.getAbsoluteAdapterPosition());
        holder.productName.setText(model.name);
        holder.subList.setLayoutManager(new LinearLayoutManager(context));
        holder.subList.setHasFixedSize(false);
        SaleSubAdapter adapter = new SaleSubAdapter(context, model.list);
        holder.subList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SaleVH extends RecyclerView.ViewHolder {
        TextView productName;
        RecyclerView subList;
        public SaleVH(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            subList = itemView.findViewById(R.id.subList);
        }
    }

}
