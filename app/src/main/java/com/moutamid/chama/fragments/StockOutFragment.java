package com.moutamid.chama.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.moutamid.chama.R;
import com.moutamid.chama.databinding.FragmentStockOutBinding;
import com.moutamid.chama.databinding.StockTableLayoutBinding;
import com.moutamid.chama.models.StockModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class StockOutFragment extends Fragment {

    FragmentStockOutBinding binding;

    StockTableLayoutBinding stockTableLayoutBinding;

    public StockOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStockOutBinding.inflate(getLayoutInflater(), container, false);

        binding.main.removeAllViews();
//        for (StockModel model : list) {
//            stockTableLayoutBinding = StockTableLayoutBinding.inflate(getLayoutInflater());
//            stockTableLayoutBinding.tableView.removeAllViews();
//            stockTableLayoutBinding.name.setText(model.name);
//            TableLayout tableLayout = new TableLayout(requireContext());
//            tableLayout.setLayoutParams(new TableLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT));
//            tableLayout.removeAllViews();
//            for (int i = 0; i < model.data.size(); i++) {
//                String s = model.data.get(i);
//                s = s.replace(", ", ",");
//                String[] columns = s.split(",");
//                TableRow tableRow = new TableRow(requireContext());
//                tableRow.setLayoutParams(new TableRow.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//                int size = 0;
//                for (String col : columns) {
//                    size++;
//                    View view = getLayoutInflater().inflate(R.layout.table_text, null, false);
//                    TextView textView = view.findViewById(R.id.text);
//                    if (size == columns.length - 1 && i != 0) {
//                        try {
//                            col = "$" + String.format(Locale.getDefault(), "%,.2f", Double.parseDouble(col));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (i == 0) {
//                        textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.text_header_bg, null));
//                        textView.setTextColor(getResources().getColor(R.color.white));
//                    }
//                    textView.setText(col);
//                    tableRow.addView(view);
//                }
//                tableLayout.addView(tableRow);
//            }
//            stockTableLayoutBinding.tableView.addView(tableLayout);
//            binding.main.addView(stockTableLayoutBinding.getRoot());
//        }

        return binding.getRoot();
    }

}