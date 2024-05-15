package com.moutamid.chama.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.databinding.WithdrawFundBinding;

public class WithdrawFunds extends BottomSheetDialogFragment {

    WithdrawFundBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WithdrawFundBinding.inflate(getLayoutInflater(), container, false);

        return binding.getRoot();
    }
}
