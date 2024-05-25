package com.moutamid.chama.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moutamid.chama.R;
import com.moutamid.chama.adapters.ChatAdapter;
import com.moutamid.chama.bottomsheets.ChatMenu;
import com.moutamid.chama.databinding.ActivityChatBinding;
import com.moutamid.chama.listener.ImageSelectionListener;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    ChatModel chatModel;
    ArrayList<MessageModel> list;
    ChatAdapter adapter;
    private static final int PICK_FROM_GALLERY = 1001;
    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Constants.initDialog(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatModel = (ChatModel) Stash.getObject(Constants.CHATS, ChatModel.class);
        binding.back.setOnClickListener(v -> onBackPressed());

        binding.name.setText(chatModel.name);
        Glide.with(this).load(chatModel.image).placeholder(R.drawable.profile_icon).into(binding.image);
        list = new ArrayList<>();
        binding.chat.setLayoutManager(new LinearLayoutManager(this));
        binding.chat.setHasFixedSize(false);

        adapter = new ChatAdapter(this, list);
        binding.chat.setAdapter(adapter);

        Constants.databaseReference().child(Constants.MESSAGES).child(chatModel.id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            MessageModel messageModel = snapshot.getValue(MessageModel.class);
                            list.add(messageModel);
                            list.sort(Comparator.comparingLong(o -> o.timestamp));
                            adapter.notifyItemInserted(list.size() - 1);
                            binding.chat.scrollToPosition(list.size() - 1);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            MessageModel messageModel = snapshot.getValue(MessageModel.class);
                            int index =0;
                            for (int i = 0; i < list.size(); i++) {
                                MessageModel l = list.get(i);
                                if (messageModel.id.equals(l.id)){
                                    index = i;
                                    break;
                                }
                            }
                            list.remove(index);
                            list.add(index, messageModel);
                            list.sort(Comparator.comparingLong(o -> o.timestamp));
                            adapter.notifyItemChanged(index);
                            binding.chat.scrollToPosition(list.size() - 1);
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.clip.setOnClickListener(v -> {
            ChatMenu chatMenu = new ChatMenu(chatModel, imageSelectionListener);
            chatMenu.show(getSupportFragmentManager(), chatMenu.getTag());
        });

        binding.message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
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
            if (!binding.message.getText().toString().isEmpty()) {
                sendMessage();
            }
        });

    }

    ImageSelectionListener imageSelectionListener = () -> {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            showPreviewDialog();
        }
    }

    Dialog imagePreview;

    private void showPreviewDialog() {
        imagePreview = new Dialog(this);
        imagePreview.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imagePreview.setContentView(R.layout.image_preview);
        imagePreview.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        imagePreview.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imagePreview.setCancelable(true);
        imagePreview.show();

        ImageView image = imagePreview.findViewById(R.id.image);
        ImageView back = imagePreview.findViewById(R.id.back);
        TextView name = imagePreview.findViewById(R.id.name);
        MaterialCardView send = imagePreview.findViewById(R.id.send);
        EditText message = imagePreview.findViewById(R.id.message);

        image.setImageURI(imageURI);

        back.setOnClickListener(v -> {
            imagePreview.dismiss();
            imageURI = null;
        });

        name.setText(chatModel.name);

        send.setOnClickListener(v -> {
            Constants.showDialog();
            Constants.storageReference().child("Chat_images").child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date()))
                    .putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            Constants.dismissDialog();
                            imagePreview.dismiss();
                            String m = message.getText().toString();
                            message.setText("");
                            imagePreview = null;
                            sendImage(uri.toString(), m);
                        });
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void sendImage(String link, String message) {
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        MessageModel model = new MessageModel();
        model.id = UUID.randomUUID().toString();
        model.senderID = Constants.auth().getCurrentUser().getUid();
        model.chatID = chatModel.id;
        model.isGroup = chatModel.isGroup;
        model.isMoneyShared = false;
        model.isImageShared = true;
        model.money = "";
        model.timestamp = new Date().getTime();
        model.image = stashUser.image;
        model.message = link;
        model.caption = message;

        String m;

        if (chatModel.isGroup) {
            m = stashUser.name + ": " + (message.isEmpty() ? "Sent an Image" : message);
        } else {
            m = message.isEmpty() ? "Sent an Image" : message;
        }

        Constants.databaseReference().child(Constants.MESSAGES).child(chatModel.id).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamp", model.timestamp);
                    map.put("lastMessage", m);
                    Constants.databaseReference().child(Constants.CHATS).child(stashUser.id).child(chatModel.id).updateChildren(map)
                            .addOnSuccessListener(unused1 -> {
                                if (chatModel.isGroup) {
                                    for (UserModel userModel : chatModel.groupMembers) {
                                        Log.d(TAG, "sendImage: " + userModel.id);
                                        Constants.databaseReference().child(Constants.CHATS).child(userModel.id).child(chatModel.id).updateChildren(map)
                                                .addOnSuccessListener(unused2 -> {
                                                    binding.message.setText("");
                                                });
                                    }
                                } else {
                                    Constants.databaseReference().child(Constants.CHATS).child(chatModel.userID).child(chatModel.id).updateChildren(map)
                                            .addOnSuccessListener(unused2 -> {
                                                binding.message.setText("");
                                            });
                                }
                            });
                });
    }

    private static final String TAG = "ChatActivity";

    private void sendMessage() {
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        MessageModel model = new MessageModel();
        model.id = UUID.randomUUID().toString();
        model.senderID = Constants.auth().getCurrentUser().getUid();
        model.chatID = chatModel.id;
        model.isGroup = chatModel.isGroup;
        model.isMoneyShared = false;
        model.isImageShared = false;
        model.money = "";
        model.timestamp = new Date().getTime();
        model.image = stashUser.image;
        model.message = binding.message.getText().toString().trim();

        String m = chatModel.isGroup ? stashUser.name + ": " + model.message : model.message;

        Constants.databaseReference().child(Constants.MESSAGES).child(chatModel.id).child(model.id)
                .setValue(model).addOnSuccessListener(unused -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("timestamp", model.timestamp);
                    map.put("lastMessage", m);
                    Constants.databaseReference().child(Constants.CHATS).child(stashUser.id).child(chatModel.id).updateChildren(map)
                            .addOnSuccessListener(unused1 -> {
                                if (chatModel.isGroup) {
                                    for (UserModel userModel : chatModel.groupMembers) {
                                        Log.d(TAG, "sendImage: " + userModel.id);
                                        Constants.databaseReference().child(Constants.CHATS).child(userModel.id).child(chatModel.id).updateChildren(map)
                                                .addOnSuccessListener(unused2 -> {
                                                    binding.message.setText("");
                                                });
                                    }
                                } else {
                                    Constants.databaseReference().child(Constants.CHATS).child(chatModel.userID).child(chatModel.id).updateChildren(map)
                                            .addOnSuccessListener(unused2 -> {
                                                binding.message.setText("");
                                            });
                                }
                            });
                });
    }

    @Override
    public void onBackPressed() {
        if (imagePreview != null) {
            if (imagePreview.isShowing()) {
                imagePreview.dismiss();
                imageURI = null;
            }
        } else {
            super.onBackPressed();
            Stash.clear(Constants.CHATS);
        }
    }
}