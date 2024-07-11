package com.moutamid.chama.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chama.R;
import com.moutamid.chama.activities.AddProductActivity;
import com.moutamid.chama.activities.AddStockActivity;
import com.moutamid.chama.adapters.ProductsAdapter;
import com.moutamid.chama.databinding.FragmentProductsBinding;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;

public class ProductsFragment extends Fragment {
    FragmentProductsBinding binding;
    ArrayList<ProductModel> list;
    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsBinding.inflate(getLayoutInflater(), container, false);

        binding.products.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.products.setHasFixedSize(false);

        binding.newProduct.setOnClickListener(v -> {
            binding.menu.collapseImmediately();
            startActivity(new Intent(requireContext(), AddProductActivity.class));
        });

        binding.addStock.setOnClickListener(v -> {
            binding.menu.collapseImmediately();
            startActivity(new Intent(requireContext(), AddStockActivity.class));
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        Constants.databaseReference().child(Constants.PRODUCTS).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);
                            list.add(productModel);
                        }
                    }
                    if (list.isEmpty()) {
                        binding.addStock.setEnabled(false);
                        binding.notLayout.setVisibility(View.VISIBLE);
                        binding.products.setVisibility(View.GONE);
                    }else {
                        binding.addStock.setEnabled(true);
                        binding.notLayout.setVisibility(View.GONE);
                        binding.products.setVisibility(View.VISIBLE);
                    }
                    Stash.put(Constants.PRODUCTS, list);
                    ProductsAdapter adapter = new ProductsAdapter(requireContext(), list);
                    binding.products.setAdapter(adapter);
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });;
    }
}