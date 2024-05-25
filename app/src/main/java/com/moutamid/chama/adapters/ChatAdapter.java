package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.chama.R;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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
            }
            return CHAT_RIGHT;
        } else {
            if (model.isImageShared) {
                if (!model.caption.isEmpty()) {
                    return CHAT_LEFT_IMAGE_CAPTION;
                }
                return CHAT_LEFT_IMAGE;
            }
            return CHAT_LEFT;
        }
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CHAT_LEFT) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false));
        } else if (viewType == CHAT_LEFT_IMAGE) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_left_image, parent, false));
        } else if (viewType == CHAT_LEFT_IMAGE_CAPTION) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_left_image_caption, parent, false));
        } else if (viewType == CHAT_RIGHT) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false));
        } else if (viewType == CHAT_RIGHT_IMAGE) {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_right_image, parent, false));
        } else {
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_right_image_caption, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        MessageModel model = list.get(holder.getAbsoluteAdapterPosition());

        if (model.isImageShared) {
            if (!model.caption.isEmpty()) {
                holder.caption.setText(model.caption);
            }
            Glide.with(context).load(model.message).placeholder(R.color.transprent).into(holder.image);
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

        public ChatVH(@NonNull View itemView) {
            super(itemView);
            profile_icon = itemView.findViewById(R.id.profile);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
            caption = itemView.findViewById(R.id.caption);
            image = itemView.findViewById(R.id.image);
        }
    }

}
