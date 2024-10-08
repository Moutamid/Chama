package com.moutamid.chamaaa.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.activities.ChatActivity;
import com.moutamid.chamaaa.models.ChatModel;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageVH> {
    Context context;
    ArrayList<ChatModel> list;
    private static final String TAG = "MessageAdapter";

    public MessageAdapter(Context context, ArrayList<ChatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MessageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageVH(LayoutInflater.from(context).inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageVH holder, int position) {
        ChatModel model = list.get(holder.getAbsoluteAdapterPosition());
        Log.d(TAG, "onBindViewHolder: " + model.name);
        holder.name.setText(model.name);
        holder.message.setText(model.lastMessage);
        holder.date.setText(Constants.getTime(model.timestamp));

        Glide.with(context).load(model.image).placeholder(R.drawable.profile_icon).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Stash.put(Constants.CHATS, model);
            context.startActivity(new Intent(context, ChatActivity.class));
        });

        if (model.isMoneyShared) {
            holder.moneySharedLayout.setVisibility(View.VISIBLE);
            holder.whoReceive.setText(model.whoShared.split(" - ")[0]);
            holder.money.setText(model.whoShared.split(" - ")[1]);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MessageVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, message, date, whoReceive, money;
        LinearLayout moneySharedLayout;

        public MessageVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            message = itemView.findViewById(R.id.message);
            whoReceive = itemView.findViewById(R.id.whoReceive);
            money = itemView.findViewById(R.id.money);
            moneySharedLayout = itemView.findViewById(R.id.moneySharedLayout);
        }
    }

}
