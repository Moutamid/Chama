package com.moutamid.chama.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chama.adapters.UserAdapter;
import com.moutamid.chama.databinding.ActivityGroupSelectionBinding;
import com.moutamid.chama.listener.GroupCreateListener;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class GroupSelectionActivity extends AppCompatActivity {
    ActivityGroupSelectionBinding binding;
    UserAdapter adapter;
    ArrayList<UserModel> list;
    ArrayList<UserModel> currentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> finish());

        list = new ArrayList<>();
        currentItems = new ArrayList<>();

        binding.usersRC.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRC.setHasFixedSize(false);

        binding.discard.setOnClickListener(v -> {
            binding.createGroup.setVisibility(View.VISIBLE);
            binding.groupLayout.setVisibility(View.GONE);
            binding.create.setVisibility(View.GONE);
            adapter = new UserAdapter(this, list, false, groupCreateListener);
            binding.usersRC.setAdapter(adapter);
        });

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.create.setOnClickListener(v -> {
            if (binding.groupName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Enter Group Name", Toast.LENGTH_SHORT).show();
                return;
            }
            createGroup();
        });

        binding.createGroup.setOnClickListener(v -> {
            binding.createGroup.setVisibility(View.GONE);
            binding.groupLayout.setVisibility(View.VISIBLE);
            binding.create.setVisibility(View.VISIBLE);
            adapter = new UserAdapter(this, list, true, groupCreateListener);
            binding.usersRC.setAdapter(adapter);
        });

    }

    private void createGroup() {
        Constants.showDialog();
        ChatModel senderModel = new ChatModel();
        ChatModel receiverModel = new ChatModel();
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        stashUser.role = "OWNER";
        currentItems.add(stashUser);

        String ID = UUID.randomUUID().toString();

        senderModel.id = ID;
        receiverModel.id = ID;
        senderModel.userID = "";
        receiverModel.userID = "";

        senderModel.money = "";
        receiverModel.money = "";

        senderModel.timestamp = new Date().getTime();
        receiverModel.timestamp = new Date().getTime();

        senderModel.name = binding.groupName.getText().toString();
        receiverModel.name = binding.groupName.getText().toString();
        senderModel.image = "";
        receiverModel.image = "";

        senderModel.isGroup = true;
        receiverModel.isGroup = true;

        senderModel.isMoneyShared = false;
        senderModel.isSocoGroup = false;

        receiverModel.isMoneyShared = false;
        receiverModel.isSocoGroup = false;

        senderModel.adminID = Constants.auth().getCurrentUser().getUid();
        receiverModel.adminID = Constants.auth().getCurrentUser().getUid();

        senderModel.lastMessage = "Start Messaging";
        receiverModel.lastMessage = "Start Messaging";

        senderModel.groupMembers = new ArrayList<>(currentItems);
        receiverModel.groupMembers = new ArrayList<>(currentItems);

        Constants.databaseReference().child(Constants.CHATS).child(Constants.auth().getCurrentUser().getUid())
                .child(ID).setValue(senderModel).addOnSuccessListener(unused -> {
                    int i = 0;
                    for (UserModel userModel : currentItems) {
                        i++;
                        int finalI = i;
                        Constants.databaseReference().child(Constants.CHATS).child(userModel.id)
                                .child(ID).setValue(receiverModel).addOnSuccessListener(unused1 -> {
                                    if (finalI >= currentItems.size() - 1) {
                                        Stash.put(Constants.CHATS, senderModel);
                                        startActivity(new Intent(GroupSelectionActivity.this, ChatActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(e -> {
                                    Constants.dismissDialog();
                                    Toast.makeText(GroupSelectionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(GroupSelectionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    GroupCreateListener groupCreateListener = new GroupCreateListener() {
        @Override
        public void selected(UserModel userModel) {
            userModel.password = null;
            userModel.role = "UNKNOWN_ROLE";
            currentItems.add(userModel);
        }

        @Override
        public void unselected(UserModel userModel) {
            for (int i = 0, currentItemsSize = currentItems.size(); i < currentItemsSize; i++) {
                UserModel user = currentItems.get(i);
                if (user.id.equals(userModel.id)) {
                    currentItems.remove(i);
                    break;
                }
            }
        }

        @Override
        public void createChat(UserModel userModel) {
            Constants.showDialog();
            Constants.databaseReference().child(Constants.CHATS)
                    .child(Constants.auth().getCurrentUser().getUid())
                    .get().addOnSuccessListener(dataSnapshot -> {
                        Constants.dismissDialog();
                        if (dataSnapshot.exists()) {
                            ChatModel finalModel = null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ChatModel model = snapshot.getValue(ChatModel.class);
                                if (!model.isGroup) {
                                    if (model.userID.equals(userModel.id)) {
                                        finalModel = model;
                                        break;
                                    }
                                }
                            }
                            if (finalModel != null) {
                                Stash.put(Constants.CHATS, finalModel);
                                startActivity(new Intent(GroupSelectionActivity.this, ChatActivity.class));
                                finish();
                            } else {
                                makeNew(userModel);
                            }
                        } else {
                            makeNew(userModel);
                        }
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(GroupSelectionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    };

    private void makeNew(UserModel userModel) {
        ChatModel senderModel = new ChatModel();
        ChatModel receiverModel = new ChatModel();
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);

        String ID = UUID.randomUUID().toString();

        senderModel.id = ID;
        receiverModel.id = ID;
        senderModel.userID = userModel.id;
        receiverModel.userID = Constants.auth().getCurrentUser().getUid();

        senderModel.money = "";
        receiverModel.money = "";

        senderModel.timestamp = new Date().getTime();
        receiverModel.timestamp = new Date().getTime();

        senderModel.name = userModel.name;
        receiverModel.name = stashUser.name;
        senderModel.image = userModel.image;
        receiverModel.image = stashUser.image;

        senderModel.isGroup = false;
        receiverModel.isGroup = false;
        senderModel.isMoneyShared = false;
        receiverModel.isMoneyShared = false;

        senderModel.lastMessage = "Start Messaging";
        receiverModel.lastMessage = "Start Messaging";

        Constants.databaseReference().child(Constants.CHATS).child(Constants.auth().getCurrentUser().getUid())
                .child(ID).setValue(senderModel).addOnSuccessListener(unused -> {
                    Constants.databaseReference().child(Constants.CHATS).child(userModel.id)
                            .child(ID).setValue(receiverModel).addOnSuccessListener(unused1 -> {
                                Stash.put(Constants.CHATS, senderModel);
                                startActivity(new Intent(GroupSelectionActivity.this, ChatActivity.class));
                                finish();
                            }).addOnFailureListener(e -> {
                                Constants.dismissDialog();
                                Toast.makeText(GroupSelectionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(GroupSelectionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        getUsers();
    }

    private void getUsers() {
        Constants.showDialog();
        Constants.databaseReference().child(Constants.USER).get()
                .addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if (!userModel.id.equals(Constants.auth().getCurrentUser().getUid()))
                                list.add(userModel);
                        }
                        adapter = new UserAdapter(this, list, false, groupCreateListener);
                        binding.usersRC.setAdapter(adapter);
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}