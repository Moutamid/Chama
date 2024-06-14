package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.moutamid.chama.R;
import com.moutamid.chama.listener.GroupMembers;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersVH> implements Filterable {
    GroupMembers groupMembers;
    Context context;
    ArrayList<UserModel> list;
    ArrayList<UserModel> listAll;

    public MembersAdapter(GroupMembers groupMembers, Context context, ArrayList<UserModel> list) {
        this.groupMembers = groupMembers;
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public MembersVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembersVH(LayoutInflater.from(context).inflate(R.layout.members, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MembersVH holder, int position) {
        UserModel model = list.get(holder.getAbsoluteAdapterPosition());
        holder.name.setText(model.name);
        String role = model.id.equals(Constants.auth().getCurrentUser().getUid()) ? "admin" : "";
        holder.role.setText(role);

        Glide.with(context).load(model.image).placeholder(R.drawable.profile_icon).into(holder.image);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!model.id.equals(Constants.auth().getCurrentUser().getUid())) {
                    new MaterialAlertDialogBuilder(context)
                            .setMessage("Remove this user from this group?")
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                groupMembers.onRemove(list.get(holder.getAbsoluteAdapterPosition()), holder.getAbsoluteAdapterPosition());
                            })
                            .show();
                }
                return true;
            }
        });

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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends UserModel>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class MembersVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, role;

        public MembersVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            role = itemView.findViewById(R.id.role);
        }
    }

}
