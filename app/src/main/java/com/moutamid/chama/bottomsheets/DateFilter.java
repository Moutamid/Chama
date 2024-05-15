package com.moutamid.chama.bottomsheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.databinding.DateFilterBinding;

public class DateFilter extends BottomSheetDialogFragment {
    DateFilterBinding binding;
    String type;
    public DateFilter(String type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DateFilterBinding.inflate(getLayoutInflater(), container, false);



        return binding.getRoot();
    }
}