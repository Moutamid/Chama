package com.moutamid.chamaaa.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.activities.MainActivity;
import com.moutamid.chamaaa.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    FragmentHomeBinding binding;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        binding.bottomNav.setItemActiveIndicatorColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        binding.bottomNav.setOnNavigationItemSelectedListener(this);
        binding.bottomNav.setSelectedItemId(R.id.dashboard);
        return binding.getRoot();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.dashboard) {
            MainActivity activity = (MainActivity) requireActivity();
            activity.disableMessageLayout();
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new DashboardFragment()).commit();
        } else if (id == R.id.chat) {
            MainActivity activity = (MainActivity) requireActivity();
            activity.enableMessageLayout();
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, new MessageFragment()).commit();
        }
        return true;
    }
}