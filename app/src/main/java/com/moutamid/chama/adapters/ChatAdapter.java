package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.chama.R;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.utilis.Constants;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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
        return list.get(position).senderID.equals(Constants.auth().getCurrentUser().getUid()) ? CHAT_RIGHT : CHAT_LEFT;
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CHAT_LEFT){
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false));
        } else{
            return new ChatVH(LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        MessageModel model = list.get(holder.getAbsoluteAdapterPosition());
        holder.message.setText(model.message);
        holder.date.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(model.timestamp));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatVH extends RecyclerView.ViewHolder{
        TextView message, date;
        CircleImageView profile_icon;
        public ChatVH(@NonNull View itemView) {
            super(itemView);
            profile_icon = itemView.findViewById(R.id.profile);
            message = itemView.findViewById(R.id.message);
            date = itemView.findViewById(R.id.date);
        }
    }

}
