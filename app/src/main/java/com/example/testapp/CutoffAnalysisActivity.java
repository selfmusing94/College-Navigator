package com.example.testapp;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

// Essential Imports for MPAndroidChart
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

// Additional supporting imports
import android.graphics.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CutoffAnalysisActivity extends AppCompatActivity {
    // UI Components
    private TextInputEditText etMinCutoff, etMaxCutoff;
    private MaterialButton btnAnalyzeCutoffs;
    private CardView cardCutoffStats, cardCollegesList, cardCutoffChart;
    private TextView tvAverageCutoff, tvMinCutoff, tvMaxCutoff;
    private RecyclerView recyclerViewCutoffColleges;
    private BarChart cutoffDistributionChart;
    private LinearLayout emptyStateLayout;

    // Data
    private List<College> allColleges = new ArrayList<>();
    private List<College> filteredColleges;
    private CutoffCollegeAdapter collegeAdapter;
    private List<College> colleges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutoff_analysis);

        // Initialize Views
        initializeViews();

        // Load Colleges
        loadColleges();

        // Setup Listeners
        setupListeners();
    }

    private void initializeViews() {
        etMinCutoff = findViewById(R.id.etMinCutoff);
        etMaxCutoff = findViewById(R.id.etMaxCutoff);
        btnAnalyzeCutoffs = findViewById(R.id.btnAnalyzeCutoffs);

        cardCutoffStats = findViewById(R.id.cardCutoffStats);
        cardCollegesList = findViewById(R.id.cardCollegesList);
        cardCutoffChart = findViewById(R.id.cardCutoffChart);

        tvAverageCutoff = findViewById(R.id.tvAverageCutoff);
        tvMinCutoff = findViewById(R.id.tvMinCutoff);
        tvMaxCutoff = findViewById(R.id.tvMaxCutoff);

        recyclerViewCutoffColleges = findViewById(R.id.recyclerViewCutoffColleges);
        cutoffDistributionChart = findViewById(R.id.cutoffDistributionChart);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupListeners() {
        btnAnalyzeCutoffs.setOnClickListener(v -> analyzeCutoffs());
    }

    private void analyzeCutoffs() {
        // Validate input
        String minCutoffStr = etMinCutoff.getText().toString().trim();
        String maxCutoffStr = etMaxCutoff.getText().toString().trim();

        if (minCutoffStr.isEmpty() || maxCutoffStr.isEmpty()) {
            Toast.makeText(this, "Please enter both min and max cutoffs", Toast.LENGTH_SHORT).show();
            return;
        }

        int minCutoff = Integer.parseInt(minCutoffStr);
        int maxCutoff = Integer.parseInt(maxCutoffStr);

        if (minCutoff > maxCutoff){
            Toast.makeText(this,"Min Cutoff Cannot be Greater than Maximum Cutoff",Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter colleges
        filteredColleges = allColleges.stream()
                .filter(college ->
                        college.getCutoff() >= minCutoff &&
                                college.getCutoff() <= maxCutoff)
                .collect(Collectors.toList());

        // Update UI based on results
        updateUIWithResults(filteredColleges);
    }

    private void updateUIWithResults(List<College> colleges) {
        if (colleges.isEmpty()) {
            // Show empty state
            emptyStateLayout.setVisibility(View.VISIBLE);
            cardCutoffStats.setVisibility(View.GONE);
            cardCollegesList.setVisibility(View.GONE);
            cardCutoffChart.setVisibility(View.GONE);
            return;
        }

        // Hide empty state
        emptyStateLayout.setVisibility(View.GONE);

        // Update Statistics
        updateCutoffStatistics(colleges);

        // Setup RecyclerView
        setupCollegeRecyclerView(colleges);

        // Create Cutoff Distribution Chart
        createCutoffDistributionChart(colleges);

        // Show Cards
        cardCutoffStats.setVisibility(View.VISIBLE);
        cardCollegesList.setVisibility(View.VISIBLE);
        cardCutoffChart.setVisibility(View.VISIBLE);
    }

    private void updateCutoffStatistics(List<College> colleges) {
        double avgCutoff = colleges.stream()
                .mapToInt(College::getCutoff)
                .average()
                .orElse(0.0);

        int minCutoff = colleges.stream()
                .mapToInt(College::getCutoff)
                .min()
                .orElse(0);

        int maxCutoff = colleges.stream()
                .mapToInt(College::getCutoff)
                .max()
                .orElse(0);

        tvAverageCutoff.setText(String.format("Avg: %d", (int)avgCutoff));
        tvMinCutoff.setText(String.format("Min: %d", minCutoff));
        tvMaxCutoff.setText(String.format("Max: %d", maxCutoff));
    }

    private void setupCollegeRecyclerView(List<College> colleges) {
        collegeAdapter = new CutoffCollegeAdapter(this, colleges);
        recyclerViewCutoffColleges.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCutoffColleges.setAdapter(collegeAdapter);
    }

    private void createCutoffDistributionChart(List<College> colleges) {
        // Prepare data for bar chart
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Group colleges by cutoff ranges
        Map<String, List<College>> cutoffGroups = colleges.stream()
                .collect(Collectors.groupingBy(college -> {
                    int cutoff = college.getCutoff();
                    if (cutoff < 1000) return "0-1000";
                    if (cutoff < 2000) return "1000-2000";
                    if (cutoff < 3000) return "2000-3000";
                    if (cutoff < 4000) return "3000-4000";
                    if (cutoff < 5000) return "4000-5000";
                    return "5000+";
                }));

        // Create bar entries
        int index = 0;
        for (Map.Entry<String, List<College>> group : cutoffGroups.entrySet()) {
            entries.add(new BarEntry(index, group.getValue().size()));
            labels.add(group.getKey());
            index++;
        }

        // Configure chart
        BarDataSet dataSet = new BarDataSet(entries, "Colleges by Cutoff Range");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        cutoffDistributionChart.setData(barData);
        cutoffDistributionChart.getDescription().setEnabled(false);
        cutoffDistributionChart.animateY(1000);

        // Customize X-Axis
        XAxis xAxis = cutoffDistributionChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());

        // Customize Y-Axis
        YAxis yAxis = cutoffDistributionChart.getAxisLeft(); // Get the left Y-axis
        yAxis.setGranularity(1f); // Set granularity for Y-axis to 1
        yAxis.setAxisMinimum(0f); // Set minimum value for Y-axis

        // Disable the right Y-axis
        YAxis rightYAxis = cutoffDistributionChart.getAxisRight();
        rightYAxis.setEnabled(false); // Disable the right Y-axis

        // Remove horizontal and vertical grid lines
        yAxis.setDrawGridLines(false); // Disable horizontal grid lines
        xAxis.setDrawGridLines(false); // Disable vertical grid lines

        // Refresh chart
        cutoffDistributionChart.invalidate();
    }

    // Custom Adapter for Cutoff Colleges
    public static class CutoffCollegeAdapter extends RecyclerView.Adapter<CutoffCollegeAdapter.CutoffViewHolder> {
        private Context context;
        private List<College> collegeList;


        public CutoffCollegeAdapter(Context context, List<College> collegeList) {
            this.context = context;
            this.collegeList = collegeList;
        }

        @NonNull
        @Override
        public CutoffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_cutoff_college, parent, false);
            return new CutoffViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CutoffViewHolder holder, int position) {
            College college = collegeList.get(position);
            Typeface customfont = ResourcesCompat.getFont(context, R.font.acme_regular);

            // Set college details
            holder.tvCollegeName.setText(college.getName());
            holder.chipCutoff.setText(String.format("Cutoff : %d", college.getCutoff()));
            holder.chipCutoff.setTypeface(customfont);
            holder.tvLocation.setText(college.getLocation());
            holder.tvrating.setText(String.valueOf(college.getRating()));

            // Color coding based on cutoff
            int cutoff = college.getCutoff();
            int backgroundColor;
            if (cutoff < 2000) {
                backgroundColor = ContextCompat.getColor(context, R.color.cutoff_low);
            } else if (cutoff < 4000) {
                backgroundColor = ContextCompat.getColor(context, R.color.cutoff_medium);
            } else {
                backgroundColor = ContextCompat.getColor(context, R.color.cutoff_high);
            }
            holder.cardView.setCardBackgroundColor(backgroundColor);

        }

        @Override
        public int getItemCount() {
            return collegeList.size();
        }

        public static class CutoffViewHolder extends RecyclerView.ViewHolder {
            TextView tvCollegeName,  tvLocation, tvrating ;
            CardView cardView;
            Chip chipCutoff;

            public CutoffViewHolder(@NonNull View itemView) {
                super(itemView);

                tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
                chipCutoff = itemView.findViewById(R.id.chipCutoff);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                cardView = itemView.findViewById(R.id.cardViewofcutoff);
                tvrating = itemView.findViewById(R.id.tvRating);
            }
        }
    }


    private void loadColleges() {
        // Clear existing list
        allColleges.clear();

        // Mock Data - Replace with your actual data source
        allColleges.addAll(Arrays.asList(
                new College("Indian Institute of Technology, Delhi","Delhi",9.5,1961,Arrays.asList("Computer Science", "Mechanical Engineering", "Electrical Engineering", "Civil Engineering"),1245),
                new College("Indian Institute of Technology, Bombay", "Mumbai", 9.4, 1958, Arrays.asList("Computer Science", "Aerospace Engineering", "Chemical Engineering", "Data Science"), 1378),
                new College("Indian Institute of Technology, Madras", "Madras", 9.6, 1960, Arrays.asList("Computer Science", "Electronics", "Biotechnology", "Artificial Intelligence"), 1156),
                new College("National Institute of Technology, Trichy", "Tiruchirappalli", 8.9, 1964, Arrays.asList("Mechanical Engineering", "Electrical Engineering", "Computer Science", "Chemical Engineering"), 2345),
                new College("Birla Institute of Technology and Sciences, Pilani", "Pilani", 8.7, 1964, Arrays.asList("Computer Science", "Electronics", "Mechanical Engineering", "Mathematics"), 2567),
                new College("Jadavpur University", "Kolkata", 8.5, 1906, Arrays.asList("Computer Science", "Electronics", "Mechanical Engineering", "Architecture"), 3456),
                new College("Delhi University", "Delhi", 8.2, 1922, Arrays.asList("Arts", "Science", "Commerce", "Law"), 3789),
                new College("Anna University", "Chennai", 8.0, 1978, Arrays.asList("Computer Science", "Mechanical Engineering", "Electrical Engineering", "Civil Engineering"), 4123),
                new College("Indian Institute of Science", "Bangalore", 9.8, 1909, Arrays.asList("Computer Science", "Research", "Aerospace", "Biotechnology"), 1089),
                new College("Manipal Institute of Technology", "Manipal", 8.6, 1957, Arrays.asList("Computer Science", "Engineering", "Medical Sciences", "Management"), 2789)
        ));
    }
}