package com.moutamid.chama.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.moutamid.chama.R;
import com.moutamid.chama.listener.GroupCreateListener;
import com.moutamid.chama.models.UserModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class NewMembersAdapter extends RecyclerView.Adapter<NewMembersAdapter.MembersVH> implements Filterable {
    Context context;
    ArrayList<UserModel> list;
    ArrayList<UserModel> listAll;
    GroupCreateListener groupCreateListener;

    public NewMembersAdapter(Context context, ArrayList<UserModel> list, GroupCreateListener groupCreateListener) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        this.groupCreateListener = groupCreateListener;
    }

    @NonNull
    @Override
    public MembersVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembersVH(LayoutInflater.from(context).inflate(R.layout.group_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MembersVH holder, int position) {
        UserModel userModel = list.get(holder.getAbsoluteAdapterPosition());
        holder.radio.setVisibility(View.VISIBLE);
        holder.name.setText(userModel.name);
        Glide.with(context).load(userModel.image).placeholder(R.drawable.profile_icon).into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            holder.radio.setChecked(!holder.radio.isChecked());
        });
        holder.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) groupCreateListener.selected(userModel);
                else groupCreateListener.unselected(userModel);
            }
        });

    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<UserModel> filterList;
            if (constraint.toString().isEmpty()) {
                filterList = new ArrayList<>(listAll);
            } else {
                filterList = (ArrayList<UserModel>) listAll.stream()
                        .filter(item -> item.name.toLowerCase().contains(constraint.toString().toLowerCase()))
                        .collect(Collectors.toList());
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends UserModel>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MembersVH extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        MaterialRadioButton radio;
        public MembersVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            radio = itemView.findViewById(R.id.radio);
        }
    }

}
