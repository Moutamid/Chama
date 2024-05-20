package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.chama.models.MessageModel;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatVH> {
    Context context;
    ArrayList<MessageModel> list;
    public static final int CHAT_LEFT = 1;
    public static final int CHAT_RIGHT = 2;
    public static final int CHAT_LEFT_SEND_MONEY = 3;
    public static final int CHAT_RIGHT_SEND_MONEY = 4;

    public ChatAdapter(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatVH extends RecyclerView.ViewHolder{
        public ChatVH(@NonNull View itemView) {
            super(itemView);
        }
    }

}
