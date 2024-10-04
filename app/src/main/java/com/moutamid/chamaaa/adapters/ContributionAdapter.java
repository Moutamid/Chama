package com.moutamid.chamaaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.models.MessageModel;

import java.util.ArrayList;

public class ContributionAdapter extends RecyclerView.Adapter<ContributionAdapter.ContributionVH> {
    Context context;
    ArrayList<MessageModel> list;

    public ContributionAdapter(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ContributionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContributionVH(LayoutInflater.from(context).inflate(R.layout.contributions_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContributionVH holder, int position) {
        MessageModel model = list.get(holder.getAbsoluteAdapterPosition());
        String message = (holder.getAbsoluteAdapterPosition() + 1) + " - " + model.message + " to " + model.receiverName;
        holder.item.setText(message);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContributionVH extends RecyclerView.ViewHolder{
        TextView item;
        public ContributionVH(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
        }
    }

}
