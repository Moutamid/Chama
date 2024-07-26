package com.moutamid.chama.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chama.R;
import com.moutamid.chama.adapters.ProductBuyAdapter;
import com.moutamid.chama.adapters.ProductsAdapter;
import com.moutamid.chama.bottomsheets.BuyProduct;
import com.moutamid.chama.databinding.ActivityProductListBinding;
import com.moutamid.chama.fragments.ProductFragment;
import com.moutamid.chama.fragments.ProductsAddFragment;
import com.moutamid.chama.fragments.StockFragment;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.utilis.Constants;

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