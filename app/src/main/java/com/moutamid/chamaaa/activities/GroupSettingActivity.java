package com.moutamid.chamaaa.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.adapters.MembersAdapter;
import com.moutamid.chamaaa.databinding.ActivityGroupSettingBinding;
import com.moutamid.chamaaa.listener.GroupMembers;
import com.moutamid.chamaaa.models.Admins;
import com.moutamid.chamaaa.models.ChatModel;
import com.moutamid.chamaaa.models.UserModel;
import com.moutamid.chamaaa.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GroupSettingActivity extends AppCompatActivity {
    ActivityGroupSettingBinding binding;
    ChatModel chatModel;
    MembersAdapter adapter;
    private static final int PICK_FROM_GALLERY = 1001;
    Uri imageUri;

    boolean canEdit = false;

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        chatModel = (ChatModel) Stash.getObject(Constants.CHATS, ChatModel.class);

        Glide.with(this).load(chatModel.image).placeholder(R.drawable.profile_icon).into(binding.image);
        binding.name.setText(chatModel.name);
        binding.nameEdit.setText(chatModel.name);

        binding.size.setText(String.valueOf(chatModel.groupMembers.size()));

        adapter = new MembersAdapter(groupMembers, this, chatModel.groupMembers, chatModel.adminID);
        binding.usersRC.setAdapter(adapter);

        if (chatModel.adminID.equals(Constants.auth().getCurrentUser().getUid())) {
            canEdit = true;
            binding.addMore.setVisibility(View.VISIBLE);
        } else binding.addMore.setVisibility(View.GONE);

        for (UserModel users : chatModel.groupMembers) {
            if (users.id.equals(Constants.auth().getCurrentUser().getUid())) {
                if (users.role.equals("Edit Group")) {
                    canEdit = true;
                    break;
                }
            }
        }

        if (canEdit) {
            binding.save.setVisibility(View.VISIBLE);
            binding.name.setOnClickListener(v -> {
                binding.name.setVisibility(View.GONE);
                binding.nameEdit.setVisibility(View.VISIBLE);
                binding.nameEdit.requestFocus();
            });
            binding.save.setOnClickListener(v -> {
                Constants.showDialog();
                if (imageUri == null) {
                    uploadData(chatModel.image);
                } else {
                    uploadImage();
                }
            });
            binding.profileIcon.setOnClickListener(v -> {
                ImagePicker.with(this)
                        .cropSquare()
                        .galleryOnly()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start(PICK_FROM_GALLERY);
            });
        } else {
            binding.save.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> onBackPressed());

        binding.usersRC.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRC.setHasFixedSize(false);

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.addMore.setOnClickListener(v -> {
            startActivity(new Intent(this, AddMembersActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Stash.put(Constants.CHATS, chatModel);
    }

    private void uploadData(String image) {
        chatModel.image = image;
        chatModel.name = binding.nameEdit.getText().toString().trim();
        int i = 0;
        for (UserModel userModel : chatModel.groupMembers) {
            i++;
            int finalI = i;
            Constants.databaseReference().child(Constants.CHATS).child(userModel.id)
                    .child(chatModel.id).setValue(chatModel).addOnSuccessListener(unused1 -> {
                        if (finalI >= chatModel.groupMembers.size() - 1) {
                            onBackPressed();
                        }
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void uploadImage() {
        Constants.storageReference().child("image").child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date()))
                .putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> uploadData(uri.toString()));
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).placeholder(R.drawable.profile_icon).into(binding.image);
        }
    }

    GroupMembers groupMembers = new GroupMembers() {
        @Override
        public void onRemove(UserModel userModel, int pos) {
            chatModel.groupMembers.remove(pos);
            adapter.notifyItemRemoved(pos);
            Constants.databaseReference().child(Constants.CHATS).child(userModel.id)
                    .child(chatModel.id).removeValue();
            for (UserModel users : chatModel.groupMembers) {
                Constants.databaseReference().child(Constants.CHATS).child(users.id)
                        .child(chatModel.id).child("groupMembers").setValue(chatModel.groupMembers);
            }
        }

        @Override
        public void assignRule(UserModel userModel, int pos) {
            showDialog(userModel, pos);
        }
    };

    private void showDialog(UserModel userModel, int pos) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.assign_role);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetAnim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCancelable(true);
        dialog.show();

        Button assign = dialog.findViewById(R.id.assign);

        RadioGroup rulesGroup = dialog.findViewById(R.id.rulesGroup);

        assign.setOnClickListener(v -> {
            int checkedId = rulesGroup.getCheckedRadioButtonId();
            String selected;
            if (checkedId == R.id.sale) {
                selected = "Sale";
            } else if (checkedId == R.id.stock) {
                selected = "Stock";
            } else if (checkedId == R.id.expenses) {
                selected = "Expenses";
            } else if (checkedId == R.id.manageProducts) {
                selected = "Products Manager";
            } else selected = "UNKNOWN_ROLE";
            Log.d(TAG, "showDialog: " + selected);
            ArrayList<UserModel> users = new ArrayList<>();
            UserModel selectedUser = null;
            for (UserModel user : chatModel.groupMembers) {
                if (user.id.equals(userModel.id)) {
                    user.role = selected;
                    selectedUser = user;
                }
                users.add(user);
            }
            chatModel.groupMembers = new ArrayList<>(users);
//            if (chatModel.isGroup && chatModel.isSoccoGroup) {
//                Constants.databaseReference().child(Constants.SOCO).child(chatModel.id).setValue(chatModel);
//            }
            for (UserModel user : chatModel.groupMembers) {
                Constants.databaseReference().child(Constants.CHATS).child(user.id)
                        .child(chatModel.id).child("groupMembers").setValue(chatModel.groupMembers);
            }
            adapter.notifyItemChanged(pos);
            UserModel finalSelectedUser = selectedUser;
            Constants.databaseReference().child(Constants.ADMINS).get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    String key = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Admins admin = snapshot.getValue(Admins.class);
                        if (finalSelectedUser != null) {
                            if (finalSelectedUser.id.equals(admin.id)) {
                                key = snapshot.getKey();
                                break;
                            }
                        }
                    }
                    if (key != null) {
                        Constants.databaseReference().child(Constants.ADMINS).child(key).removeValue();
                    }
                }
            });
            dialog.dismiss();
        });
        Log.d(TAG, "showDialog: ");
    }


    private static final String TAG = "GroupSettingActivity";
}