package com.moutamid.chama.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.moutamid.chama.R;
import com.moutamid.chama.adapters.MembersAdapter;
import com.moutamid.chama.databinding.ActivityGroupSettingBinding;
import com.moutamid.chama.listener.GroupMembers;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GroupSettingActivity extends AppCompatActivity {
    ActivityGroupSettingBinding binding;
    ChatModel chatModel;
    MembersAdapter adapter;
    private static final int PICK_FROM_GALLERY = 1001;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.initDialog(this);
        chatModel = (ChatModel) Stash.getObject(Constants.CHATS, ChatModel.class);

        Glide.with(this).load(chatModel.image).placeholder(R.drawable.profile_icon).into(binding.image);
        binding.name.setText(chatModel.name);
        binding.nameEdit.setText(chatModel.name);

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

        binding.back.setOnClickListener(v -> onBackPressed());

        binding.usersRC.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRC.setHasFixedSize(false);

        adapter = new MembersAdapter(groupMembers, this, chatModel.groupMembers);
        binding.usersRC.setAdapter(adapter);

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

        });
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
                            Stash.put(Constants.CHATS, chatModel);
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
    };

}