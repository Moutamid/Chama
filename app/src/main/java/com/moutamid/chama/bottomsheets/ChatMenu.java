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
import com.moutamid.chama.databinding.ChatMenuBinding;
import com.moutamid.chama.listener.ImageSelectionListener;
import com.moutamid.chama.models.ChatModel;

public class ChatMenu extends BottomSheetDialogFragment {
    ChatMenuBinding binding;
    ChatModel chatModel;
    ImageSelectionListener imageSelectionListener;

    public ChatMenu(ChatModel chatModel, ImageSelectionListener imageSelectionListener) {
        this.chatModel = chatModel;
        this.imageSelectionListener = imageSelectionListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ChatMenuBinding.inflate(getLayoutInflater(), container, false);

        if (!chatModel.isGroup){
            binding.createPol.setVisibility(View.GONE);
        }

        binding.createPol.setOnClickListener(v -> {
            dismiss();
            CreatePoll createPoll = new CreatePoll(chatModel);
            createPoll.show(getParentFragmentManager(), createPoll.getTag());
        });

        binding.sendMoney.setOnClickListener(v -> {
            dismiss();
            SendMoney sendMoney = new SendMoney(chatModel);
            sendMoney.show(getParentFragmentManager(), sendMoney.getTag());
        });

        binding.withdrawFund.setOnClickListener(v -> {
            dismiss();
            WithdrawFunds withdrawFunds = new WithdrawFunds(chatModel, null);
            withdrawFunds.show(getParentFragmentManager(), withdrawFunds.getTag());
        });

        binding.sendPicture.setOnClickListener(v -> {
            dismiss();
            imageSelectionListener.imagePick();
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
