package com.moutamid.chama.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.activities.ChatActivity;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageVH> {
    Context context;
    ArrayList<ChatModel> list;

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

        holder.name.setText(model.name);
        holder.message.setText(model.lastMessage);
        holder.date.setText(Constants.getTime(model.timestamp));

        if (model.isMoneyShared){
            holder.moneySharedLayout.setVisibility(View.VISIBLE);
            String[] money = model.money.split(", ");
            holder.whoReceive.setText(money[0]);
            holder.money.setText(money[1]);
        }
        Glide.with(context).load(model.image).placeholder(R.drawable.profile_icon).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Stash.put(Constants.CHATS, model);
            context.startActivity(new Intent(context, ChatActivity.class));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MessageVH extends RecyclerView.ViewHolder{
        CircleImageView image;
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
