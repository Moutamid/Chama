package com.moutamid.chama.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.chama.R;
import com.moutamid.chama.activities.ProductEditActivity;
import com.moutamid.chama.fragments.ProductsFragment;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsVH> implements Filterable {
    Context context;
    ArrayList<ProductModel> list;
    ArrayList<ProductModel> listAll;
    ProductsFragment.Refresh refresh;
    public ProductsAdapter(Context context, ArrayList<ProductModel> list, ProductsFragment.Refresh refresh) {
        this.context = context;
        this.list = list;
        this.refresh = refresh;
        this.listAll = new ArrayList<>(list);
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

        holder.edit.setOnClickListener(v -> {
            Stash.put(Constants.EDIT_PRODUCT, model);
            context.startActivity(new Intent(context, ProductEditActivity.class));
        });

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(Constants.PRODUCTS).child(model.id).removeValue()
                                .addOnSuccessListener(unused -> refresh.refresh())
                                .addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ProductModel> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filterList.addAll(listAll);
            } else {
                for (ProductModel listModel : listAll){
                    if (listModel.name.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends ProductModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ProductsVH extends RecyclerView.ViewHolder{
        ImageView productImage;
        TextView productName;
        TextView productDescription;
        TextView stock;
        TextView price;
        MaterialButton edit, delete;
        public ProductsVH(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productDescription = itemView.findViewById(R.id.productDescription);
            productName = itemView.findViewById(R.id.productName);
            stock = itemView.findViewById(R.id.stock);
            price = itemView.findViewById(R.id.price);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }

}
