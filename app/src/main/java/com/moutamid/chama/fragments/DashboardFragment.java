package com.moutamid.chama.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.chama.R;
import com.moutamid.chama.activities.ProductListActivity;
import com.moutamid.chama.adapters.ProductHomeAdapter;
import com.moutamid.chama.adapters.TimelineAdapter;
import com.moutamid.chama.bottomsheets.BuyProduct;
import com.moutamid.chama.bottomsheets.DateFilter;
import com.moutamid.chama.databinding.FragmentDashboardBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.models.StockModel;
import com.moutamid.chama.models.TimelineModel;
import com.moutamid.chama.models.TransactionModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.models.VoteModel;
import com.moutamid.chama.utilis.Constants;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;


public class DashboardFragment extends Fragment {
    FragmentDashboardBinding binding;
    private LineChart lineChart;
    private BarChart chart;
    ArrayList<ChatModel> list;
    ArrayList<VoteModel> votes;
    Context mContext;
    Dialog dialog;
    LinearLayout selectedLayout;
    TextView selectedText;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(getLayoutInflater(), container, false);

        setupLineChart();
        setUpProducts();
        setupTimeline();

        selectedLayout = binding.allListing;
        selectedText = binding.allListingText;

        binding.allListing.setOnClickListener(v -> {
            selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_circle));
            selectedText.setTextColor(getResources().getColor(R.color.black));

            binding.allListing.setBackground(getResources().getDrawable(R.drawable.round_circle_blue));
            binding.allListingText.setTextColor(getResources().getColor(R.color.blue));

            selectedLayout = binding.allListing;
            selectedText = binding.allListingText;

            showAllListing();
        });

        binding.sales.setOnClickListener(v -> {
            selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_circle));
            selectedText.setTextColor(getResources().getColor(R.color.black));

            binding.sales.setBackground(getResources().getDrawable(R.drawable.round_circle_red));
            binding.saleText.setTextColor(getResources().getColor(R.color.red));

            selectedLayout = binding.sales;
            selectedText = binding.saleText;
            showSales();
        });


        binding.cash.setOnClickListener(v -> {
            selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_circle));
            selectedText.setTextColor(getResources().getColor(R.color.black));

            binding.cash.setBackground(getResources().getDrawable(R.drawable.round_circle_yellow));
            binding.cashText.setTextColor(getResources().getColor(R.color.yellow));

            selectedLayout = binding.cash;
            selectedText = binding.cashText;

            showCash();
        });

        binding.stock.setOnClickListener(v -> {
            selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_circle));
            selectedText.setTextColor(getResources().getColor(R.color.black));

            binding.stock.setBackground(getResources().getDrawable(R.drawable.round_circle_purple));
            binding.stockText.setTextColor(getResources().getColor(R.color.purple));

            selectedLayout = binding.stock;
            selectedText = binding.stockText;

            showStock();
        });


        binding.dateFilterReports.setOnClickListener(v -> {
            DateFilter dateFilter = new DateFilter(Constants.REPORT);
            dateFilter.show(getChildFragmentManager(), dateFilter.getTag());
        });

        binding.dateFilterSavings.setOnClickListener(v -> {
            DateFilter dateFilter = new DateFilter(Constants.SAVING);
            dateFilter.show(getChildFragmentManager(), dateFilter.getTag());
        });

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();

        getMessages();

        getTransactions();

        return binding.getRoot();
    }

    private void showStock() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0,0));
        final float[] xIndex = {1};
        for (int i = 0; i < groups.size(); i++) {
            ChatModel chatModel = groups.get(i);
            int finalI = i;
            Constants.databaseReference().child(Constants.STOCK).child(chatModel.id)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stock = snapshot2.getValue(StockModel.class);
                                    if (stock != null) {
                                        float value = (float) stock.unit_price;
                                        entries.add(new Entry(xIndex[0], value));
                                        xIndex[0]++;
                                    }
                                }
                            }
                        }

                        if (finalI == groups.size() - 1) {
                            LineDataSet dataSet = new LineDataSet(entries, "");
                            dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                            dataSet.setColor(getResources().getColor(R.color.purple));
                            dataSet.setDrawCircles(false);
                            dataSet.setDrawValues(false);
                            dataSet.setCircleColor(getResources().getColor(R.color.purple));
                            dataSet.setLineWidth(2f);
                            dataSet.setCircleRadius(3f);
                            dataSet.setFillAlpha(65);
                            dataSet.setFillColor(getResources().getColor(R.color.purple));
                            dataSet.setDrawCircleHole(false);
                            dataSet.setHighLightColor(Color.rgb(244, 117, 117));

                            LineData lineData = new LineData(dataSet);
                            lineData.setValueTextColor(Color.BLACK);
                            lineData.setValueTextSize(9f);
                            lineChart.setData(lineData);
                            lineChart.invalidate();
                        }

                    });
        }
    }

    private void showCash() {
        final float[] xIndex = {1};
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0,0));
        for (int i = 0; i < groups.size(); i++) {
            ChatModel chatModel = groups.get(i);
            int finalI = i;
            Constants.databaseReference().child(Constants.CASH_SALE).child(chatModel.id)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stock = snapshot2.getValue(StockModel.class);
                                    if (stock != null) {
                                        // Create entries for each stock
                                        float value = (float) stock.unit_price;
                                        Log.d(TAG, "showCash: " + value);
                                        Log.d(TAG, "showCash: " + xIndex[0]);
                                        entries.add(new Entry(xIndex[0], value));
                                        xIndex[0]++;
                                    }
                                }
                            }
                        }
                        if (finalI == groups.size() -1){
                            LineDataSet dataSet = new LineDataSet(entries, "");
                            dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                            dataSet.setColor(getResources().getColor(R.color.yellow));
                            dataSet.setDrawCircles(false);
                            dataSet.setDrawValues(false);
                            dataSet.setCircleColor(getResources().getColor(R.color.yellow));
                            dataSet.setLineWidth(2f);
                            dataSet.setCircleRadius(3f);
                            dataSet.setFillAlpha(65);
                            dataSet.setFillColor(getResources().getColor(R.color.yellow));
                            dataSet.setDrawCircleHole(false);
                            dataSet.setHighLightColor(Color.rgb(244, 117, 117));

                            LineData lineData = new LineData(dataSet);
                            lineData.setValueTextColor(Color.BLACK);
                            lineData.setValueTextSize(9f);
                            lineChart.setData(lineData);
                            lineChart.invalidate();
                        }
                    });
        }

    }

    private void showSales() {
        List<Entry> sale = new ArrayList<>();
        sale.add(new Entry(0,0));
        final float[] xIndex = {1};
        for (int i = 0; i < groups.size(); i++) {
            ChatModel chatModel = groups.get(i);
            int finalI = i;
            Constants.databaseReference().child(Constants.CREDIT_SALE).child(chatModel.id)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stock = snapshot2.getValue(StockModel.class);
                                    float value = (float) stock.unit_price;
                                    sale.add(new Entry(xIndex[0], value));
                                    xIndex[0]++;
                                }
                            }
                        }

                        if (finalI == groups.size() - 1) {
                            LineDataSet dataSet = new LineDataSet(sale, "");
                            dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                            dataSet.setColor(getResources().getColor(R.color.red));
                            dataSet.setDrawCircles(false);
                            dataSet.setDrawValues(false);
                            dataSet.setCircleColor(getResources().getColor(R.color.red));
                            dataSet.setLineWidth(2f);
                            dataSet.setCircleRadius(3f);
                            dataSet.setFillAlpha(65);
                            dataSet.setFillColor(getResources().getColor(R.color.red));
                            dataSet.setDrawCircleHole(false);
                            dataSet.setHighLightColor(Color.rgb(244, 117, 117));

                            LineData lineData = new LineData(dataSet);
                            lineData.setValueTextColor(Color.BLACK);
                            lineData.setValueTextSize(9f);

                            lineChart.setData(lineData);
                            lineChart.invalidate();
                        }

                    });
        }
    }

    List<Entry> sale;
    List<Entry> cash;
    List<Entry> stock;
    LineData lineData;

    private void showAll() {
        List<Entry> allStockEntries = new ArrayList<>();
        List<Entry> allCashEntries = new ArrayList<>();
        List<Entry> allSalesEntries = new ArrayList<>();

        for (ChatModel chatModel : groups) {
            // Stock data
            Constants.databaseReference().child(Constants.STOCK).child(chatModel.id)
                    .get().addOnSuccessListener(stockSnapshot -> {
                        if (stockSnapshot.exists()) {
                            float xIndex = 0;
                            for (DataSnapshot snapshot : stockSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stock = snapshot2.getValue(StockModel.class);
                                    if (stock != null) {
                                        float value = (float) stock.unit_price;
                                        allStockEntries.add(new Entry(xIndex, value));
                                        xIndex++;
                                    }
                                }
                            }
                        }
                    });

            // Cash data
            Constants.databaseReference().child(Constants.CASH_SALE).child(chatModel.id)
                    .get().addOnSuccessListener(cashSnapshot -> {
                        if (cashSnapshot.exists()) {
                            float xIndex = 0;
                            for (DataSnapshot snapshot : cashSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stock = snapshot2.getValue(StockModel.class);
                                    if (stock != null) {
                                        float value = (float) stock.unit_price;
                                        allCashEntries.add(new Entry(xIndex, value));
                                        xIndex++;
                                    }
                                }
                            }
                        }
                    });

            // Sales data
            Constants.databaseReference().child(Constants.CREDIT_SALE).child(chatModel.id)
                    .get().addOnSuccessListener(salesSnapshot -> {
                        if (salesSnapshot.exists()) {
                            float xIndex = 0;
                            for (DataSnapshot snapshot : salesSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stock = snapshot2.getValue(StockModel.class);
                                    if (stock != null) {
                                        float value = (float) stock.unit_price;
                                        allSalesEntries.add(new Entry(xIndex, value));
                                        xIndex++;
                                    }
                                }
                            }
                        }
                    });
        }

        // Create LineDataSets for each data type
        LineDataSet stockDataSet = new LineDataSet(allStockEntries, "Stock");
        stockDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        stockDataSet.setColor(getResources().getColor(R.color.purple));
        stockDataSet.setDrawCircles(false);
        stockDataSet.setDrawValues(false);
        stockDataSet.setCircleColor(getResources().getColor(R.color.purple));
        stockDataSet.setLineWidth(2f);
        stockDataSet.setCircleRadius(3f);
        stockDataSet.setFillAlpha(65);
        stockDataSet.setFillColor(getResources().getColor(R.color.purple));
        stockDataSet.setDrawCircleHole(false);
        stockDataSet.setHighLightColor(Color.rgb(244, 117, 117));

        LineDataSet cashDataSet = new LineDataSet(allCashEntries, "Cash");
        cashDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        cashDataSet.setColor(getResources().getColor(R.color.yellow));
        cashDataSet.setDrawCircles(false);
        cashDataSet.setDrawValues(false);
        cashDataSet.setCircleColor(getResources().getColor(R.color.yellow));
        cashDataSet.setLineWidth(2f);
        cashDataSet.setCircleRadius(3f);
        cashDataSet.setFillAlpha(65);
        cashDataSet.setFillColor(getResources().getColor(R.color.yellow));
        cashDataSet.setDrawCircleHole(false);
        cashDataSet.setHighLightColor(Color.rgb(244, 117, 117));

        LineDataSet salesDataSet = new LineDataSet(allSalesEntries, "Sales");
        salesDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        salesDataSet.setColor(getResources().getColor(R.color.red));
        salesDataSet.setDrawCircles(false);
        salesDataSet.setDrawValues(false);
        salesDataSet.setCircleColor(getResources().getColor(R.color.red));
        salesDataSet.setLineWidth(2f);
        salesDataSet.setCircleRadius(3f);
        salesDataSet.setFillAlpha(65);
        salesDataSet.setFillColor(getResources().getColor(R.color.red));
        salesDataSet.setDrawCircleHole(false);
        salesDataSet.setHighLightColor(Color.rgb(244, 117, 117));

        LineData lineData = new LineData(stockDataSet);
        lineData.addDataSet(cashDataSet);
        lineData.addDataSet(salesDataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void showAllListing() {
        lineData = new LineData();
        sale = new ArrayList<>();
        cash = new ArrayList<>();
        stock = new ArrayList<>();
        Log.d(TAG, "showAllListing: groups " + groups.size());
        sale.add(new Entry(0,0));
        cash.add(new Entry(0,0));
        stock.add(new Entry(0,0));
        final float[] xIndex = {1};
        for (int i = 0; i < groups.size(); i++) {
            ChatModel chatModel = groups.get(i);
            int finalI = i;
            Constants.databaseReference().child(Constants.CREDIT_SALE).child(chatModel.id)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            Log.d(TAG, "showAllListing: finalI " + finalI);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stockModel = snapshot2.getValue(StockModel.class);
                                    float value = (float) stockModel.unit_price;
                                    Log.d(TAG, "showAllListing: value " + value);
                                    sale.add(new Entry(xIndex[0], value));
                                    xIndex[0]++;
                                }
                            }
                            Log.d(TAG, "showAllListing: sale size " + sale.size());
                        }

                        if (finalI == groups.size() - 1) {
                            LineDataSet saleSet = new LineDataSet(sale, "Sale");
                            saleSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                            saleSet.setColor(getResources().getColor(R.color.red));
                            saleSet.setDrawCircles(false);
                            saleSet.setDrawValues(false);
                            saleSet.setCircleColor(getResources().getColor(R.color.red));
                            saleSet.setLineWidth(2f);
                            saleSet.setCircleRadius(3f);
                            saleSet.setFillAlpha(65);
                            saleSet.setFillColor(getResources().getColor(R.color.red));
                            saleSet.setDrawCircleHole(false);
                            saleSet.setHighLightColor(Color.rgb(244, 117, 117));
                            lineData.addDataSet(saleSet);
                            Log.d(TAG, "showAllListing: line data size " + lineData.getDataSets().size());
                            getCashData();
//                            lineChart.setData(lineData);
//                            lineChart.invalidate();
                        }

                    });
        }

    }

    private void getCashData() {
        final float[] xIndex = {1};
        for (int i = 0; i < groups.size(); i++) {
            ChatModel chatModel = groups.get(i);
            int finalI = i;
            Constants.databaseReference().child(Constants.CASH_SALE).child(chatModel.id)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stockModel = snapshot2.getValue(StockModel.class);
                                    float value = (float) stockModel.unit_price;
                                    Log.d(TAG, "getCashData:  csh value " + value);
                                    cash.add(new Entry(xIndex[0], value));
                                    xIndex[0]++;
                                }
                            }
                        }
                        if (finalI == groups.size() - 1) {
                            Log.d(TAG, "showAllListing IF CASH");
                            LineDataSet cashSet = new LineDataSet(cash, "Cash");
                            cashSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                            cashSet.setColor(getResources().getColor(R.color.yellow));
                            cashSet.setDrawCircles(false);
                            cashSet.setDrawValues(false);
                            cashSet.setCircleColor(getResources().getColor(R.color.yellow));
                            cashSet.setLineWidth(2f);
                            cashSet.setCircleRadius(3f);
                            cashSet.setFillAlpha(65);
                            cashSet.setFillColor(getResources().getColor(R.color.yellow));
                            cashSet.setDrawCircleHole(false);
                            cashSet.setHighLightColor(Color.rgb(244, 117, 117));
                            lineData.addDataSet(cashSet);
                            Log.d(TAG, "showAllListing: line data size " + lineData.getDataSets().size());
                             getStockData();

//                            lineChart.setData(lineData);
//                            lineChart.invalidate();

                        }
                    });
        }
    }

    private void getStockData() {
        final float[] xIndex = {1};
        for (int i = 0; i < groups.size(); i++) {
            ChatModel chatModel = groups.get(i);
            int finalI = i;
            Constants.databaseReference().child(Constants.STOCK).child(chatModel.id)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                    StockModel stockModel = snapshot2.getValue(StockModel.class);
                                    if (stockModel != null) {
                                        float value = (float) stockModel.unit_price;
                                        stock.add(new Entry(xIndex[0], value));
                                        xIndex[0]++;
                                    }
                                }
                            }
                        }
                        if (finalI == groups.size() - 1) {
                            Log.d(TAG, "getStockData: SET");
                            LineDataSet stockSet = new LineDataSet(stock, "Stock");
                            stockSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
                            stockSet.setColor(getResources().getColor(R.color.purple));
                            stockSet.setDrawCircles(false);
                            stockSet.setDrawValues(false);
                            stockSet.setCircleColor(getResources().getColor(R.color.purple));
                            stockSet.setLineWidth(2f);
                            stockSet.setCircleRadius(3f);
                            stockSet.setFillAlpha(65);
                            stockSet.setFillColor(getResources().getColor(R.color.purple));
                            stockSet.setDrawCircleHole(false);
                            stockSet.setHighLightColor(Color.rgb(244, 117, 117));

                            lineData.addDataSet(stockSet);

                            lineChart.setData(lineData);
                            lineChart.invalidate();
                        }
                    });
        }
    }

    ArrayList<ChatModel> groups = new ArrayList<>();
    private void setUpProducts() {
        binding.products.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        binding.products.setHasFixedSize(false);
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
                        showAllListing();
                        getGroupsProducts(groups);
                    }
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.viewAll.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ProductListActivity.class));
        });

    }

    private void getGroupsProducts(ArrayList<ChatModel> groups) {
        ArrayList<ProductModel> list = new ArrayList<>();
        for (ChatModel group : groups) {
            Constants.databaseReference().child(Constants.PRODUCTS).child(group.id).get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ProductModel productModel = snapshot.getValue(ProductModel.class);
                                list.add(productModel);
                            }
                        }
                        ProductHomeAdapter adapter = new ProductHomeAdapter(mContext, list, model -> {
                            BuyProduct fragment = new BuyProduct(model, null);
                            fragment.show(getChildFragmentManager(), fragment.getTag());
                        });
                        binding.products.setAdapter(adapter);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void getTransactions() {
        Constants.databaseReference().child(Constants.TRANSACTIONS).child(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        List<BarEntry> normalEntries = new ArrayList<>();
                        List<BarEntry> lockedEntries = new ArrayList<>();
                        List<BarEntry> withdrawalEntries = new ArrayList<>();

                        for (int i = 0; i < Constants.months.length; i++) {
                            String month = Constants.months[i];
                            DataSnapshot monthData = dataSnapshot.child(month);
                            float normalSum = 0;
                            float lockedSum = 0;
                            float withdrawalSum = 0;

                            if (monthData.exists()) {
                                for (DataSnapshot data : monthData.getChildren()) {
                                    TransactionModel monthlyData = data.getValue(TransactionModel.class);
                                    if (monthlyData != null) {
                                        if (monthlyData.type.equals(Constants.NORMAL))
                                            normalSum += (float) monthlyData.amount;
                                        if (monthlyData.type.equals(Constants.LOCK))
                                            lockedSum += (float) monthlyData.amount;
                                        if (monthlyData.type.equals(Constants.WITHDRAW))
                                            withdrawalSum += (float) monthlyData.amount;
                                    }
                                }
                            }

                            // Add the aggregated sum to the entries
                            normalEntries.add(new BarEntry(i, normalSum));
                            lockedEntries.add(new BarEntry(i, lockedSum));
                            withdrawalEntries.add(new BarEntry(i, withdrawalSum));
                        }

                        BarDataSet normalDataSet = new BarDataSet(normalEntries, "Normal");
                        normalDataSet.setColor(getResources().getColor(R.color.bar_normal));
                        BarDataSet lockedDataSet = new BarDataSet(lockedEntries, "Locked");
                        lockedDataSet.setColor(getResources().getColor(R.color.bar_locked));
                        BarDataSet withdrawalDataSet = new BarDataSet(withdrawalEntries, "Withdrawal");
                        withdrawalDataSet.setColor(getResources().getColor(R.color.bar_withdrawal));

                        BarData barData = new BarData(normalDataSet, lockedDataSet, withdrawalDataSet);
                        binding.barchart.setData(barData);
                        binding.barchart.invalidate(); // refresh

                        float groupSpace = 0.3f;
                        float barSpace = 0.05f;
                        float barWidth = 0.2f;
                        barData.setBarWidth(barWidth);
                        binding.barchart.groupBars(0, groupSpace, barSpace);
                        binding.barchart.invalidate();

                        binding.barchart.setExtraOffsets(0, 0, 0, 15);

                        // Customizing the Y-axis value formatter
                        binding.barchart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                if (value >= 1000 && value < 1000000) {
                                    return (int) (value / 1000) + "K";
                                } else if (value >= 1000000) {
                                    return (int) (value / 1000000) + "M";
                                } else {
                                    return String.valueOf((int) value);
                                }
                            }
                        });

                        // Setting the X-axis labels
                        ValueFormatter xAxisFormatter = new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return Constants.months[(int) value % Constants.months.length];
                            }
                        };
                        binding.barchart.getXAxis().setValueFormatter(xAxisFormatter);

                        binding.barchart.getDescription().setEnabled(false);
                        binding.barchart.setDrawGridBackground(false);
                        binding.barchart.getAxisLeft().setDrawGridLines(false);
                        binding.barchart.getXAxis().setDrawGridLines(false);
                        binding.barchart.getAxisRight().setDrawGridLines(false);
                        binding.barchart.getAxisRight().setEnabled(false);
                        binding.barchart.getXAxis().setGranularity(1f);
                        binding.barchart.getXAxis().setGranularityEnabled(true);

                        binding.barchart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(Constants.months));
                        binding.barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                        for (IBarDataSet set : binding.barchart.getData().getDataSets())
                            set.setDrawValues(false);

                        binding.barchart.invalidate();
                        Legend legend = binding.barchart.getLegend();
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        legend.setDrawInside(false);
                    }
                });
    }

    private void getVotingResults() {
        votes = new ArrayList<>();
        Log.d(TAG, "getVotingResults: " + list.size());
        for (ChatModel model : list) {
            Constants.databaseReference().child(Constants.MESSAGES).child(model.id)
                    .get().addOnSuccessListener(dataSnapshot -> {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MessageModel messageModel = snapshot.getValue(MessageModel.class);
                            if (messageModel.isPoll && Constants.isSameDay(messageModel.timestamp, new Date().getTime())) {
                                votes.add(new VoteModel(model, messageModel));
                            }
                        }
                        if (votes.isEmpty()) {
                            binding.noPoll.setVisibility(View.VISIBLE);
                        } else {
                            binding.noPoll.setVisibility(View.GONE);
                            votes.sort(Comparator.comparing(voteModel -> voteModel.model.timestamp));
                            Collections.reverse(votes);
                            setViews();
                        }
                    });
        }
    }

    private static final String TAG = "DashboardFragment";

    private void setViews() {
        Log.d(TAG, "setViews: " + votes.size());

        binding.votingResults.removeAllViews();
        for (VoteModel voteModel : votes) {
            View voting_result = getLayoutInflater().inflate(R.layout.voting_result, null, false);
            TextView question = voting_result.findViewById(R.id.question);
            TextView groupName = voting_result.findViewById(R.id.groupName);
            LinearLayout votingOptions = voting_result.findViewById(R.id.votingOptions);

            groupName.setText(voteModel.chatModel.name);
            question.setText(voteModel.model.pollModel.question);
            votingOptions.removeAllViews();
            for (String options : voteModel.model.pollModel.options) {
                View voting_options = getLayoutInflater().inflate(R.layout.voting_options, null, false);
                TextView option = voting_options.findViewById(R.id.option);
                TextView countVote = voting_options.findViewById(R.id.countVote);
                RelativeLayout imagesLayout = voting_options.findViewById(R.id.imagesLayout);
                LinearProgressIndicator progressBar = voting_options.findViewById(R.id.progressBar);

                View voting_images = getLayoutInflater().inflate(R.layout.voting_images, null, false);
                CircleImageView image1 = voting_images.findViewById(R.id.image1);
                CircleImageView image2 = voting_images.findViewById(R.id.image2);
                CircleImageView image3 = voting_images.findViewById(R.id.image3);
                option.setText(options);
                int voteCount = voteModel.model.pollModel.votes.votes.get(options.replace(" ", "_"));
                if (voteCount == 1) {
                    countVote.setVisibility(View.GONE);
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.GONE);
                    image3.setVisibility(View.GONE);
                    Map<String, Object> votersForOption = (Map<String, Object>) voteModel.model.pollModel.votes.voters.get(options.replace(" ", "_"));
                    if (votersForOption != null) {
                        List<String> link = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : votersForOption.entrySet()) {
                            String voterId = entry.getKey();
                            Log.d(TAG, "voterId: " + voterId);
                            for (UserModel userModel : voteModel.chatModel.groupMembers) {
                                if (userModel.id.equals(voterId)) {
                                    link.add(userModel.image);
                                }
                            }
                        }
                        Glide.with(mContext).load(link.get(0)).placeholder(R.drawable.profile_icon).into(image1);
                    }
                } else if (voteCount == 2) {
                    countVote.setVisibility(View.GONE);
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.GONE);

                    Map<String, Object> votersForOption = (Map<String, Object>) voteModel.model.pollModel.votes.voters.get(options.replace(" ", "_"));
                    if (votersForOption != null) {
                        List<String> link = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : votersForOption.entrySet()) {
                            String voterId = entry.getKey();
                            Log.d(TAG, "voterId: " + voterId);
                            for (UserModel userModel : voteModel.chatModel.groupMembers) {
                                if (userModel.id.equals(voterId)) {
                                    link.add(userModel.image);
                                }
                            }
                        }
                        Glide.with(mContext).load(link.get(0)).placeholder(R.drawable.profile_icon).into(image1);
                        Glide.with(mContext).load(link.get(1)).placeholder(R.drawable.profile_icon).into(image2);
                    }

                } else if (voteCount == 3) {
                    countVote.setVisibility(View.GONE);
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.VISIBLE);

                    Map<String, Object> votersForOption = (Map<String, Object>) voteModel.model.pollModel.votes.voters.get(options.replace(" ", "_"));

                    if (votersForOption != null) {
                        List<String> link = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : votersForOption.entrySet()) {
                            String voterId = entry.getKey();
                            Log.d(TAG, "voterId: " + voterId);
                            for (UserModel userModel : voteModel.chatModel.groupMembers) {
                                if (userModel.id.equals(voterId)) {
                                    link.add(userModel.image);
                                }
                            }
                        }
                        Glide.with(mContext).load(link.get(0)).placeholder(R.drawable.profile_icon).into(image1);
                        Glide.with(mContext).load(link.get(1)).placeholder(R.drawable.profile_icon).into(image2);
                        Glide.with(mContext).load(link.get(2)).placeholder(R.drawable.profile_icon).into(image3);
                    }

                } else if (voteCount >= 4) {
                    countVote.setVisibility(View.VISIBLE);
                    image1.setVisibility(View.VISIBLE);
                    image2.setVisibility(View.VISIBLE);
                    image3.setVisibility(View.VISIBLE);
                    countVote.setText("+" + (voteCount - 3) + " more");
                    Map<String, Object> votersForOption = (Map<String, Object>) voteModel.model.pollModel.votes.voters.get(options.replace(" ", "_"));
                    if (votersForOption != null) {
                        List<String> link = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : votersForOption.entrySet()) {
                            String voterId = entry.getKey();
                            Log.d(TAG, "voterId: " + voterId);
                            for (UserModel userModel : voteModel.chatModel.groupMembers) {
                                if (userModel.id.equals(voterId)) {
                                    link.add(userModel.image);
                                }
                            }
                        }
                        Glide.with(mContext).load(link.get(0)).placeholder(R.drawable.profile_icon).into(image1);
                        Glide.with(mContext).load(link.get(1)).placeholder(R.drawable.profile_icon).into(image2);
                        Glide.with(mContext).load(link.get(2)).placeholder(R.drawable.profile_icon).into(image3);
                    }

                } else {
                    countVote.setVisibility(View.GONE);
                    image1.setVisibility(View.GONE);
                    image2.setVisibility(View.GONE);
                    image3.setVisibility(View.GONE);
                }
                imagesLayout.addView(voting_images);
                progressBar.setMax(voteModel.chatModel.groupMembers.size());
                progressBar.setProgress(voteCount, true);
                votingOptions.addView(voting_options);
            }
            binding.votingResults.addView(voting_result);
        }
    }

    private void getMessages() {
        list = new ArrayList<>();
        Constants.databaseReference().child(Constants.CHATS)
                .child(Constants.auth().getCurrentUser().getUid()).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatModel model = dataSnapshot.getValue(ChatModel.class);
                            if (model.isGroup) {
                                list.add(model);
                            }
                        }
                        if (list.isEmpty()) {
                            binding.noPoll.setVisibility(View.VISIBLE);
                        } else {
                            binding.noPoll.setVisibility(View.GONE);
                            getVotingResults();
                        }
                    }
                    dialog.dismiss();
                }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupTimeline() {
        binding.timelineRC.setLayoutManager(new LinearLayoutManager(mContext));
        binding.timelineRC.setHasFixedSize(false);
        ArrayList<TimelineModel> list = new ArrayList<>();
        Constants.databaseReference().child(Constants.TIMELINE).child(Constants.auth().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                TimelineModel model = dataSnapshot.getValue(TimelineModel.class);
                                list.add(model);
                            }
                        }

                        if (list.isEmpty()) {
                            binding.noTimeline.setVisibility(View.VISIBLE);
                            binding.timelineRC.setVisibility(View.GONE);
                        } else {
                            binding.noTimeline.setVisibility(View.GONE);
                            binding.timelineRC.setVisibility(View.VISIBLE);
                        }

                        list.sort(Comparator.comparing(timelineModel -> timelineModel.timeline));
                        Collections.reverse(list);
                        TimelineAdapter adapter = new TimelineAdapter(mContext, list);
                        binding.timelineRC.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupLineChart() {

        List<String> dates = getDate();

        lineChart = binding.linechart;
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(0.9f);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        lineChart.setPinchZoom(true);

        lineChart.setBackgroundColor(Color.WHITE);

        lineChart.animateX(1500);

        Legend l = lineChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 1000 && value < 1000000) {
                    return (int) (value / 1000) + "K";
                } else if (value >= 1000000) {
                    return (int) (value / 1000000) + "M";
                } else {
                    return String.valueOf((int) value);
                }
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        lineChart.setExtraOffsets(0, 0, 0, 15);

    }

    private void setLineData(int count, float range) {

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * (range / 2f)) + 50;
            values1.add(new Entry(i, val));
        }

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 450;
            values2.add(new Entry(i, val));
        }

        ArrayList<Entry> values3 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 500;
            values3.add(new Entry(i, val));
        }

        LineDataSet set1, set2, set3;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) lineChart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values1, "");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(getResources().getColor(R.color.purple));
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            set1.setCircleColor(getResources().getColor(R.color.purple));
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(getResources().getColor(R.color.purple));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(values2, "");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(getResources().getColor(R.color.red));
            set2.setDrawCircles(false);
            set2.setDrawValues(false);
            set2.setCircleColor(getResources().getColor(R.color.red));
            set2.setLineWidth(2f);
            set2.setCircleRadius(3f);
            set2.setFillAlpha(65);
            set2.setFillColor(getResources().getColor(R.color.red));
            set2.setDrawCircleHole(false);
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            set3 = new LineDataSet(values3, "");
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set3.setColor(getResources().getColor(R.color.yellow));
            set3.setDrawCircles(false);
            set3.setDrawValues(false);
            set3.setCircleColor(getResources().getColor(R.color.yellow));
            set3.setLineWidth(2f);
            set3.setCircleRadius(3f);
            set3.setFillAlpha(65);
            set3.setFillColor(getResources().getColor(R.color.yellow));
            set3.setDrawCircleHole(false);
            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the data sets
            LineData data = new LineData(set1, set2, set3);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(9f);

            // set data
            lineChart.setData(data);
        }
    }

    private List<String> getDate() {
        List<String> dates = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            Date date = calendar.getTime();
            String formattedDate = sdf.format(date);
            dates.add(formattedDate);
        }
        Collections.reverse(dates);
        return dates;
    }

}