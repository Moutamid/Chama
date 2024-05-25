package com.moutamid.chama.bottomsheets;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.CreatePollBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.PollModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.models.Votes;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePoll extends BottomSheetDialogFragment {
    CreatePollBinding binding;
    int pos = 0;
    ChatModel chatModel;
    ArrayList<String> options;

    public CreatePoll(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreatePollBinding.inflate(getLayoutInflater(), container, false);

        binding.toolbar.name.setText("Create A Poll");
        binding.toolbar.back.setOnClickListener(v -> dismiss());

        options = new ArrayList<>();

        binding.addMore.setOnClickListener(v -> {
            addOptions(pos + 1);
        });

        for (int i = 0; i < 3; i++) {
            addOptions(i);
        }

        binding.send.setOnClickListener(v -> {
            if (valid()) {
                retrieveDataForOptions();
                UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
                MessageModel model = new MessageModel();
                model.id = UUID.randomUUID().toString();
                model.senderID = Constants.auth().getCurrentUser().getUid();
                model.chatID = chatModel.id;
                model.isGroup = chatModel.isGroup;
                model.isMoneyShared = false;
                model.isImageShared = false;
                model.isPoll = true;
                model.money = "";
                model.timestamp = new Date().getTime();
                model.image = stashUser.image;
                model.message = "";

                PollModel pollModel = new PollModel();
                pollModel.question = binding.question.getEditText().getText().toString();
                pollModel.options = new ArrayList<>(options);
                pollModel.senderId = Constants.auth().getCurrentUser().getUid();
                Votes votes = new Votes();
                votes.votes = new HashMap<>();
                for (String option : options) {
                    votes.votes.put(option.replace(" ", "_"), 0);
                }
                pollModel.votes = votes;
                model.pollModel = pollModel;
                String m = stashUser.name + ": Created a Poll";

                Constants.databaseReference().child(Constants.MESSAGES).child(chatModel.id).child(model.id)
                        .setValue(model).addOnSuccessListener(unused -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("timestamp", model.timestamp);
                            map.put("lastMessage", m);
                            Constants.databaseReference().child(Constants.CHATS).child(stashUser.id).child(chatModel.id).updateChildren(map)
                                    .addOnSuccessListener(unused1 -> {
                                        if (chatModel.isGroup) {
                                            int i = 0;
                                            for (UserModel userModel : chatModel.groupMembers) {
                                                i++;
                                                int finalI = i;
                                                Constants.databaseReference().child(Constants.CHATS).child(userModel.id).child(chatModel.id).updateChildren(map)
                                                        .addOnSuccessListener(unused2 -> {
                                                            if (finalI == chatModel.groupMembers.size() - 1) {
                                                                dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        });
            }
        });

        return binding.getRoot();
    }

    private boolean valid() {
        retrieveDataForOptions();
        if (options.size() < 2) {
            Toast.makeText(requireContext(), "Minimum 2 options are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.question.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Question is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    char currentLetter = 'A';

    private void retrieveDataForOptions() {
        options.clear();
        for (int i = 0; i < binding.options.getChildCount(); i++) {
            View view = binding.options.getChildAt(i);
            if (view instanceof RelativeLayout) {
                RelativeLayout textInputLayout = (RelativeLayout) view;
                TextInputLayout customEditText = textInputLayout.findViewById(R.id.option);

                String enteredText = customEditText.getEditText().getText().toString();
                if (!enteredText.isEmpty())
                    options.add(enteredText.trim());
            }
        }
    }

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
