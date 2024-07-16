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
import com.moutamid.chama.databinding.ActivitySubBinding;
import com.moutamid.chama.models.ExpenseModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;

public class SubActivity extends AppCompatActivity {
    ActivitySubBinding binding;
    ArrayList<ExpenseModel> list;
    double spent, balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Add Expense");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        list = new ArrayList<>();
        list = Stash.getArrayList(Constants.HISTORY, ExpenseModel.class);

        spent = Double.parseDouble(Stash.getString(Constants.SPENT, "0"));
        balance = Double.parseDouble(Stash.getString(Constants.TOTAL, "0"));
        Log.d("INCOME", "income " +  spent);

        binding.addSpent.setOnClickListener(v -> {
            if (binding.amount.getEditText().getText().toString().isEmpty()){
                Toast.makeText(this, "Add Amount", Toast.LENGTH_SHORT).show();
            } else {
                Double amount = Double.parseDouble(binding.amount.getEditText().getText().toString());

                list.add(new ExpenseModel(binding.desc.getEditText().getText().toString(), amount, true));
                balance = balance - amount;
                amount = amount + spent;

                Stash.put(Constants.HISTORY, list);
                Stash.put(Constants.SPENT, ""+amount);
                Stash.put(Constants.TOTAL, ""+balance);

                Toast.makeText(this, "Spent Added", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });


    }
}