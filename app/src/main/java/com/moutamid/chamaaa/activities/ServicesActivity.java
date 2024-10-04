package com.moutamid.chamaaa.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.databinding.ActivityServicesBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ServicesActivity extends AppCompatActivity {
    ActivityServicesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Services");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.left.setOnClickListener(v -> binding.scrollbar.smoothScrollBy(-100, 0));
        binding.right.setOnClickListener(v -> binding.scrollbar.smoothScrollBy(100, 0));

        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < getRows().size(); i++) {
            String s = getRows().get(i);
            s = s.replace(", ", ",");
            String[] columns = s.split(",");
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            int size = 0;
            for (String col : columns) {
                size++;
                View view = getLayoutInflater().inflate(R.layout.table_text, null, false);
                TextView textView = view.findViewById(R.id.text);
                if (size == columns.length && i != 0) {
                    col = "$" + String.format(Locale.getDefault(), "%,.2f", Double.parseDouble(col));
                }
                if (i == 0) {
                    textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.text_header_bg, null));
                    textView.setTextColor(getColor(R.color.white));
                }
                textView.setText(col);
                tableRow.addView(view);
            }
            tableLayout.addView(tableRow);
            Log.d(TAG, "updateViews: tableLayout");
        }
        binding.tableView.addView(tableLayout);

    }

    private static final String TAG = "ServicesActivity";

    private ArrayList<String> getRows() {
        String[] serviceData = {
                "Date,Type,Service Offered,Amount",
                "2024-05-20,Contract,Web Development,12000.00",
                "2024-04-15,Hourly,Graphic Design,50.00",
                "2024-03-01,Retainer,Social Media Management,1500.00",
                "2024-02-10,Project,Mobile App Development,25000.00",
                "2024-01-12,One-Time,Content Writing,750.00",
                "2023-12-08,Hourly,Video Editing,75.64",
                "2023-11-15,Contract,SEO Optimization,8000.00",
                "2023-10-20,Retainer,Marketing Consulting,3000.00",
                "2023-09-01,Project,E-Commerce Website Design,18000.00"
        };
        return new ArrayList<>(Arrays.asList(serviceData));
    }
}