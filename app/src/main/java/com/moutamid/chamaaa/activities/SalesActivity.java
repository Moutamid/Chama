package com.moutamid.chamaaa.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.adapters.ViewPagerFragmentAdapter;
import com.moutamid.chamaaa.databinding.ActivitySalesBinding;
import com.moutamid.chamaaa.fragments.CashSaleFragment;
import com.moutamid.chamaaa.fragments.CreditSaleFragment;
import com.moutamid.chamaaa.utilis.Constants;

public class SalesActivity extends AppCompatActivity {
    ActivitySalesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        binding.toolbar.name.setText("Sales");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new CashSaleFragment(), "Cash Sale");
        adapter.addFragment(new CreditSaleFragment(), "Credit Sale");
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }
}