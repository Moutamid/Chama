package com.moutamid.chamaaa.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.databinding.ActivityProductListBinding;
import com.moutamid.chamaaa.fragments.ProductFragment;
import com.moutamid.chamaaa.fragments.StockFragment;
import com.moutamid.chamaaa.models.ProductModel;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ActivityProductListBinding binding;
    ArrayList<ProductModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);

        binding.toolbar.name.setText("Products");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.products);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.products) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProductFragment()).commit();
        } else if (id == R.id.stock) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new StockFragment()).commit();
        }
        return true;
    }
}