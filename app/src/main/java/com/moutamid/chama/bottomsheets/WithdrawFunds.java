package com.moutamid.chama.bottomsheets;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.databinding.WithdrawFundBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.zhouyou.view.seekbar.SignSeekBar;

public class WithdrawFunds extends BottomSheetDialogFragment {

    WithdrawFundBinding binding;
    ChatModel chatModel;
    MessageModel model;

    public WithdrawFunds(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

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

        binding.send.setOnClickListener(v -> {

        });

        binding.seekBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                binding.money.setText("USD " + progressFloat);
                binding.money2.setText("USD " + progressFloat);
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
