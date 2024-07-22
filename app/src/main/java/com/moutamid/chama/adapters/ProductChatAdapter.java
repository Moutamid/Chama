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
import com.moutamid.chama.activities.ProductListActivity;
import com.moutamid.chama.listener.Buy;
import com.moutamid.chama.models.ProductModel;

import java.util.ArrayList;

public class ProductChatAdapter extends RecyclerView.Adapter<ProductChatAdapter.ProductVH> {
    Context context;
    ArrayList<ProductModel> list;
    Buy buy;

    public ProductChatAdapter(Context context, ArrayList<ProductModel> list, Buy buy) {
        this.context = context;
        this.list = list;
        this.buy = buy;
    }

    @NonNull
    @Override
    public ProductVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductVH(LayoutInflater.from(context).inflate(R.layout.product_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductVH holder, int position) {
        ProductModel model = list.get(holder.getAbsoluteAdapterPosition());
        Glide.with(context).load(model.image).into(holder.productImage);
        holder.price.setText(String.format("$%s", model.unit_price));
        holder.productName.setText(model.name);

        holder.itemView.setOnClickListener(v -> {
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
        TextView price;
        public ProductVH(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.price);
        }
    }

}
