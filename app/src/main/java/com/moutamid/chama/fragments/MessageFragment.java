package com.moutamid.chama.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.chama.R;
import com.moutamid.chama.adapters.MessageAdapter;
import com.moutamid.chama.databinding.FragmentMessageBinding;
import com.moutamid.chama.models.ChatModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class MessageFragment extends Fragment {
    FragmentMessageBinding binding;
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(getLayoutInflater(), container, false);

        binding.create.setOnClickListener(v -> createMessages());

        binding.messagesRC.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.messagesRC.setHasFixedSize(false);

        return binding.getRoot();
    }

    private void createMessages() {
        binding.noLayout.setVisibility(View.GONE);
        binding.messagesRC.setVisibility(View.VISIBLE);

        ArrayList<ChatModel> list =  new ArrayList<>();
        list.add(new ChatModel(UUID.randomUUID().toString(), "Alexa", "", getString(R.string.lorem), "You got, USD 250", true, false, new Date().getTime()));
        list.add(new ChatModel(UUID.randomUUID().toString(), "Nick Jones", "", getString(R.string.lorem), "You shared, USD 20", true, false, new Date().getTime()));
        list.add(new ChatModel(UUID.randomUUID().toString(), "James", "", getString(R.string.lorem), "", false, false, new Date().getTime()));
        list.add(new ChatModel(UUID.randomUUID().toString(), "Jeema", "", getString(R.string.lorem), "You shared, USD 350", true, false, new Date().getTime()));
        list.add(new ChatModel(UUID.randomUUID().toString(), "Chama Group", "", getString(R.string.lorem), "", false, true, new Date().getTime()));

        MessageAdapter adapter = new MessageAdapter(requireContext(), list);
        binding.messagesRC.setAdapter(adapter);
    }
}