package com.moutamid.chamaaa.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.adapters.SaleMainAdapter;
import com.moutamid.chamaaa.databinding.FragmentCreditSaleBinding;
import com.moutamid.chamaaa.models.ChatModel;
import com.moutamid.chamaaa.models.SaleModel;
import com.moutamid.chamaaa.models.Sell;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreditSaleFragment extends Fragment {
    FragmentCreditSaleBinding binding;
    ArrayList<Sell> saleList = new ArrayList<>();
    ArrayList<ChatModel> groups;
    public CreditSaleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreditSaleBinding.inflate(getLayoutInflater(), container, false);
        binding.main.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.main.setHasFixedSize(false);

        Constants.showDialog();
        manualData();
        return binding.getRoot();
    }

    private void manualData() {
        groups = new ArrayList<>();
        Constants.databaseReference().child(Constants.CHATS)
                .child(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ChatModel model = snapshot.getValue(ChatModel.class);
                            if (model.isBusinessGroup)
                                groups.add(model);
                        }
                    }

                    List<String> names = groups.stream()
                            .map(products -> products.name)
                            .collect(Collectors.toList());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, names);
                    binding.groupItem.setAdapter(adapter);
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.search.setOnClickListener(v -> {
            ChatModel chatModel = groups.stream()
                    .filter(productModel -> productModel.name.trim().equals(binding.group.getEditText().getText().toString().trim()))
                    .findFirst()
                    .orElse(null);
            if (chatModel != null) {
                showData(chatModel);
            } else {
                Toast.makeText(requireContext(), "No Product Found for this group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showData(ChatModel chatModel) {
        Constants.databaseReference().child(Constants.CREDIT_SALE).child(chatModel.id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            saleList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ArrayList<SaleModel> subList = new ArrayList<>();
                                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                    SaleModel model = dataSnapshot2.getValue(SaleModel.class);
                                    subList.add(model);
                                }
                                saleList.add(new Sell(dataSnapshot.getKey(), subList));
                            }
                            if (isAdded()) updateTable(saleList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateTable(ArrayList<Sell> mainList) {
        SaleMainAdapter adapter = new SaleMainAdapter(requireContext(), mainList);
        binding.main.setAdapter(adapter);
    }


}