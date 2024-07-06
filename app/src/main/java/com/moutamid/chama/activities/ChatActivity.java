package com.moutamid.chama.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.chama.R;
import com.moutamid.chama.adapters.ChatAdapter;
import com.moutamid.chama.bottomsheets.ChatMenu;
import com.moutamid.chama.bottomsheets.WithdrawFunds;
import com.moutamid.chama.databinding.ActivityChatBinding;
import com.moutamid.chama.listener.FundTransfer;
import com.moutamid.chama.listener.ImageSelectionListener;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.TimelineModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.notifications.FcmNotificationsSender;
import com.moutamid.chama.notifications.NotificationScheduler;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String status = "";
    ChatModel chatModel;
    ArrayList<MessageModel> list;
    ChatAdapter adapter;
    private static final int PICK_FROM_GALLERY = 1001;
    Uri imageURI;
    String currentDate;
    final Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat;

    @Override
    protected void onResume() {
        super.onResume();
        chatModel = (ChatModel) Stash.getObject(Constants.CHATS, ChatModel.class);

        if (chatModel.isGroup && chatModel.isSocoGroup) {
            binding.joinGroup.setVisibility(View.VISIBLE);

            if (!chatModel.adminID.equals(Constants.auth().getCurrentUser().getUid())){
                binding.more.setVisibility(View.GONE);
            }

            for (UserModel users : chatModel.groupMembers) {
                if (users.id.equals(Constants.auth().getCurrentUser().getUid())) {
                    binding.joinGroup.setVisibility(View.GONE);
                    break;
                }
            }
        } else {
            binding.joinGroup.setVisibility(View.GONE);
        }

        binding.join.setOnClickListener(v -> {
            UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
            ArrayList<UserModel> users = new ArrayList<>(chatModel.groupMembers);
            stashUser.role = "UNKNOWN_ROLE";
            users.add(stashUser);
            chatModel.groupMembers = new ArrayList<>(users);
            Log.d(TAG, "onResume: " + users.size());
            Constants.databaseReference().child(Constants.SOCO)
                    .child(chatModel.id).setValue(chatModel).addOnSuccessListener(unused -> {
                        Log.d(TAG, "onResume: JOINED");
                        binding.joinGroup.setVisibility(View.GONE);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        if (!chatModel.isGroup) {
            getUserStatus();
        } else {
            Constants.databaseReference().child(Constants.STATUS).child(chatModel.id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String s = snapshot.getValue(String.class);
                        binding.status.setText(s);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        Constants.databaseReference().child(Constants.REMINDERS).child(chatModel.id).child("isReminder")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean reminder = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                            int i = reminder ? View.VISIBLE : View.GONE;
                            binding.reminder.setVisibility(i);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Constants.databaseReference().child(Constants.RUNNING_TOPICS).child(chatModel.id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String topic = snapshot.getValue(String.class);
                                    if (topic.isEmpty()){
                                        binding.running.setVisibility(View.GONE);
                                        binding.topic.setText(topic);
                                    } else {
                                        binding.running.setVisibility(View.VISIBLE);
                                        binding.topic.setText(topic);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        binding.name.setText(chatModel.name);
        Glide.with(this).load(chatModel.image).placeholder(R.drawable.profile_icon).into(binding.image);
        list.clear();
        fetchMessages();
    }

    private void getUserStatus() {
        Constants.databaseReference().child(Constants.STATUS).child(chatModel.userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String s = snapshot.getValue(String.class);
                    if (s.equals("online") || s.equals("offline")) status = s;
                    binding.status.setText(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchMessages() {
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
                            int index = 0;
                            for (int i = 0; i < list.size(); i++) {
                                MessageModel l = list.get(i);
                                if (messageModel.id.equals(l.id)) {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Constants.initDialog(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.back.setOnClickListener(v -> onBackPressed());

        binding.more.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.inflate(R.menu.popup_nav);

            Menu popupMenu = menu.getMenu();
            if (!chatModel.isGroup) {
                popupMenu.getItem(0).setVisible(false);
            }
            menu.show();

            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.setting) {
                    startActivity(new Intent(this, GroupSettingActivity.class));
                } else if (item.getItemId() == R.id.reminder) {
                    showCalender();
                } else if (item.getItemId() == R.id.run) {
                    runTopic();
                }
                return true;
            });

        });

        list = new ArrayList<>();
        binding.chat.setLayoutManager(new LinearLayoutManager(this));
        binding.chat.setHasFixedSize(false);

        adapter = new ChatAdapter(this, list, fundTransfer);
        binding.chat.setAdapter(adapter);


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
                String value;
                if (s.toString().isEmpty()) {
                    value = status;
                    binding.clip.setVisibility(View.VISIBLE);
                } else {
                    UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
                    value = userModel.name + " typing...";
                    binding.clip.setVisibility(View.GONE);
                }
                if (chatModel.isGroup) Constants.databaseReference().child(Constants.STATUS).child(chatModel.id).setValue(value);
                else Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid()).setValue(value);
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

        binding.end.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("End this topic")
                    .setMessage("Are you sure you want to end this topic for all?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(Constants.RUNNING_TOPICS).child(chatModel.id).setValue("");
                    }).setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

    }

    private void runTopic() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.running_topic);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();

        TextInputLayout topic = dialog.findViewById(R.id.topic);
        MaterialButton run = dialog.findViewById(R.id.run);
        MaterialButton close = dialog.findViewById(R.id.close);

        close.setOnClickListener(v ->  dialog.dismiss());

        run.setOnClickListener(v -> {
            if (topic.getEditText().getText().toString().isEmpty()){
                topic.setErrorEnabled(true);
                topic.getEditText().setError("Topic is empty");
            } else {
                dialog.dismiss();
                String s = topic.getEditText().getText().toString().trim();
                Constants.databaseReference().child(Constants.RUNNING_TOPICS).child(chatModel.id).setValue(s);
                String message = "Running topic" + (chatModel.isGroup ? " in " : " with ") + chatModel.name + " is " + s;
                addTimeline("Running Topic", message);
            }
        });

    }

    private void showCalender() {
        DatePickerDialog.OnDateSetListener date = (datePicker, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            showTimePicker();
        };
        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        TimePickerDialog.OnTimeSetListener time = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            setReminder();
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, time,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Set Time", timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", (dialog, which) -> {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
        });

        timePickerDialog.show();
    }

    private void setReminder() {
        binding.reminder.setVisibility(View.VISIBLE);
        long delayInMillis = calendar.getTime().getTime();
        Log.d(TAG, "setReminder: " + delayInMillis);
        Log.d(TAG, "setReminder: " + calendar.getTime().getTime());
        String[] topics;
        List<String> topicsList = new ArrayList<>();
        if (chatModel.isGroup) {
            for (UserModel users : chatModel.groupMembers) {
                topicsList.add(users.id);
            }
            topics = topicsList.toArray(new String[0]);
        } else {
            topics = new String[]{Constants.auth().getCurrentUser().getUid(), chatModel.userID};
        }

        String title = "Meeting Reminder";
        String body = "Reminder of impending meeting" + (chatModel.isGroup ? " in " : " with ") + chatModel.name;
        NotificationScheduler.scheduleAlarmNotification(this, calendar, topics, title, body, chatModel.id);

        Map<String, Object> reminder = new HashMap<>();
        reminder.put("isReminder", true);
        Constants.databaseReference().child(Constants.REMINDERS).child(chatModel.id).updateChildren(reminder);

        addTimeline("Impending Meeting", body);
    }

    private void addTimeline(String title, String message) {
        String[] ids;
        List<String> topicsList = new ArrayList<>();
        if (chatModel.isGroup) {
            for (UserModel users : chatModel.groupMembers) {
                topicsList.add(users.id);
            }
            ids = topicsList.toArray(new String[0]);
        } else {
            ids = new String[]{Constants.auth().getCurrentUser().getUid(), chatModel.userID};
        }

        TimelineModel timelineModel = new TimelineModel();
        timelineModel.isChat = true;
        timelineModel.id = UUID.randomUUID().toString();
        timelineModel.chatID = chatModel.id;
        timelineModel.timeline = new Date().getTime();
        timelineModel.name = title;
        timelineModel.desc = message;

        for (String id : ids) {
            Constants.databaseReference().child(Constants.TIMELINE).child(id)
                    .child(timelineModel.id).setValue(timelineModel).addOnSuccessListener(unused -> {
                        // Success
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    FundTransfer fundTransfer = new FundTransfer() {
        @Override
        public void onWithdraw(MessageModel model) {
            WithdrawFunds withdrawFunds = new WithdrawFunds(chatModel, model);
            withdrawFunds.show(ChatActivity.this.getSupportFragmentManager(), withdrawFunds.getTag());
        }

        @Override
        public void onReceipt(MessageModel model) {

        }
    };

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
                                                    if (chatModel.isGroup) Constants.databaseReference().child(Constants.STATUS).child(chatModel.id).setValue(status);
                                                    else Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid()).setValue("online");
                                                });
                                    }
                                } else {
                                    Constants.databaseReference().child(Constants.CHATS).child(chatModel.userID).child(chatModel.id).updateChildren(map)
                                            .addOnSuccessListener(unused2 -> {
                                                binding.message.setText("");
                                                if (chatModel.isGroup) Constants.databaseReference().child(Constants.STATUS).child(chatModel.id).setValue(status);
                                                else Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid()).setValue("online");
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
                    map.put("isMoneyShared", false);

                    if (chatModel.isGroup && chatModel.isSocoGroup) {
                        Constants.databaseReference().child(Constants.SOCO).child(chatModel.id).updateChildren(map)
                                .addOnSuccessListener(unused2 -> {
                                    binding.message.setText("");
                                    if (chatModel.isGroup) Constants.databaseReference().child(Constants.STATUS).child(chatModel.id).setValue(status);
                                    else Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid()).setValue("online");
                                    ArrayList<String> ids = new ArrayList<>();
                                    for (UserModel userModel : chatModel.groupMembers) {
                                        if (!userModel.id.equals(Constants.auth().getCurrentUser().getUid())) ids.add(userModel.id);
                                    }
                                    String[] id = ids.toArray(new String[0]);
                                    new FcmNotificationsSender(id, chatModel.name, m, this, chatModel.id, false).SendNotifications();
                                });
                    } else {
                        update(map);
                    }
                });
    }

    private void update(Map<String, Object> map) {
        UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        Constants.databaseReference().child(Constants.CHATS).child(stashUser.id).child(chatModel.id).updateChildren(map)
                .addOnSuccessListener(unused1 -> {
                    if (chatModel.isGroup) {
                        for (UserModel userModel : chatModel.groupMembers) {
                            Log.d(TAG, "sendImage: " + userModel.id);
                            Constants.databaseReference().child(Constants.CHATS).child(userModel.id).child(chatModel.id).updateChildren(map)
                                    .addOnSuccessListener(unused2 -> {
                                        binding.message.setText("");
                                        Constants.databaseReference().child(Constants.STATUS).child(chatModel.id).setValue(status);
                                    });
                        }
                        ArrayList<String> ids = new ArrayList<>();
                        for (UserModel userModel : chatModel.groupMembers) {
                            if (!userModel.id.equals(Constants.auth().getCurrentUser().getUid())) ids.add(userModel.id);
                        }
                        String[] id = ids.toArray(new String[0]);
                        Log.d(TAG, "update: " + id.length);
                        new FcmNotificationsSender(id, chatModel.name, map.get("lastMessage").toString(), this, chatModel.id, false).SendNotifications();
                    } else {
                        Constants.databaseReference().child(Constants.CHATS).child(chatModel.userID).child(chatModel.id).updateChildren(map)
                                .addOnSuccessListener(unused2 -> {
                                    binding.message.setText("");
                                    Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid()).setValue("online");
                                    String[] id = new String[]{chatModel.userID};
                                    new FcmNotificationsSender(id, chatModel.name, map.get("lastMessage").toString(), this, chatModel.id, false).SendNotifications();
                                });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatModel.isGroup) Constants.databaseReference().child(Constants.STATUS).child(chatModel.id).setValue(status);
        else Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid()).setValue(status);
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