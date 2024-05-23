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
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.CreatePollBinding;

public class CreatePoll extends BottomSheetDialogFragment {
    CreatePollBinding binding;
    int pos = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreatePollBinding.inflate(getLayoutInflater(), container, false);

        binding.toolbar.name.setText("Create A Poll");
        binding.toolbar.back.setOnClickListener(v -> dismiss());

        binding.addMore.setOnClickListener(v -> {
            addOptions(pos + 1);
        });

        for (int i = 0; i < 3; i++) {
            addOptions(i);
        }

        return binding.getRoot();
    }

    char currentLetter = 'A';

    private void addOptions(int i) {
        pos = i;
        if (pos >= 25) {
            binding.addMore.setTextColor(getResources().getColor(R.color.text3));
            binding.addMore.setEnabled(false);
        }
        if (pos > 0) {
            currentLetter = getNextLetter(currentLetter);
        }
        String hint = "Option " + currentLetter;
        LayoutInflater inflater = getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.options, null);
        TextInputLayout option = customLayout.findViewById(R.id.option);
        option.setHint(hint);
        binding.options.addView(customLayout);
    }

    public char getNextLetter(char startLetter) {
        char currentLetter = startLetter;
        currentLetter = (char) (currentLetter + (currentLetter >= 'Z' ? -25 : 1));
        return currentLetter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}
