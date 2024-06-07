package com.moutamid.chama.bottomsheets;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.moutamid.chama.R;
import com.moutamid.chama.adapters.PersonAdapter;
import com.moutamid.chama.databinding.SendMoneyBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.SavingModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;
import com.zhouyou.view.seekbar.SignSeekBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SendMoney extends BottomSheetDialogFragment {
    SendMoneyBinding binding;
    ChatModel chatModel;
    UserModel userModel;
    ArrayList<UserModel> list;
    UserModel selectedPerson;
    SavingModel mySavings, hisSaving;

    public SendMoney(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SendMoneyBinding.inflate(getLayoutInflater(), container, false);

        binding.toolbar.name.setText("Send Money");
        binding.toolbar.back.setOnClickListener(v -> dismiss());

        userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);

        list = new ArrayList<>();
        Constants.showDialog();

        Constants.databaseReference().child(Constants.SAVING).child(Constants.auth().getCurrentUser().getUid()).child(Constants.NORMAL)
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        mySavings = dataSnapshot.getValue(SavingModel.class);
                        binding.seekBar.getConfigBuilder().max((float) mySavings.amount).build();
                    } else {
                        dismiss();
                        Toast.makeText(requireContext(), "Insufficient Balance Please recharge first", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    dismiss();
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

        if (!chatModel.isGroup) {
            UserModel user = new UserModel();
            user.image = chatModel.image;
            user.name = chatModel.name;
            user.id = chatModel.userID;
            selectedPerson = user;
            binding.gender.getEditText().setText(user.name);
            list.add(user);

            Constants.databaseReference().child(Constants.SAVING).child(selectedPerson.id).child(Constants.NORMAL)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            hisSaving = dataSnapshot.getValue(SavingModel.class);
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else {
            for (UserModel user : chatModel.groupMembers) {
                if (!user.id.equals(userModel.id)) {
                    list.add(user);
                }
            }
        }

        PersonAdapter adapter = new PersonAdapter(requireContext(), R.layout.item_person, list);
        binding.genderList.setAdapter(adapter);

        binding.genderList.setDropDownBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        binding.genderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonAdapter adapter = (PersonAdapter) parent.getAdapter();
                selectedPerson = adapter.getItem(position);
                String name = selectedPerson.name;
                binding.gender.getEditText().setText(name);

                Constants.databaseReference().child(Constants.SAVING).child(selectedPerson.id).child(Constants.NORMAL)
                        .get().addOnSuccessListener(dataSnapshot -> {
                            if (dataSnapshot.exists()) {
                                hisSaving = dataSnapshot.getValue(SavingModel.class);
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        binding.seekBar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                binding.money.setText("USD " + progressFloat);
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        binding.cancel.setOnClickListener(v -> dismiss());

        binding.send.setOnClickListener(v -> {
            if (selectedPerson == null) {
                Toast.makeText(requireContext(), "Select the Person", Toast.LENGTH_SHORT).show();
            } else {
                Constants.showDialog();
                MessageModel model = new MessageModel();
                model.id = UUID.randomUUID().toString();
                model.senderID = Constants.auth().getCurrentUser().getUid();
                model.chatID = chatModel.id;
                model.isGroup = chatModel.isGroup;
                model.isMoneyShared = true;
                model.isImageShared = false;
                model.isPoll = false;
                model.money = binding.seekBar.getProgressFloat() + " USD";
                model.timestamp = new Date().getTime();
                model.image = userModel.image;
                model.message = userModel.name + " Contributed " + model.money;

                Constants.databaseReference().child(Constants.MESSAGES).child(chatModel.id).child(model.id)
                        .setValue(model).addOnSuccessListener(unused -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("timestamp", model.timestamp);
                            map.put("lastMessage", "Contributed");
                            map.put("isMoneyShared", true);
                            map.put("whoShared", "You shared - " + model.money);
                            Constants.databaseReference().child(Constants.CHATS).child(userModel.id).child(chatModel.id).updateChildren(map)
                                    .addOnSuccessListener(unused1 -> {
                                        if (chatModel.isGroup) {
                                            int i = 0;
                                            for (UserModel userModel : chatModel.groupMembers) {
                                                i++;
                                                int finalI = i;
                                                map.put("whoShared", this.userModel.name + " shared - " + model.money);
                                                Constants.databaseReference().child(Constants.CHATS).child(userModel.id).child(chatModel.id).updateChildren(map)
                                                        .addOnSuccessListener(unused2 -> {
                                                            if (finalI == chatModel.groupMembers.size() - 1) {
                                                                dismiss();
                                                            }
                                                        });
                                            }
                                        } else {
                                            map.put("whoShared", "You got - " + model.money);
                                            Constants.databaseReference().child(Constants.CHATS).child(chatModel.userID).child(chatModel.id).updateChildren(map)
                                                    .addOnSuccessListener(unused2 -> {
                                                        dismiss();
                                                    });
                                        }
                                    });
                        });
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
