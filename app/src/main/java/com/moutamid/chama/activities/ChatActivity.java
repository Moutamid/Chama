package com.moutamid.chama.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.bottomsheets.ChatMenu;
import com.moutamid.chama.databinding.ActivityChatBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    ChatModel chatModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatModel = (ChatModel) Stash.getObject(Constants.CHATS, ChatModel.class);
        binding.back.setOnClickListener(v -> onBackPressed());

        binding.name.setText(chatModel.name);
        Glide.with(this).load(chatModel.image).placeholder(R.drawable.profile_icon).into(binding.image);

        binding.chat.setLayoutManager(new LinearLayoutManager(this));
        binding.chat.setHasFixedSize(false);

        binding.clip.setOnClickListener(v -> {
            ChatMenu chatMenu = new ChatMenu(chatModel);
            chatMenu.show(getSupportFragmentManager(), chatMenu.getTag());
        });

        binding.message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    binding.clip.setVisibility(View.VISIBLE);
                } else {
                    binding.clip.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.send.setOnClickListener(v -> {
            if (!binding.message.getText().toString().isEmpty()){
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        MessageModel model = new MessageModel();
        model.id = UUID.randomUUID().toString();
        model.senderID = Constants.auth().getCurrentUser().getUid();
        model.chatID = chatModel.id;
        model.isGroup = chatModel.isGroup;
        model.isMoneyShared = false;
        model.money = "";
        model.timestamp = new Date().getTime();
        model.image = stashUser.image;
        model.message = binding.message.getText().toString().trim();

        Constants.databaseReference().child(Constants.MESSAGES).child(chatModel.id).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamp", model.timestamp);
                    map.put("lastMessage", model.message);
                    Constants.databaseReference().child(Constants.CHATS).child(stashUser.id).child(chatModel.id).updateChildren(map)
                            .addOnSuccessListener(unused1 -> {
                                Constants.databaseReference().child(Constants.CHATS).child(chatModel.userID).child(chatModel.id).updateChildren(map)
                                        .addOnSuccessListener(unused2 -> {
                                            binding.message.setText("");
                                        });
                            });
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Stash.clear(Constants.CHATS);
    }
}