package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.chama.R;
import com.moutamid.chama.models.ProductModel;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsVH> {
    Context context;
    ArrayList<ProductModel> list;

    public ProductsAdapter(Context context, ArrayList<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProductsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductsVH(LayoutInflater.from(context).inflate(R.layout.product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsVH holder, int position) {
        ProductModel model = list.get(holder.getAbsoluteAdapterPosition());
        Glide.with(context).load(model.image).into(holder.productImage);
        holder.price.setText(String.format("$%s", model.unit_price));
        holder.stock.setText(String.valueOf( model.available_stock));
        holder.productName.setText(model.name);
        holder.productDescription.setText(model.desc);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ProductsVH extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productName;
        TextView productDescription;
        TextView stock;
        TextView price;
        public ProductsVH(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productDescription = itemView.findViewById(R.id.productDescription);
            productName = itemView.findViewById(R.id.productName);
            stock = itemView.findViewById(R.id.stock);
            price = itemView.findViewById(R.id.price);
        }
    }

}
