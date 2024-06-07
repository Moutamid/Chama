package com.moutamid.chama.bottomsheets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.databinding.DepositFundsBinding;
import com.moutamid.chama.listener.BottomSheetDismissListener;
import com.moutamid.chama.models.SavingModel;
import com.moutamid.chama.utilis.Constants;

import java.util.UUID;

public class DepositFund extends BottomSheetDialogFragment {
    DepositFundsBinding binding;
    private BottomSheetDismissListener listener;

    public DepositFund() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DepositFundsBinding.inflate(getLayoutInflater(), container, false);

        binding.toolbar.name.setText("Deposit");
        binding.toolbar.back.setOnClickListener(v -> dismiss());

        binding.cancel.setOnClickListener(v -> dismiss());

        binding.normal.setChecked(true);

        binding.send.setOnClickListener(v -> {
            if (binding.amount.getEditText().getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter Amount", Toast.LENGTH_SHORT).show();
            } else {
                String type = binding.normal.isChecked() ? Constants.NORMAL : Constants.LOCK;
                SavingModel savingModel = new SavingModel();
                savingModel.id = UUID.randomUUID().toString();
                savingModel.amount = Double.parseDouble(binding.amount.getEditText().getText().toString());
                Constants.databaseReference().child(Constants.SAVING).child(Constants.auth().getCurrentUser().getUid()).child(type).setValue(savingModel).addOnSuccessListener(unused -> dismiss());
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onBottomSheetDismissed();
        }
    }

    public void setListener(BottomSheetDismissListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}
