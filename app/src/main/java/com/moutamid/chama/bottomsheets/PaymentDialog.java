package com.moutamid.chama.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.databinding.PaymentDialogBinding;
import com.moutamid.chama.models.ProductModel;

public class PaymentDialog extends BottomSheetDialogFragment {
    PaymentDialogBinding binding;
    ProductModel productModel;
    int quantity;

    public PaymentDialog() {
    }

    public PaymentDialog(ProductModel productModel, int quantity) {
        this.productModel = productModel;
        this.quantity = quantity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PaymentDialogBinding.inflate(getLayoutInflater(), container, false);

        binding.productName.setText(productModel.name);
        binding.price.setText(String.format("$%s", productModel.unit_price));
        binding.quanity.setText(String.format("x%s", quantity));
        double total = quantity * productModel.unit_price;
        binding.total.setText(String.format("$%.2f", total));

        binding.credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int vis = isChecked ? View.VISIBLE : View.GONE;
                binding.personName.setVisibility(vis);
            }
        });

        binding.sell.setOnClickListener(v -> {
            if (valid()) {

            }
        });

        return binding.getRoot();
    }

    private boolean valid() {
        if (binding.credit.isChecked()) {
            if (binding.personName.getEditText().getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Person Name is required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
