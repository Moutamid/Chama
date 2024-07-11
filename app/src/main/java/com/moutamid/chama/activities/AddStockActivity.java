package com.moutamid.chama.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.chama.databinding.ActivityAddStockBinding;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.models.StockModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddStockActivity extends AppCompatActivity {
    ActivityAddStockBinding binding;
    ArrayList<ProductModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.name.setText("Add Stock");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        list = Stash.getArrayList(Constants.PRODUCTS, ProductModel.class);

        List<String> names = list.stream()
                .map(products -> products.name)
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        binding.productsList.setAdapter(adapter);

        binding.add.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                updateProduct();
            }
        });
    }

    private void updateProduct() {
        String searchText = binding.product.getEditText().getText().toString().trim();
        ProductModel model = list.stream()
                .filter(productModel -> productModel.name.trim().equals(searchText))
                .findFirst()
                .orElse(null);
        if (model != null) {
            model.available_stock += Double.parseDouble(binding.stock.getEditText().getText().toString());
            model.unit += Double.parseDouble(binding.unit.getEditText().getText().toString());
            Constants.databaseReference().child(Constants.PRODUCTS).child(model.id).setValue(model)
                    .addOnSuccessListener(unused -> {
                        updateStock(model);
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateStock(ProductModel product) {
        StockModel model = new StockModel();
        model.id = UUID.randomUUID().toString();
        model.product_id = product.id;
        model.name = product.name.trim();
        model.measuring_unit = product.measuring_unit.trim();
        model.buying_price = product.buying_price;
        model.unit_price = product.unit_price;
        model.date = new Date().getTime();
        model.unit = Double.parseDouble(binding.unit.getEditText().getText().toString());
        model.quantity = Double.parseDouble(binding.stock.getEditText().getText().toString());

        Constants.databaseReference().child(Constants.STOCK).child(model.name).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Stock Added", Toast.LENGTH_SHORT).show();
                    getOnBackPressedDispatcher().onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        String searchText = binding.product.getEditText().getText().toString().trim();
        if (searchText.isEmpty()) {
            Toast.makeText(this, "Product is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.stock.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Stock is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.unit.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Unit is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        ProductModel model = list.stream()
                .filter(productModel -> productModel.name.trim().equals(searchText))
                .findFirst()
                .orElse(null);

        if (model == null) {
            Toast.makeText(this, "Product not fount", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}