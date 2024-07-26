package com.moutamid.chama.bottomsheets;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.activities.ProductsManageActivity;
import com.moutamid.chama.databinding.ChatMenuBinding;
import com.moutamid.chama.listener.ChatMenuListener;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

public class ChatMenu extends BottomSheetDialogFragment {
    ChatMenuBinding binding;
    ChatModel chatModel;
    ChatMenuListener chatMenuListener;

    public ChatMenu(ChatModel chatModel, ChatMenuListener chatMenuListener) {
        this.chatModel = chatModel;
        this.chatMenuListener = chatMenuListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ChatMenuBinding.inflate(getLayoutInflater(), container, false);
        boolean stock = false;
        boolean sale = false;
        boolean expenses = false;
        boolean products = false;
        if (chatModel.isGroup) {
            for (UserModel users : chatModel.groupMembers) {
                if (users.id.equals(Constants.auth().getCurrentUser().getUid())) {
                    stock = users.role.equals("Stock") || users.role.equals("Products Manager") || users.role.equals("OWNER");
                    sale = users.role.equals("Sale") || users.role.equals("Products Manager") || users.role.equals("OWNER");
                    products = users.role.equals("Products Manager") || users.role.equals("OWNER");
                    expenses = users.role.equals("Expenses") || users.role.equals("OWNER");
                    break;
                }
            }
        }
        int vis = products ? View.VISIBLE : View.GONE;
        binding.productsLayout.setVisibility(vis);

        if (!chatModel.isBusinessGroup) {
            binding.sales.setVisibility(View.GONE);
            binding.stocks.setVisibility(View.GONE);
            binding.expenses.setVisibility(View.GONE);
        }

        binding.sendMoney.setVisibility(View.VISIBLE);
        binding.withdrawFund.setVisibility(View.VISIBLE);

        if (!chatModel.isGroup) {
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
            chatMenuListener.imagePick();
        });

        binding.products.setOnClickListener(v -> {
            dismiss();
            Stash.put(Constants.PRODUCT_REFERENCE, chatModel);
            startActivity(new Intent(requireContext(), ProductsManageActivity.class));
        });

        boolean finalSale = sale;
        binding.sales.setOnClickListener(v -> {
            if (finalSale) {
                dismiss();
                chatMenuListener.sales();
            } else {
                Toast.makeText(requireContext(), "You don't have permission to use this", Toast.LENGTH_SHORT).show();
            }
        });

        boolean finalStock = stock;
        binding.stocks.setOnClickListener(v -> {
            if (finalStock) {
                dismiss();
                chatMenuListener.stocks();
            } else {
                Toast.makeText(requireContext(), "You don't have permission to use this", Toast.LENGTH_SHORT).show();
            }
        });

        boolean finalExpenses = expenses;
        binding.expenses.setOnClickListener(v -> {
            if (finalExpenses) {
                dismiss();
                chatMenuListener.expenses();
            } else {
                Toast.makeText(requireContext(), "You don't have permission to use this", Toast.LENGTH_SHORT).show();
            }
        });

        binding.run.setOnClickListener(v -> {
            dismiss();
            chatMenuListener.run();
        });

        binding.contribution.setOnClickListener(v -> {
            dismiss();
            chatMenuListener.contribution();
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
