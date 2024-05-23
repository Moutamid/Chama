package com.moutamid.chama.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivitySettingBinding;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    public static final String TERMS = "https://google.com";
    public static final String POLICY = "https://google.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Settings");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.logout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Constants.auth().signOut();
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        binding.terms.setOnClickListener(v -> openLink(TERMS));
        binding.policy.setOnClickListener(v -> openLink(POLICY));
        binding.update.setOnClickListener(v -> startActivity(new Intent(this, ProfileEditActivity.class)));

    }

    private void openLink(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        binding.username.setText(userModel.name);
        binding.email.setText(userModel.email);
        Glide.with(this).load(userModel.image).placeholder(R.drawable.profile_icon).into(binding.profile);
    }
}