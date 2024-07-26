package com.moutamid.chama.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.models.StockModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class StockInFragment extends Fragment {
    FragmentStockInBinding binding;
    StockTableLayoutBinding stockTableLayoutBinding;
    private static final String TAG = "StockInFragment";
    ArrayList<Stock> stock_out = new ArrayList<>();
    ArrayList<ProductModel> list = new ArrayList<>();
    ArrayList<ChatModel> groups;
    public StockInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStockInBinding.inflate(getLayoutInflater(), container, false);

        ChatModel chatModel = (ChatModel) Stash.getObject(Constants.PRODUCT_REFERENCE, ChatModel.class);
        Constants.showDialog();
        if (chatModel != null) {
            binding.layout.setVisibility(View.GONE);
            showData(chatModel);
        } else {
            binding.layout.setVisibility(View.VISIBLE);
            manualData();
        }

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
        Constants.databaseReference().child(Constants.STOCK_OUT).child(chatModel.id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ArrayList<StockModel> subList = new ArrayList<>();
                                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                    StockModel model = dataSnapshot2.getValue(StockModel.class);
                                    subList.add(model);
                                }
                                stock_out.add(new Stock(dataSnapshot.getKey(), subList));
                            }
                        }
                        Constants.databaseReference().child(Constants.PRODUCTS).child(chatModel.id).get()
                                .addOnSuccessListener(dataSnapshot -> {
                                    if (dataSnapshot.exists()) {
                                        list.clear();
                                        for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                            ProductModel productModel = snapshot2.getValue(ProductModel.class);
                                            list.add(productModel);
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
                                    }
                                    getStockList(chatModel);
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getStockList(ChatModel chatModel) {
        Constants.databaseReference().child(Constants.STOCK).child(chatModel.id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Constants.dismissDialog();
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
                        if (isAdded()) updateTable(mainList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Constants.dismissDialog();
                        Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTable(ArrayList<Stock> mainList) {
        binding.main.removeAllViews();
        for (Stock model : mainList) {
            stockTableLayoutBinding = StockTableLayoutBinding.inflate(getLayoutInflater());
            stockTableLayoutBinding.tableView.removeAllViews();
            stockTableLayoutBinding.name.setText(model.name);
            TableLayout tableLayout = new TableLayout(requireContext());
            tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1));
            tableLayout.removeAllViews();

            TableRow header = new TableRow(requireContext());
            header.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            String[] columns = new String[]{"Date", "Unit", "Buying Price", "Quantities"};
            for (String col : columns) {
                View view = getLayoutInflater().inflate(R.layout.table_text, null, false);
                TextView textView = view.findViewById(R.id.text);
                textView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.text_header_bg, null));
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setText(col);
                header.addView(view);
            }
            tableLayout.addView(header);
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
                unit.setText(s.unit +" "+ s.measuring_unit);
                quantity.setText(String.valueOf(s.quantity));

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
            int sold = 0;
            Stock stock = stock_out.stream().filter(stock1 -> Objects.equals(stock1.name, model.name)).findFirst().orElse(null);
            if (stock != null) {
             sold = (int) stock.list.stream()
                        .mapToDouble(stockModel -> stockModel.quantity)
                        .sum();
            }
            String s = sold == 0 ? "N/A" : String.valueOf(sold);
            quantityS.setText(s);

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

            double available = 0;
            ProductModel productModel = list.stream().filter(product -> Objects.equals(product.name, model.name)).findFirst().orElse(null);
            if (productModel != null) {
                available = productModel.available_stock;
            }
            quantityT.setText(String.format(Locale.getDefault(), "%,.2f", available));

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