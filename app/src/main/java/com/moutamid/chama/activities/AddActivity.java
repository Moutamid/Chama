package com.moutamid.chama.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityAddBinding;
import com.moutamid.chama.models.ExpenseModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
    ActivityAddBinding binding;
    ArrayList<ExpenseModel> list;
    double income, balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Add Income");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        list = new ArrayList<>();
        list = Stash.getArrayList(Constants.HISTORY, ExpenseModel.class);

        income = Double.parseDouble(Stash.getString(Constants.INCOME, "0"));
        balance = Double.parseDouble(Stash.getString(Constants.TOTAL, "0"));
        Log.d("INCOME", "income " +  income);

        binding.addIncome.setOnClickListener(v -> {
            if (binding.amount.getEditText().getText().toString().isEmpty()){
                Toast.makeText(this, "Add income", Toast.LENGTH_SHORT).show();
            } else {
                double amount = Double.parseDouble(binding.amount.getEditText().getText().toString());
                list.add(new ExpenseModel(binding.desc.getEditText().getText().toString(), amount, false));
                balance = balance + amount;
                amount = amount + income;

                Stash.put(Constants.HISTORY, list);
                Stash.put(Constants.INCOME, ""+amount);
                Stash.put(Constants.TOTAL, ""+balance);

                Toast.makeText(this, "Income Added", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

    }
}