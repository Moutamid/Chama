package com.moutamid.chama.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.chama.R;
import com.moutamid.chama.adapters.ViewPagerFragmentAdapter;
import com.moutamid.chama.databinding.FragmentSaleBinding;

public class SaleFragment extends Fragment {
    FragmentSaleBinding binding;
    public SaleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSaleBinding.inflate(getLayoutInflater(), container, false);


        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getChildFragmentManager());
        adapter.addFragment(new CashSaleFragment(), "Cash Sale");
        adapter.addFragment(new CreditSaleFragment(), "Credit Sale");
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        return binding.getRoot();
    }
}