package com.moutamid.chama.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.moutamid.chama.adapters.HistorySpent;
import com.moutamid.chama.databinding.ActivityExpensesBinding;
import com.moutamid.chama.models.ExpenseModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExpensesActivity extends AppCompatActivity {
    ActivityExpensesBinding binding;
    ArrayList<ExpenseModel> list;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyler.setLayoutManager(new LinearLayoutManager(this));
        binding.recyler.setHasFixedSize(false);

        String ID = getIntent().getStringExtra("ADMIN");
        id = ID != null ? ID : Constants.auth().getCurrentUser().getUid();

        binding.toolbar.name.setText("Expense Manager");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.addSpent.setOnClickListener(v -> {
            startActivity(new Intent(this, SubActivity.class).putExtra("ADMIN", id));
        });
        binding.addIncome.setOnClickListener(v -> {
            startActivity(new Intent(this, AddActivity.class).putExtra("ADMIN", id));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        list = new ArrayList<>();
        Constants.showDialog();
        Constants.databaseReference().child(Constants.EXPENSES).child(id)
                .get().addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ExpenseModel model = snapshot.getValue(ExpenseModel.class);
                            list.add(model);
                        }
                        list.sort(Comparator.comparingLong(value -> value.timestamp));
                        Collections.reverse(list);
                        HistorySpent adapter = new HistorySpent(this, list);
                        binding.recyler.setAdapter(adapter);
                        Totals totals = calculate(list);
                        binding.balance.setText("$" + String.format("%,.2f", totals.total));
                        binding.income.setText("$" + String.format("%,.2f", totals.totalIncome));
                        binding.spent.setText("$" + String.format("%,.2f", totals.totalExpense));
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Totals calculate(ArrayList<ExpenseModel> list) {
        double totalExpense = 0.0;
        double totalIncome = 0.0;
        for (ExpenseModel item : list) {
            if (item.isExpense) {
                totalExpense += item.price;
            } else {
                totalIncome += item.price;
            }
        }
        double total = totalIncome - totalExpense;
        return new Totals(total, totalExpense, totalIncome);
    }

    public static class Totals {
        double total;
        double totalExpense;
        double totalIncome;
        public Totals(double total, double totalExpense, double totalIncome) {
            this.total = total;
            this.totalExpense = totalExpense;
            this.totalIncome = totalIncome;
        }
    }

}