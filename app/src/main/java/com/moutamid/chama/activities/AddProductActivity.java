package com.moutamid.chama.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.chip.Chip;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityAddProductBinding;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    ActivityAddProductBinding binding;
    Uri imageUri;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.name.setText("Add Product");
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
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        Constants.storageReference().child("product_images").child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date())).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadData(uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData(String image) {
        ProductModel product = new ProductModel();
        product.id = UUID.randomUUID().toString();
        product.name = binding.productName.getEditText().getText().toString();
        product.desc = binding.desc.getEditText().getText().toString();
        product.date = new Date().getTime();
        product.available_stock = 0.0;
        product.unit = 0.0;
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
                    Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show();
                    getOnBackPressedDispatcher().onBackPressed();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        if (imageUri == null) {
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            return false;
        }
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

        ArrayList<ProductModel> list = Stash.getArrayList(Constants.PRODUCTS, ProductModel.class);
        if (!list.isEmpty()) {
            for (ProductModel model : list) {
                if (model.name.trim().equals(binding.productName.getEditText().getText().toString().trim())) {
                    Toast.makeText(this, "Product with same name already exist", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
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