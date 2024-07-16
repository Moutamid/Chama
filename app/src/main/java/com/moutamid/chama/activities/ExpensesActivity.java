package com.moutamid.chama.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.adapters.HistorySpent;
import com.moutamid.chama.databinding.ActivityExpensesBinding;
import com.moutamid.chama.models.ExpenseModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class ExpensesActivity extends AppCompatActivity {
    ActivityExpensesBinding binding;
    double balance, income, spent;
    ArrayList<ExpenseModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Expense Manager");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        balance = Double.parseDouble(Stash.getString(Constants.TOTAL, "0"));
        income = Double.parseDouble(Stash.getString(Constants.INCOME, "0"));
        spent = Double.parseDouble(Stash.getString(Constants.SPENT, "0"));

        list = new ArrayList<>();
        list = Stash.getArrayList(Constants.HISTORY, ExpenseModel.class);
        Collections.reverse(list);
        binding.recyler.setLayoutManager(new LinearLayoutManager(this));
        binding.recyler.setHasFixedSize(false);

        HistorySpent adapter = new HistorySpent(this, list);
        binding.recyler.setAdapter(adapter);

        binding.balance.setText("$" + String.format("%,.2f", balance));
        binding.income.setText("$" + String.format("%,.2f", income));
        binding.spent.setText("$" + String.format("%,.2f", spent));

        binding.addSpent.setOnClickListener(v -> {
            startActivity(new Intent(this, SubActivity.class));
        });
        binding.addIncome.setOnClickListener(v -> {
            startActivity(new Intent(this, AddActivity.class));
        });

    }
}