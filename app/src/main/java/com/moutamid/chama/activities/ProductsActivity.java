package com.moutamid.chama.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityProductsBinding;
import com.moutamid.chama.fragments.ProductsFragment;
import com.moutamid.chama.fragments.StockFragment;
import com.moutamid.chama.utilis.Constants;

public class ProductsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
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
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ProductsFragment()).commit();
        } else if (id == R.id.stock) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new StockFragment()).commit();
        }
        return true;
    }
}