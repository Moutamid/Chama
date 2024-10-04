package com.moutamid.chamaaa.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.databinding.ActivityManageUsersBinding;

public class ManageUsersActivity extends AppCompatActivity {
    ActivityManageUsersBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Manage Users");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

    }
}