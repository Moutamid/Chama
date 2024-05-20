package com.moutamid.chama.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.moutamid.chama.fragments.HomeFragment;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityMainBinding;
import com.moutamid.chama.utilis.Constants;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Constants.checkApp(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.navView.setNavigationItemSelectedListener(this);
        binding.drawLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        binding.navView.setCheckedItem(R.id.home);

        binding.search.setOnClickListener(v -> startActivity(new Intent(this, GroupSelectionActivity.class)));
        disableMessageLayout();
    }

    public void enableMessageLayout(){
        binding.toolbar.setTitle("Messages");
        binding.toolbar.setSubtitle("");
        binding.search.setVisibility(View.GONE);
        binding.messageLayout.setVisibility(View.VISIBLE);
    }

    public void disableMessageLayout(){
        binding.toolbar.setTitle("Hi Charlie");
        binding.toolbar.setSubtitle("Welcome back, lets explore what’s going on..");
        binding.search.setVisibility(View.VISIBLE);
        binding.messageLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.home){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.logout){
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        return false;
    }
}