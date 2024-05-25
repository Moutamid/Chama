package com.moutamid.chama.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.chama.R;
import com.moutamid.chama.activities.GroupSelectionActivity;
import com.moutamid.chama.adapters.MessageAdapter;
import com.moutamid.chama.databinding.FragmentMessageBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class MessageFragment extends Fragment {
    FragmentMessageBinding binding;
    ArrayList<ChatModel> list;
    Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public MessageFragment() {
        // Required empty public constructor
    }
    Dialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(getLayoutInflater(), container, false);

        binding.create.setOnClickListener(v -> startActivity(new Intent(requireContext(), GroupSelectionActivity.class)));

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        binding.messagesRC.setLayoutManager(new LinearLayoutManager(mContext));
        binding.messagesRC.setHasFixedSize(false);
        getMessages();
        return binding.getRoot();
    }

    private void getMessages() {
        list = new ArrayList<>();
        Constants.databaseReference().child(Constants.CHATS)
                .child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ChatModel model = dataSnapshot.getValue(ChatModel.class);
                                list.add(model);
                            }
                            if (!list.isEmpty()){
                                binding.noLayout.setVisibility(View.GONE);
                                binding.messagesRC.setVisibility(View.VISIBLE);
                            }
                            MessageAdapter adapter = new MessageAdapter(mContext, list);
                            binding.messagesRC.setAdapter(adapter);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}