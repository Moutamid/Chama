package com.moutamid.chama.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.chama.adapters.MessageAdapter;
import com.moutamid.chama.adapters.ProductHomeAdapter;
import com.moutamid.chama.adapters.ProductsAdapter;
import com.moutamid.chama.databinding.FragmentProductBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ProductFragment extends Fragment {
    FragmentProductBinding binding;
    ArrayList<ChatModel> groups;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductBinding.inflate(getLayoutInflater(), container, false);

        Constants.showDialog();

        groups = new ArrayList<>();
        Constants.databaseReference().child(Constants.CHATS)
                .child(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ChatModel model = snapshot.getValue(ChatModel.class);
                            if (model.isBusinessGroup)
                                groups.add(model);
                        }
                    }

                    List<String> names = groups.stream()
                            .map(products -> products.name)
                            .collect(Collectors.toList());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, names);
                    binding.groupItem.setAdapter(adapter);
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.search.setOnClickListener(v -> {
            ChatModel chatModel = groups.stream()
                    .filter(productModel -> productModel.name.trim().equals(binding.group.getEditText().getText().toString().trim()))
                    .findFirst()
                    .orElse(null);
            if (chatModel != null) {
                findProduct(chatModel);
            } else {
                Toast.makeText(requireContext(), "No Product Found for this group", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void findProduct(ChatModel chatModel) {
        ArrayList<ProductModel> list = new ArrayList<>();
        Constants.databaseReference().child(Constants.PRODUCTS).child(chatModel.id).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);
                            list.add(productModel);
                        }
                    }
                    ProductHomeAdapter adapter = new ProductHomeAdapter(requireContext(), list, null);
                    binding.products.setAdapter(adapter);
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}