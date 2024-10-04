package com.moutamid.chamaaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.listener.Buy;
import com.moutamid.chamaaa.models.ProductModel;

import java.util.ArrayList;

public class ProductBuyAdapter extends RecyclerView.Adapter<ProductBuyAdapter.ProductVH> {
    Context context;
    ArrayList<ProductModel> list;
    Buy buy;

    public ProductBuyAdapter(Context context, ArrayList<ProductModel> list, Buy buy) {
        this.context = context;
        this.list = list;
        this.buy = buy;
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductVH(LayoutInflater.from(context).inflate(R.layout.products_buy, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        ProductModel model = list.get(holder.getAbsoluteAdapterPosition());
        Glide.with(context).load(model.image).into(holder.productImage);
        holder.price.setText(String.format("$%s", model.unit_price));
        holder.stock.setText(String.valueOf( model.available_stock));
        holder.productName.setText(model.name);
        holder.productDescription.setText(model.desc);

        holder.buy.setOnClickListener(v -> {
            buy.onBuy(model);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ProductVH extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productDescription;
        TextView stock;
        TextView price;
        MaterialButton buy;
        public ProductVH(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productDescription = itemView.findViewById(R.id.productDescription);
            productName = itemView.findViewById(R.id.productName);
            stock = itemView.findViewById(R.id.stock);
            price = itemView.findViewById(R.id.price);
            buy = itemView.findViewById(R.id.buy);
        }
    }

}
