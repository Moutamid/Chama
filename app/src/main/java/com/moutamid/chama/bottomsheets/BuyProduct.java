package com.moutamid.chama.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.databinding.BuyProductBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.ProductModel;

public class BuyProduct extends BottomSheetDialogFragment {
    BuyProductBinding binding;
    ProductModel model;
    ChatModel chatModel;
    int quantity = 1;
    public BuyProduct(ProductModel model, ChatModel chatModel) {
        this.model = model;
        this.chatModel = chatModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BuyProductBinding.inflate(getLayoutInflater(), container, false);

        binding.toolbar.name.setText("Sell Product");
        binding.toolbar.back.setOnClickListener(v -> dismiss());

        Glide.with(this).load(model.image).into(binding.productImage);
        binding.price.setText(String.format("$%s", model.unit_price));
        binding.stock.setText(String.valueOf( model.available_stock));
        binding.productName.setText(model.name);
        binding.productDescription.setText(model.desc);

        if (model.available_stock <= 0){
            binding.buy.setText("Not Enough Stock");
            binding.buy.setEnabled(false);
        }

        binding.minus.setEnabled(false);

        binding.add.setOnClickListener(v -> {
            quantity += 1;
            binding.quantity.setText(String.valueOf(quantity));
            binding.minus.setEnabled(true);
            if (quantity == model.available_stock) {
                binding.add.setEnabled(false);
            }
        });

        binding.minus.setOnClickListener(v -> {
            quantity -= 1;
            binding.quantity.setText(String.valueOf(quantity));
            binding.add.setEnabled(true);
            if (quantity == 1) {
                binding.minus.setEnabled(false);
            }
        });

        binding.buy.setOnClickListener(v -> {
            PaymentDialog paymentDialog = new PaymentDialog(model, quantity, chatModel);
            paymentDialog.show(getChildFragmentManager(), paymentDialog.getTag());
        });

        return binding.getRoot();
    }
}
