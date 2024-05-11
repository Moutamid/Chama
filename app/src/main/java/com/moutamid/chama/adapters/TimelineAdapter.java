package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.chama.R;
import com.moutamid.chama.models.TimelineModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineVH> {
    Context context;
    ArrayList<TimelineModel> list;

    public TimelineAdapter(Context context, ArrayList<TimelineModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TimelineVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimelineVH(LayoutInflater.from(context).inflate(R.layout.timeline_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineVH holder, int position) {
        TimelineModel model = list.get(holder.getAbsoluteAdapterPosition());
        holder.date.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(model.timeline));
        holder.desc.setText(model.desc);
        holder.name.setText(model.name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TimelineVH extends RecyclerView.ViewHolder {
        TextView name, desc, date;
        public TimelineVH(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            desc = itemView.findViewById(R.id.desc);
            name = itemView.findViewById(R.id.name);
        }
    }

}
