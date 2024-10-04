package com.moutamid.chamaaa.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.adapters.ViewPagerFragmentAdapter;
import com.moutamid.chamaaa.databinding.FragmentStockBinding;


public class StockFragment extends Fragment {
    FragmentStockBinding binding;
    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStockBinding.inflate(getLayoutInflater(), container, false);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new StockInFragment(), "Stock In");
        adapter.addFragment(new StockOutFragment(), "Stock Out");
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        return binding.getRoot();
    }
}