package com.moutamid.chama.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.bottomsheets.WithdrawFunds;
import com.moutamid.chama.databinding.ActivitySavingBinding;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

public class SavingActivity extends AppCompatActivity {
    ActivitySavingBinding binding;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Savings");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        binding.username.setText(userModel.name);
        binding.email.setText(userModel.email);
        Glide.with(this).load(userModel.image).placeholder(R.drawable.profile_icon).into(binding.profile);

        binding.withdraw.setOnClickListener(v -> {
            WithdrawFunds withdrawFunds = new WithdrawFunds("30.00");
            withdrawFunds.show(this.getSupportFragmentManager(), withdrawFunds.getTag());
        });

    }
}