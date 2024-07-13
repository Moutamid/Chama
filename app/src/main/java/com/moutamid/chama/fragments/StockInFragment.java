package com.moutamid.chama.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.FragmentStockInBinding;
import com.moutamid.chama.databinding.StockTableLayoutBinding;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.models.StockModel;
import com.moutamid.chama.utilis.Constants;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class StockInFragment extends Fragment {
    FragmentStockInBinding binding;
    StockTableLayoutBinding stockTableLayoutBinding;
    private static final String TAG = "StockInFragment";

    public StockInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStockInBinding.inflate(getLayoutInflater(), container, false);

        binding.main.removeAllViews();
        Constants.databaseReference().child(Constants.STOCK)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Stock> mainList = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ArrayList<StockModel> subList = new ArrayList<>();
                                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                    StockModel model = dataSnapshot2.getValue(StockModel.class);
                                    subList.add(model);
                                }
                                mainList.add(new Stock(dataSnapshot.getKey(), subList));
                            }
                        }
                        updateTable(mainList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }

    private void updateTable(ArrayList<Stock> mainList) {
        for (Stock model : mainList) {
            stockTableLayoutBinding = StockTableLayoutBinding.inflate(getLayoutInflater());
            stockTableLayoutBinding.tableView.removeAllViews();
            stockTableLayoutBinding.name.setText(model.name);
            TableLayout tableLayout = new TableLayout(requireContext());
            tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            tableLayout.removeAllViews();

            TableRow header = new TableRow(requireContext());
            header.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            String[] columns = new String[]{"Date", model.list.get(0).measuring_unit, "Buying Price", "Quantities"};
            for (String col : columns) {
                View view = getLayoutInflater().inflate(R.layout.table_text, null, false);
                TextView textView = view.findViewById(R.id.text);
                textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.text_header_bg, null));
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setText(col);
                header.addView(view);
            }
            tableLayout.addView(header);
            double total = 0;
            for (int i = 0; i < model.list.size(); i++) {
                StockModel s = model.list.get(i);
                TableRow tableRow = new TableRow(requireContext());
                tableRow.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                View dateView = getLayoutInflater().inflate(R.layout.table_text, null, false);
                View unitView = getLayoutInflater().inflate(R.layout.table_text, null, false);
                View priceView = getLayoutInflater().inflate(R.layout.table_text, null, false);
                View quantityView = getLayoutInflater().inflate(R.layout.table_text, null, false);

                TextView date = dateView.findViewById(R.id.text);
                TextView unit = unitView.findViewById(R.id.text);
                TextView price = priceView.findViewById(R.id.text);
                TextView quantity = quantityView.findViewById(R.id.text);

                date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(s.date));
                price.setText("$" + String.format(Locale.getDefault(), "%,.2f", s.buying_price));
                unit.setText(String.valueOf(s.unit));
                quantity.setText(String.valueOf(s.quantity));

                total += s.quantity;

                tableRow.addView(dateView);
                tableRow.addView(unitView);
                tableRow.addView(priceView);
                tableRow.addView(quantityView);

                tableLayout.addView(tableRow);
            }

            TableRow soldRow = new TableRow(requireContext());
            soldRow.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            View dateViewS = getLayoutInflater().inflate(R.layout.table_text, null, false);
            View unitViewS = getLayoutInflater().inflate(R.layout.table_text, null, false);
            View priceViewS = getLayoutInflater().inflate(R.layout.table_text, null, false);
            View quantityViewS = getLayoutInflater().inflate(R.layout.table_text, null, false);

            TextView dateS = dateViewS.findViewById(R.id.text);
            TextView unitS = unitViewS.findViewById(R.id.text);
            TextView priceS = priceViewS.findViewById(R.id.text);
            TextView quantityS = quantityViewS.findViewById(R.id.text);

            dateS.setText("Sold");
            unitS.setText("-");
            priceS.setText("-");
            quantityS.setText("N/A");

            soldRow.addView(dateViewS);
            soldRow.addView(unitViewS);
            soldRow.addView(priceViewS);
            soldRow.addView(quantityViewS);

            tableLayout.addView(soldRow);


            TableRow totalRow = new TableRow(requireContext());
            totalRow.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            View dateViewT = getLayoutInflater().inflate(R.layout.table_text, null, false);
            View unitViewT = getLayoutInflater().inflate(R.layout.table_text, null, false);
            View priceViewT = getLayoutInflater().inflate(R.layout.table_text, null, false);
            View quantityViewT = getLayoutInflater().inflate(R.layout.table_text, null, false);

            TextView dateT = dateViewT.findViewById(R.id.text);
            TextView unitT = unitViewT.findViewById(R.id.text);
            TextView priceT = priceViewT.findViewById(R.id.text);
            TextView quantityT = quantityViewT.findViewById(R.id.text);

            dateT.setText("Total Available");
            priceT.setText("-");
            unitT.setText("-");
            quantityT.setText(String.format(Locale.getDefault(), "%,.2f", total));

            totalRow.addView(dateViewT);
            totalRow.addView(unitViewT);
            totalRow.addView(priceViewT);
            totalRow.addView(quantityViewT);

            tableLayout.addView(totalRow);

            stockTableLayoutBinding.tableView.addView(tableLayout);
            binding.main.addView(stockTableLayoutBinding.getRoot());
        }
    }

    public static class Stock {
        public String name;
        public ArrayList<StockModel> list;
        public Stock(String name, ArrayList<StockModel> list) {
            this.name = name;
            this.list = list;
        }
    }

}