package com.moutamid.chama.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.chip.Chip;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityProductEditBinding;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProductEditActivity extends AppCompatActivity {
    ActivityProductEditBinding binding;
    Uri imageUri;
    ProductModel product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        product = (ProductModel) Stash.getObject(Constants.EDIT_PRODUCT, ProductModel.class);

        updateUI();

        binding.toolbar.name.setText("Edit Product");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.selectImage.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare().compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });

        binding.add.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                if (imageUri != null) {
                    uploadImage();
                } else {
                    updateData(product.image);
                }
            }
        });

    }

    private void updateUI() {
        binding.productName.getEditText().setText(product.name);
        binding.desc.getEditText().setText(product.desc);
        binding.unitPrice.getEditText().setText(String.valueOf(product.unit_price));
        binding.buyingPrice.getEditText().setText(String.valueOf(product.buying_price));
        Glide.with(this).load(product.image).placeholder(R.drawable.upload).into(binding.productImage);

        if (product.measuring_unit.equals("Kg")){
            binding.kilogram.setChecked(true);
        } else if (product.measuring_unit.equals("L")){
            binding.liter.setChecked(true);
        } else {
            binding.pounds.setChecked(true);
        }
        
        for (int i = 0; i < binding.measuringUnits.getChildCount(); i++) {
            Chip chip = (Chip) binding.measuringUnits.getChildAt(i);
            if (chip.getText().toString().equals(product.measuring_unit)) {
               chip.setChecked(true);
            }
        }
    }

    private void updateData(String image) {
        product.name = binding.productName.getEditText().getText().toString();
        product.desc = binding.desc.getEditText().getText().toString();
        product.unit_price = Double.parseDouble(binding.unitPrice.getEditText().getText().toString());
        product.buying_price = Double.parseDouble(binding.buyingPrice.getEditText().getText().toString());
        product.image = image;
        String measuring_unit = "";
        for (int i = 0; i < binding.measuringUnits.getChildCount(); i++) {
            Chip chip = (Chip) binding.measuringUnits.getChildAt(i);
            if (chip.isChecked()) {
                measuring_unit = chip.getText().toString();
            }
        }
        product.measuring_unit = measuring_unit;

        Constants.databaseReference().child(Constants.PRODUCTS).child(product.id).setValue(product)
                .addOnSuccessListener(unused -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
                    getOnBackPressedDispatcher().onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage() {
        Constants.storageReference().child("product_images").child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date())).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        updateData(uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        if (binding.productName.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Product Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.desc.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Product Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.buyingPrice.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Product Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.unitPrice.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Product Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        String measuring_unit = "";
        for (int i = 0; i < binding.measuringUnits.getChildCount(); i++) {
            Chip chip = (Chip) binding.measuringUnits.getChildAt(i);
            if (chip.isChecked()) {
                measuring_unit = chip.getText().toString();
            }
        }
        if (measuring_unit.isEmpty()) {
            Toast.makeText(this, "Please Select Unit Type", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).placeholder(R.drawable.upload).into(binding.productImage);
        }
    }

}