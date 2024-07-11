package com.moutamid.chama.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.moutamid.chama.R;
import com.moutamid.chama.adapters.ViewPagerFragmentAdapter;
import com.moutamid.chama.databinding.ActivitySalesBinding;
import com.moutamid.chama.fragments.CashSaleFragment;
import com.moutamid.chama.fragments.CreditSaleFragment;

public class SalesActivity extends AppCompatActivity {
    ActivitySalesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Sales");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new CashSaleFragment(), "Cash Sale");
        adapter.addFragment(new CreditSaleFragment(), "Credit Sale");
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }
}