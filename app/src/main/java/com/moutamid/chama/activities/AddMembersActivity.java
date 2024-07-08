package com.moutamid.chama.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chama.adapters.NewMembersAdapter;
import com.moutamid.chama.databinding.ActivityAddMembersBinding;
import com.moutamid.chama.listener.GroupCreateListener;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;

public class AddMembersActivity extends AppCompatActivity {
    ActivityAddMembersBinding binding;
    NewMembersAdapter adapter;
    ArrayList<UserModel> list;
    ArrayList<UserModel> currentItems;
    ChatModel chatModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatModel = (ChatModel) Stash.getObject(Constants.CHATS, ChatModel.class);

        binding.back.setOnClickListener(v -> finish());

        list = new ArrayList<>();
        currentItems = new ArrayList<>();

        binding.usersRC.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRC.setHasFixedSize(false);

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
            addMembers();
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

        }
    };
    private static final String TAG = "AddMembersActivity";

    private void addMembers() {
        Constants.showDialog();
        chatModel.groupMembers.addAll(currentItems);
        ArrayList<UserModel> groupMembers = chatModel.groupMembers;
        if (chatModel.isSocoGroup) {
            Constants.databaseReference().child(Constants.SOCO)
                    .child(chatModel.id).child("groupMembers").setValue(chatModel.groupMembers)
                    .addOnSuccessListener(unused -> {
                        Stash.put(Constants.CHATS, chatModel);
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            for (int i = 0; i < groupMembers.size(); i++) {
                UserModel users = groupMembers.get(i);
                int finalI = i;
                Constants.databaseReference().child(Constants.CHATS).child(users.id)
                        .child(chatModel.id).child("groupMembers").setValue(chatModel.groupMembers)
                        .addOnSuccessListener(unused -> {
                            if (finalI == groupMembers.size()-1) {
                                Stash.put(Constants.CHATS, chatModel);
                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Constants.dismissDialog();
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        }
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
                            boolean isUnique = chatModel.groupMembers.stream()
                                    .noneMatch(users -> userModel.id.equals(users.id));
                            if (isUnique) {
                                list.add(userModel);
                            }
                        }
                        adapter = new NewMembersAdapter(this, list, groupCreateListener);
                        binding.usersRC.setAdapter(adapter);
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}