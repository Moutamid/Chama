package com.moutamid.chamaaa.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chamaaa.activities.AddProductActivity;
import com.moutamid.chamaaa.activities.AddStockActivity;
import com.moutamid.chamaaa.adapters.ProductsAdapter;
import com.moutamid.chamaaa.databinding.FragmentProductsAddBinding;
import com.moutamid.chamaaa.models.ChatModel;
import com.moutamid.chamaaa.models.ProductModel;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;

public class ProductsAddFragment extends Fragment {
    FragmentProductsAddBinding binding;
    ArrayList<ProductModel> list;
    ChatModel chatModel;
    public ProductsAddFragment() {
        // Required empty public constructor
    }
    ProductsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsAddBinding.inflate(getLayoutInflater(), container, false);

        chatModel = (ChatModel) Stash.getObject(Constants.PRODUCT_REFERENCE, ChatModel.class);

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

        binding.search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    public interface Refresh {
        void refresh();
    }

    Refresh refresh = this::refreshList;

    private void refreshList() {
        list = new ArrayList<>();
        Constants.databaseReference().child(Constants.PRODUCTS).child(chatModel.id).get()
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
                    adapter = new ProductsAdapter(requireContext(), list, refresh);
                    binding.products.setAdapter(adapter);
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}