package com.moutamid.chama.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.moutamid.chama.adapters.TimelineAdapter;
import com.moutamid.chama.bottomsheets.DateFilter;
import com.moutamid.chama.databinding.FragmentDashboardBinding;
import com.moutamid.chama.models.ChatModel;
import com.moutamid.chama.models.MessageModel;
import com.moutamid.chama.models.TimelineModel;
import com.moutamid.chama.models.TransactionModel;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.models.VoteModel;
import com.moutamid.chama.utilis.Constants;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class DashboardFragment extends Fragment {
    FragmentDashboardBinding binding;
    private LineChart lineChart;
    private BarChart chart;
    ArrayList<ChatModel> list;
    ArrayList<VoteModel> votes;
    Context mContext;
    Dialog dialog;

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
        setupTimeline();

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
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void setupTimeline() {
        binding.timelineRC.setLayoutManager(new LinearLayoutManager(requireContext()));
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

    private void setupBarchart() {
        chart = binding.barchart;
        chart.getDescription().setEnabled(false);

//        chart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);

        chart.setDrawGridBackground(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
        String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int monthIndex = (int) value;
                if (monthIndex >= 0 && monthIndex < MONTHS.length) {
                    return MONTHS[monthIndex];
                } else {
                    return "";
                }
            }
        });

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value > 1000) {
                    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
                    formatter.setMinimumFractionDigits(0);
                    formatter.setMaximumFractionDigits(0);
                    return formatter.format(value / 1000) + "k"; // Add "k" for thousands
                } else {
                    return String.valueOf((int) value); // Display as integer for small values
                }
            }
        });

        chart.getAxisRight().setEnabled(false);

        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.2f; // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"
        int groupCount = 10 + 1;
        int startYear = 1980;
        int endYear = startYear + groupCount;

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();

        float randomMultiplier = 100 * 100000f;

        for (int i = 0; i < 12; i++) {
            values1.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
            values2.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
            values3.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
        }

        BarDataSet set1, set2, set3;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            // create 4 DataSets
            set1 = new BarDataSet(values1, "Normal");
            set1.setColor(getResources().getColor(R.color.bar_normal));
            set2 = new BarDataSet(values2, "Locked");
            set2.setColor(getResources().getColor(R.color.bar_locked));
            set3 = new BarDataSet(values3, "Withdrawal");
            set3.setColor(getResources().getColor(R.color.bar_withdrawal));

            set1.setDrawValues(false);
            set2.setDrawValues(false);
            set3.setDrawValues(false);

            BarData data = new BarData(set1, set2, set3);
            data.setValueFormatter(new LargeValueFormatter());

            chart.setData(data);
        }

        // specify the width each bar should have
        chart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart.getXAxis().setAxisMinimum(0);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.getXAxis().setAxisMaximum(startYear + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(startYear, groupSpace, barSpace);
        chart.invalidate();

    }

    private void setupLineChart() {
        lineChart = binding.linechart;

        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);

        lineChart.animateX(1500);

        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

//        YAxis rightAxis = lineChart.getAxisRight();
//        rightAxis.setTextColor(Color.BLACK);
//        rightAxis.setAxisMaximum(900);
//        rightAxis.setAxisMinimum(-200);
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setDrawZeroLine(false);
//        rightAxis.setGranularityEnabled(false);

        setLineData(31, 50);
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

}