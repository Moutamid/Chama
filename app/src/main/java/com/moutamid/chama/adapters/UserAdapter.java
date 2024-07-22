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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> implements Filterable {

    Context context;
    ArrayList<UserModel> list;
    ArrayList<UserModel> listAll;
    boolean createGroup;
    GroupCreateListener groupCreateListener;

    public UserAdapter(Context context, ArrayList<UserModel> list, boolean createGroup, GroupCreateListener groupCreateListener) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        this.createGroup = createGroup;
        this.groupCreateListener = groupCreateListener;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserVH(LayoutInflater.from(context).inflate(R.layout.group_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        UserModel userModel = list.get(holder.getAbsoluteAdapterPosition());
        if (createGroup) {
            holder.radio.setVisibility(View.VISIBLE);
        } else {
            holder.radio.setVisibility(View.GONE);
        }
        holder.email.setText(userModel.email);
        holder.name.setText(userModel.name);
        Glide.with(context).load(userModel.image).placeholder(R.drawable.profile_icon).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            if (createGroup){
                holder.radio.setChecked(!holder.radio.isChecked());
            } else {
                groupCreateListener.createChat(userModel);
            }
        });

        if (createGroup){
            holder.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) groupCreateListener.selected(userModel);
                    else groupCreateListener.unselected(userModel);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
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

    public class UserVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, email;
        MaterialRadioButton radio;

        public UserVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            radio = itemView.findViewById(R.id.radio);
        }
    }

}
