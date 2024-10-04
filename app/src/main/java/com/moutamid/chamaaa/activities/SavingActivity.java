package com.moutamid.chamaaa.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.bottomsheets.DepositFund;
import com.moutamid.chamaaa.databinding.ActivitySavingBinding;
import com.moutamid.chamaaa.models.SavingModel;
import com.moutamid.chamaaa.models.UserModel;
import com.moutamid.chamaaa.utilis.Constants;

public class SavingActivity extends AppCompatActivity {
    ActivitySavingBinding binding;
    UserModel userModel;
    SavingModel normal, locked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        Constants.showDialog();

        binding.toolbar.name.setText("Savings");
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        binding.username.setText(userModel.name);
        binding.email.setText(userModel.email);
        Glide.with(this).load(userModel.image).placeholder(R.drawable.profile_icon).into(binding.profile);

        binding.withdraw.setOnClickListener(v -> {
            DepositFund depositFund = new DepositFund(normal, locked, false);
            depositFund.setListener(() -> {
                fetchData();
            });
            depositFund.show(this.getSupportFragmentManager(), depositFund.getTag());
        });
        binding.deposit.setOnClickListener(v -> {
            DepositFund depositFund = new DepositFund(normal, locked, true);
            depositFund.setListener(() -> {
                fetchData();
            });
            depositFund.show(this.getSupportFragmentManager(), depositFund.getTag());
        });

        binding.edit.setOnClickListener(v -> startActivity(new Intent(this, ProfileEditActivity.class)));
        fetchData();
    }

    private void fetchData() {
        Constants.databaseReference().child(Constants.SAVING).child(Constants.auth().getCurrentUser().getUid()).child(Constants.NORMAL).
                get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        normal = dataSnapshot.getValue(SavingModel.class);
                        binding.savedAmount.setText("USD " + String.format("%.2f", normal.amount));
                    } else {
                        binding.savedAmount.setText("USD 0.00");
                    }
                });

        Constants.databaseReference().child(Constants.SAVING).child(Constants.auth().getCurrentUser().getUid()).child(Constants.LOCK).
                get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        locked = dataSnapshot.getValue(SavingModel.class);
                        binding.lockSaving.setText("USD " + String.format("%.2f", locked.amount));
                    } else {
                        binding.lockSaving.setText("USD 0.00");
                    }
                });
    }
}