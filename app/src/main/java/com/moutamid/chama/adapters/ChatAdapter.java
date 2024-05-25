package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.moutamid.chama.R;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.PollModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatVH> {
    Context context;
    ArrayList<MessageModel> list;
    public static final int CHAT_LEFT = 1;
    public static final int CHAT_RIGHT = 2;
    public static final int CHAT_LEFT_IMAGE = 3;
    public static final int CHAT_RIGHT_IMAGE = 4;
    public static final int CHAT_LEFT_IMAGE_CAPTION = 5;
    public static final int CHAT_RIGHT_IMAGE_CAPTION = 6;
    public static final int CHAT_LEFT_POLL = 7;
    public static final int CHAT_RIGHT_POLL = 8;
    LayoutInflater inflater;

    public ChatAdapter(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel model = list.get(position);
        if (model.senderID.equals(Constants.auth().getCurrentUser().getUid())) {
            if (model.isImageShared) {
                if (!model.caption.isEmpty()) {
                    return CHAT_RIGHT_IMAGE_CAPTION;
                }
                return CHAT_RIGHT_IMAGE;
            } else if (model.isPoll) {
                return CHAT_RIGHT_POLL;
            } else {
                return CHAT_RIGHT;
            }
        } else {
            if (model.isImageShared) {
                if (!model.caption.isEmpty()) {
                    return CHAT_LEFT_IMAGE_CAPTION;
                }
                return CHAT_LEFT_IMAGE;
            } else if (model.isPoll) {
                return CHAT_LEFT_POLL;
            } else {
                return CHAT_LEFT;
            }
        }
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);

        if (viewType == CHAT_LEFT) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false));
        } else if (viewType == CHAT_LEFT_IMAGE) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_left_image, parent, false));
        } else if (viewType == CHAT_LEFT_IMAGE_CAPTION) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_left_image_caption, parent, false));
        } else if (viewType == CHAT_LEFT_POLL) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.poll_left, parent, false));
        } else if (viewType == CHAT_RIGHT) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false));
        } else if (viewType == CHAT_RIGHT_IMAGE) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_right_image, parent, false));
        } else if (viewType == CHAT_RIGHT_IMAGE_CAPTION) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_right_image_caption, parent, false));
        } else {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.poll_right, parent, false));
        }
    }

    MaterialRadioButton global;
    TextView globalCounter;
    LinearProgressIndicator globalProgressBar;

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        MessageModel model = list.get(holder.getAbsoluteAdapterPosition());

        if (model.isImageShared) {
            if (!model.caption.isEmpty()) {
                holder.caption.setText(model.caption);
            }
            Glide.with(context).load(model.message).placeholder(R.color.transprent).into(holder.image);
        } else if (model.isPoll) {
            PollModel pollModel = model.pollModel;
            holder.message.setText(pollModel.question);
            holder.radio_group.removeAllViews();
            for (String options : pollModel.options) {
                View customLayout = inflater.inflate(R.layout.poll_options, null);
                MaterialCardView card = customLayout.findViewById(R.id.card);
                MaterialRadioButton radio = customLayout.findViewById(R.id.option);
                TextView counter = customLayout.findViewById(R.id.counter);
                LinearProgressIndicator progressBar = customLayout.findViewById(R.id.progressBar);
                progressBar.setMax(pollModel.options.size());
                radio.setText(options);

                card.setOnClickListener(v -> {
                    radio.setChecked(!radio.isChecked());
                    if (global != null && radio != global) {
                        global.setChecked(false);
                        int c = Integer.parseInt(globalCounter.getText().toString()) - 1;
                        if (c < 0) c = 0;
                        globalCounter.setText(String.valueOf(c));
                        globalProgressBar.setProgress(c, true);
                        Map<String, Object> votes = new HashMap<>();
                        int vote = pollModel.votes.votes.get(options.replace(" ", "_"));
                        votes.put(radio.getText().toString().replace(" ", "_"), vote - 1);
                        Constants.databaseReference().child(Constants.MESSAGES).child(model.chatID).child(model.id).child("pollModel").child("votes").child("votes")
                                .updateChildren(votes);
                        String voter = Constants.auth().getCurrentUser().getUid();
                        Constants.databaseReference().child(Constants.MESSAGES).child(model.chatID).child(model.id).child("pollModel").child("votes").child("voters")
                                .child(global.getText().toString().replace(" ", "_")).child(voter).removeValue();
                    }
                    int count;
                    if (radio.isChecked()) {
                        count = Integer.parseInt(counter.getText().toString()) + 1;
                        counter.setText(String.valueOf(count));
                        progressBar.setProgress(count, true);

                        Map<String, Object> votes = new HashMap<>();
                        int vote = pollModel.votes.votes.get(options.replace(" ", "_"));
                        votes.put(radio.getText().toString().replace(" ", "_"), vote + 1);

                        Constants.databaseReference().child(Constants.MESSAGES).child(model.chatID).child(model.id).child("pollModel").child("votes").child("votes")
                                .updateChildren(votes);

                        Map<String, Object> voters = new HashMap<>();
                        String voter = Constants.auth().getCurrentUser().getUid();
                        voters.put(voter, voter);

                        Constants.databaseReference().child(Constants.MESSAGES).child(model.chatID).child(model.id).child("pollModel").child("votes").child("voters")
                                .child(radio.getText().toString().replace(" ", "_"))
                                .updateChildren(voters);

                    } else {
                        count = Integer.parseInt(counter.getText().toString()) - 1;
                        if (count < 0) count = 0;
                        counter.setText(String.valueOf(count));
                        progressBar.setProgress(count, true);
                        Map<String, Object> votes = new HashMap<>();
                        int vote = pollModel.votes.votes.get(options.replace(" ", "_"));
                        votes.put(radio.getText().toString().replace(" ", "_"), vote - 1);
                        Constants.databaseReference().child(Constants.MESSAGES).child(model.chatID).child(model.id).child("pollModel").child("votes").child("votes")
                                .updateChildren(votes);
                        String voter = Constants.auth().getCurrentUser().getUid();
                        Constants.databaseReference().child(Constants.MESSAGES).child(model.chatID).child(model.id).child("pollModel").child("votes").child("voters")
                                .child(radio.getText().toString().replace(" ", "_")).child(voter).removeValue();
                    }

                    global = radio;
                    globalCounter = counter;
                    globalProgressBar = progressBar;
                });
                int value = pollModel.votes.votes.get(options.replace(" ", "_"));
                counter.setText(String.valueOf(value));
                progressBar.setProgress(value, true);
                Constants.databaseReference().child(Constants.MESSAGES).child(model.chatID).child(model.id).child("pollModel").child("votes").child("voters")
                        .child(options.replace(" ", "_")).child(Constants.auth().getCurrentUser().getUid())
                                .get().addOnSuccessListener(dataSnapshot -> {
                            if (dataSnapshot.exists()) {
                                global = radio;
                                globalCounter = counter;
                                globalProgressBar = progressBar;
                                radio.setChecked(true);
                            }
                        });

                holder.radio_group.addView(customLayout);
            }
        } else {
            holder.message.setText(model.message);
        }
        holder.date.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(model.timestamp));
        Glide.with(context).load(model.image).placeholder(R.drawable.profile_icon).into(holder.profile_icon);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatVH extends RecyclerView.ViewHolder {
        TextView message, date, caption;
        ImageView image;
        CircleImageView profile_icon;
        RadioGroup radio_group;

        public ChatVH(@NonNull View itemView) {
            super(itemView);
            profile_icon = itemView.findViewById(R.id.profile);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            caption = itemView.findViewById(R.id.caption);
            image = itemView.findViewById(R.id.image);
            radio_group = itemView.findViewById(R.id.radio_group);
        }
    }

}
