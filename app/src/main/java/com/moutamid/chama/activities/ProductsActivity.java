package com.moutamid.chama.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityProductsBinding;
import com.moutamid.chama.fragments.DashboardFragment;
import com.moutamid.chama.fragments.MessageFragment;
import com.moutamid.chama.fragments.SaleFragment;
import com.moutamid.chama.fragments.StockFragment;

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
        binding.bottomNav.setSelectedItemId(R.id.stock);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sales) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SaleFragment()).commit();
        } else if (id == R.id.stock) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new StockFragment()).commit();
        }
        return true;
    }
}