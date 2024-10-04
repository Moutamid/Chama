package com.moutamid.chamaaa.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chamaaa.adapters.ProductChatAdapter;
import com.moutamid.chamaaa.databinding.ShowProductsBinding;
import com.moutamid.chamaaa.models.ChatModel;
import com.moutamid.chamaaa.models.ProductModel;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;

public class ShowProducts extends BottomSheetDialogFragment {
    ShowProductsBinding binding;
    ArrayList<ProductModel> list;
    ChatModel chatModel;
    public ShowProducts(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShowProductsBinding.inflate(getLayoutInflater(), container, false);

        binding.products.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.products.setHasFixedSize(false);

        Constants.showDialog();
        list = new ArrayList<>();
        Constants.databaseReference().child(Constants.PRODUCTS).child(chatModel.id).get()
                .addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);
                            list.add(productModel);
                        }
                    }
                    if (list.isEmpty()) {
                        binding.notLayout.setVisibility(View.VISIBLE);
                        binding.products.setVisibility(View.GONE);
                    }else {
                        binding.notLayout.setVisibility(View.GONE);
                        binding.products.setVisibility(View.VISIBLE);
                    }
                    ProductChatAdapter adapter = new ProductChatAdapter(requireContext(), list, model -> {
                        BuyProduct buy = new BuyProduct(model, chatModel);
                        buy.setListener(this::dismiss);
                        buy.show(getChildFragmentManager(), buy.getTag());
                    });
                    binding.products.setAdapter(adapter);
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        return binding.getRoot();
    }
}
