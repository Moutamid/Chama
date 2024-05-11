package com.moutamid.chama.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.chama.R;
import com.moutamid.chama.databinding.FragmentMessageBinding;


public class MessageFragment extends Fragment {
    FragmentMessageBinding binding;
    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(getLayoutInflater(), container, false);



        return binding.getRoot();
    }
}