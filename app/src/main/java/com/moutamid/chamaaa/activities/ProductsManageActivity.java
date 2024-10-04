package com.moutamid.chamaaa.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.databinding.ActivityProductsBinding;
import com.moutamid.chamaaa.fragments.ProductsAddFragment;
import com.moutamid.chamaaa.fragments.StockFragment;
import com.moutamid.chamaaa.utilis.Constants;

public class ProductsManageActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ActivityProductsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Products");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.products);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.products) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProductsAddFragment()).commit();
        } else if (id == R.id.stock) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new StockFragment()).commit();
        }
        return true;
    }
}