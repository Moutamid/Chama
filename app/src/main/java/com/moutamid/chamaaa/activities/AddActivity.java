package com.moutamid.chamaaa.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.databinding.ActivityAddBinding;
import com.moutamid.chamaaa.models.ExpenseModel;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    ActivityAddBinding binding;
    ArrayList<ExpenseModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Add Income");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        list = new ArrayList<>();
        String ID = getIntent().getStringExtra("ADMIN");
        binding.addIncome.setOnClickListener(v -> {
            if (binding.amount.getEditText().getText().toString().isEmpty()){
                Toast.makeText(this, "Add income", Toast.LENGTH_SHORT).show();
            } else {
                double amount = Double.parseDouble(binding.amount.getEditText().getText().toString());
                ExpenseModel model = new ExpenseModel();
                model.timestamp = new Date().getTime();
                model.isExpense = false;
                model.name = binding.desc.getEditText().getText().toString();
                model.price = amount;
                Constants.databaseReference().child(Constants.EXPENSES).child(ID)
                        .push().setValue(model).addOnFailureListener(e -> {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Income Added", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        });
            }
        });

    }
}