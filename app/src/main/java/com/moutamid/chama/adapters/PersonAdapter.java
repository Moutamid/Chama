package com.moutamid.chama.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.moutamid.chama.R;
import com.moutamid.chama.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends ArrayAdapter<UserModel> {
    private Context mContext;
    private int layoutResourceId;
    List<UserModel> data;
//    ArrayList<UserModel> currentItems;

    public PersonAdapter(@NonNull Context context, int resource, @NonNull List<UserModel> objects) {
        super(context, resource, objects);
        mContext = context;
        data = objects;
//        currentItems = new ArrayList<>(objects);
        layoutResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResourceId, parent, false);
        }
        UserModel person = getItem(position);
        ImageView imageView = view.findViewById(R.id.image);
        TextView textView = view.findViewById(R.id.name);
        Glide.with(mContext).load(person.image).placeholder(R.drawable.profile_icon).into(imageView);
        textView.setText(person.name);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

/*    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<UserModel> filterList;
            if (constraint.toString().isEmpty()) {
                filterList = new ArrayList<>(currentItems);
            } else {
                filterList = (ArrayList<UserModel>) data.stream()
                        .filter(item -> item.name.toLowerCase().contains(constraint.toString().toLowerCase()))
                        .collect(Collectors.toList());
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            currentItems.clear();
            currentItems.addAll((Collection<? extends UserModel>) results.values);
            notifyDataSetChanged();
        }
    };*/

}
