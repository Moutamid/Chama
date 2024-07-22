package com.moutamid.chama.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.util.Collections;
import java.util.Comparator;


public class MessageFragment extends Fragment {
    FragmentMessageBinding binding;
    ArrayList<ChatModel> list;
    Context mContext;
    Dialog dialog;

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

        return binding.getRoot();
    }

    private static final String TAG = "MessageFragment";
    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        getMessages();
    }

    private void getMessages() {
        Constants.databaseReference().child(Constants.CHATS)
                .child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (!list.isEmpty()) {
                                ChatModel firstItem = list.get(0);
                                list.clear();
                                list.add(firstItem);
                            }
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ChatModel model = snapshot.getValue(ChatModel.class);
                                list.add(model);
                            }
                        }
                        Log.d(TAG, "getMessages: " + list.size());
                        if (!list.isEmpty()) {
                            binding.noLayout.setVisibility(View.GONE);
                            binding.messagesRC.setVisibility(View.VISIBLE);
                        }
                        list.sort(Comparator.comparing(chatModel -> chatModel.timestamp));
                        Collections.reverse(list);
                        MessageAdapter adapter = new MessageAdapter(mContext, list);
                        binding.messagesRC.setAdapter(adapter);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}