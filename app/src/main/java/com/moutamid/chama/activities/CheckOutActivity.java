package com.moutamid.chama.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityCheckOutBinding;
import com.moutamid.chama.models.OrderModel;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.models.StockModel;
import com.moutamid.chama.utilis.Constants;

import java.util.Date;
import java.util.UUID;

public class CheckOutActivity extends AppCompatActivity {
    ActivityCheckOutBinding binding;
    int QUANTITY;
    ProductModel productModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.name.setText("Summary");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        QUANTITY = getIntent().getIntExtra(Constants.QUANTITY, 0);

        productModel = (ProductModel) Stash.getObject(Constants.BUY_PRODUCT, ProductModel.class);

        binding.productName.setText(productModel.name);
        binding.price.setText(String.format("$%s", productModel.unit_price));
        binding.quanity.setText(String.format("x%s", QUANTITY));
        double total = QUANTITY * productModel.unit_price;
        binding.total.setText(String.format("$%s", total));
        
        binding.pay.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                buyProduct();
            }
        });

    }

    private boolean valid() {
        if (binding.fullName.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "Name is  empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.address.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "Address is  empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.country.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "Country is  empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.state.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "State is  empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.city.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "City is  empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.phone.getEditText().getText().toString().isEmpty()){
            Toast.makeText(this, "Phone Number is  empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void buyProduct() {
        OrderModel model = new OrderModel();
        productModel.available_stock = productModel.available_stock - QUANTITY;
        model.productModel = productModel;
        model.id = UUID.randomUUID().toString();
        model.fullName = binding.fullName.getEditText().getText().toString().trim();
        model.address = binding.address.getEditText().getText().toString().trim();
        model.country = binding.country.getEditText().getText().toString().trim();
        model.state = binding.state.getEditText().getText().toString().trim();
        model.city = binding.city.getEditText().getText().toString().trim();
        model.zip = binding.zipcode.getEditText().getText().toString().trim();
        model.phoneNumber = binding.phone.getEditText().getText().toString().trim();
        model.quantity = QUANTITY;
        Constants.databaseReference().child(Constants.ORDERS).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    updateProduct();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProduct() {
        Constants.databaseReference().child(Constants.PRODUCTS).child(productModel.id).setValue(productModel)
                .addOnSuccessListener(unused1 -> {
                    updateStock();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateStock() {
        StockModel model = new StockModel();
        model.id = UUID.randomUUID().toString();
        model.product_id = productModel.id;
        model.name = productModel.name.trim();
        model.measuring_unit = productModel.measuring_unit.trim();
        model.buying_price = productModel.buying_price;
        model.unit_price = productModel.unit_price;
        model.date = new Date().getTime();
        model.unit = productModel.unit;
        model.quantity = QUANTITY;

        Constants.databaseReference().child(Constants.STOCK_OUT).child(model.name).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    startActivity(new Intent(this, OrderCompleteActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}