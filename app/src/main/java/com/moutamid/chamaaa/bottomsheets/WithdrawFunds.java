package com.moutamid.chamaaa.bottomsheets;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.databinding.WithdrawFundBinding;
import com.moutamid.chamaaa.models.ChatModel;
import com.moutamid.chamaaa.models.MessageModel;
import com.moutamid.chamaaa.models.SavingModel;
import com.moutamid.chamaaa.models.TransactionModel;
import com.moutamid.chamaaa.models.UserModel;
import com.moutamid.chamaaa.utilis.Constants;
import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.Date;
import java.util.UUID;

public class WithdrawFunds extends BottomSheetDialogFragment {

    WithdrawFundBinding binding;
    ChatModel chatModel;
    MessageModel model;

    SavingModel mySavings;
    public WithdrawFunds(ChatModel chatModel, MessageModel model) {
        this.chatModel = chatModel;
        this.model = model;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WithdrawFundBinding.inflate(getLayoutInflater(), container, false);

        binding.toolbar.name.setText("Withdraw Money");
        binding.toolbar.back.setOnClickListener(v -> dismiss());

        binding.cancel.setOnClickListener(v -> dismiss());

        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);

        Constants.showDialog();

        Constants.databaseReference().child(Constants.SAVING).child(Constants.auth().getCurrentUser().getUid()).child(Constants.NORMAL)
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        mySavings = dataSnapshot.getValue(SavingModel.class);
                        binding.seekBar.getConfigBuilder().max((float) mySavings.amount).build();
                        if (model != null) {
                            binding.money.setText(model.money);
                            float money = Float.parseFloat(model.money.replace(" USD", ""));
                            binding.seekBar.setProgress(money);
                        } else {
                            binding.money.setText(String.valueOf(mySavings.amount));
                            binding.seekBar.setProgress((float) mySavings.amount);
                        }
                        binding.money2.setText("USD " + String.format("%.2f", mySavings.amount));
                    } else {
                        dismiss();
                        Toast.makeText(requireContext(), "Insufficient Balance Please recharge first", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    dismiss();
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });


        binding.person.setText(userModel.name);

        Glide.with(requireContext()).load(userModel.image).placeholder(R.drawable.profile_icon).into(binding.pofileIcon);

        binding.send.setOnClickListener(v -> {
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.id = UUID.randomUUID().toString();
            transactionModel.amount = binding.seekBar.getProgressFloat();
            transactionModel.type = Constants.WITHDRAW;
            transactionModel.timestamp = new Date().getTime();

            Constants.databaseReference().child(Constants.TRANSACTIONS).child(Constants.auth().getCurrentUser().getUid()).child(Constants.getCurrentMonth()).child(transactionModel.id).setValue(transactionModel);

            SavingModel savingModel = new SavingModel();
            savingModel.id = UUID.randomUUID().toString();
            savingModel.amount = mySavings.amount - binding.seekBar.getProgressFloat();
            Constants.databaseReference().child(Constants.SAVING).child(Constants.auth().getCurrentUser().getUid()).child(Constants.NORMAL).setValue(savingModel).addOnSuccessListener(unused -> dismiss());
        });

        binding.seekBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                binding.money.setText(progressFloat + " USD");
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}
