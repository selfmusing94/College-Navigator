package com.example.testapp;

import static android.app.ProgressDialog.show;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CutoffAnalysisActivity extends AppCompatActivity
        implements CollegeSortBottomSheet.OnSortAppliedListener,
        CollegeFilterBottomSheet.OnFilterAppliedListener {
    // UI Components
    private TextInputEditText etMinCutoff, etMaxCutoff;
    private MaterialButton btnAnalyzeCutoffs,btnFilter,btnSort;
    private CardView cardCutoffStats, cardCollegesList, cardCutoffChart;
    private TextView tvAverageCutoff, tvMinCutoff, tvMaxCutoff;
    private RecyclerView recyclerViewCutoffColleges;
    private BarChart cutoffDistributionChart;
    private LinearLayout emptyStateLayout;
    private ChipGroup chipGroupFilters;
    private HorizontalScrollView horizontalScrollViewFilters;

    // Data
    private List<College> allColleges = new ArrayList<>();
    private List<College> filteredColleges;
    private CutoffCollegeAdapter collegeAdapter;

    // Filter and Sort State
    private Integer currentSortType = null;
    private Integer currentSortOrder = null;
    private String currentMinRating = null;
    private List<String> currentCourses = new ArrayList<>();
    private List<String> currentLocations = new ArrayList<>();


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

        btnSort=findViewById(R.id.cutoffanalysisbtnSort);
        btnFilter=findViewById(R.id.cutoffanalysisbtnFilter);
        chipGroupFilters = findViewById(R.id.CutoffchipGroupFilters);
        horizontalScrollViewFilters = findViewById(R.id.CutoffhorizontalScrollViewFilters);
    }

    private void setupListeners() {
        btnAnalyzeCutoffs.setOnClickListener(v -> analyzeCutoffs());
        btnSort.setOnClickListener(v -> showSortBottomSheet());
        btnFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void showSortBottomSheet() {
        CollegeSortBottomSheet sortBottomSheet = new CollegeSortBottomSheet();
        sortBottomSheet.show(getSupportFragmentManager(), sortBottomSheet.getTag());
    }

    private void showFilterBottomSheet() {
        CollegeFilterBottomSheet filterBottomSheet = new CollegeFilterBottomSheet();
        filterBottomSheet.show(getSupportFragmentManager(), filterBottomSheet.getTag());
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
        if(minCutoff == maxCutoff){
            Toast.makeText(this,"Both Cutoff's Cannot be same.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter colleges based on cutoff range
        filteredColleges = allColleges.stream()
                .filter(college -> college.getCutoff() >= minCutoff && college.getCutoff() <= maxCutoff)
                .collect(Collectors.toList());

        // Apply additional filters based on chips
        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupFilters.getChildAt(i);
            removeFilter(chip.getText().toString());
        }
        // Update UI based on results
        updateUIWithResults(filteredColleges);
    }

    private void updateUIWithResults(List<College> colleges) {
        if (colleges.isEmpty()) {
            // Show empty state
            Toast.makeText(this,"No Colleges Found",Toast.LENGTH_SHORT).show();
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

    private void loadColleges() {
        // Clear existing list
        allColleges.clear();

        // Mock Data - Replace with your actual data source
        allColleges.addAll(Arrays.asList(
                new College("Indian Institute of Science", "Bangalore", 9.8, 1909, Arrays.asList("Computer Science", "Research", "Aerospace", "Biotechnology"), 1089),
                new College("Indian Institute of Technology, Delhi","Delhi",9.5,1961,Arrays.asList("Computer Science", "Mechanical Engineering", "Electrical Engineering", "Civil Engineering"),1245),
                new College("Indian Institute of Technology, Bombay", "Mumbai", 9.4, 1958, Arrays.asList("Computer Science", "Aerospace Engineering", "Chemical Engineering", "Data Science"), 1378),
                new College("Indian Institute of Technology, Madras", "Madras", 9.6, 1960, Arrays.asList("Computer Science", "Electronics", "Biotechnology", "Artificial Intelligence"), 1156),
                new College("National Institute of Technology, Trichy", "Tiruchirappalli", 8.9, 1964, Arrays.asList("Mechanical Engineering", "Electrical Engineering", "Computer Science", "Chemical Engineering"), 2345),
                new College("Birla Institute of Technology and Sciences, Pilani", "Pilani", 8.7, 1964, Arrays.asList("Computer Science", "Electronics", "Mechanical Engineering", "Mathematics"), 2567),
                new College("Jadavpur University", "Kolkata", 8.5, 1906, Arrays.asList("Computer Science", "Electronics", "Mechanical Engineering", "Architecture"), 3456),
                new College("Delhi University", "Delhi", 8.2, 1922, Arrays.asList("Arts", "Science", "Commerce", "Law"), 3789),
                new College("Anna University", "Chennai", 8.0, 1978, Arrays.asList("Computer Science", "Mechanical Engineering", "Electrical Engineering", "Civil Engineering"), 4123),
                new College("Manipal Institute of Technology", "Manipal", 8.6, 1957, Arrays.asList("Computer Science", "Engineering", "Medical Sciences", "Management"), 2789)
        ));
    }

    @Override
    public void onSortApplied(Integer sortType, Integer sortOrder) {
        currentSortType = sortType;
        currentSortOrder = sortOrder;

        // Apply sorting
        if (sortType != null) {
            switch (sortType) {
                case CollegeSortBottomSheet.SORT_BY_ESTABLISHED:
                    sortByEstablishedYear();
                    break;
                case CollegeSortBottomSheet.SORT_ALPHABETICAL:
                    sortAlphabetically();
                    break;
                case (int) CollegeSortBottomSheet.SORT_BY_RATING: // New case for sorting by rank
                    sortByRating();
                    break;
                case CollegeSortBottomSheet.SORT_BY_CUTOFF:
                    sortByCutoff();
                    break;
            }
        }

        // Apply sort order
        if (sortOrder != null && sortOrder == CollegeSortBottomSheet.SORT_DESCENDING) {
            Collections.reverse(filteredColleges);
        }

        removeFilterChip("sort");

        // Add new sort chip based on sort type
        if (sortType != null) {
            String sortLabel = getSortLabel(sortType);
            if (sortOrder != null && sortOrder == CollegeSortBottomSheet.SORT_DESCENDING) {
                sortLabel += " (Descending)";
            }
            addFilterChip(sortLabel, "sort");
        }

        // Existing sort logic...
        collegeAdapter.notifyDataSetChanged();
    }

    private String getSortLabel(Integer sortType) {
        switch (sortType) {
            case CollegeSortBottomSheet.SORT_BY_ESTABLISHED:
                return "Sort by Established Year";
            case CollegeSortBottomSheet.SORT_ALPHABETICAL:
                return "Sort Alphabetically";
            case (int) CollegeSortBottomSheet.SORT_BY_RATING:
                return "Sort by Rating";
            case CollegeSortBottomSheet.SORT_BY_CUTOFF:
                return "Sort by Cutoff";
            default:
                return "Unknown Sort";
        }
    }

    private void sortByEstablishedYear() {// Or handle the case as needed
        if (filteredColleges == null || filteredColleges.isEmpty()) {
            return;
        }
            Collections.sort(filteredColleges, Comparator.comparingInt(College::getEstablishedYear));
    }

    private void sortAlphabetically() {// Or handle the case as needed
        if (filteredColleges == null || filteredColleges.isEmpty()) {
            return;
        }
            Collections.sort(filteredColleges, Comparator.comparing(College::getName));
    }

    private void sortByRating() {// New method for sorting by rank
        if (filteredColleges == null || filteredColleges.isEmpty()) {
            return; // Or handle the case as needed
        }
            Collections.sort(filteredColleges, Comparator.comparing(College::getRating));
    }

    private void sortByCutoff() { // New method for sorting by rank
        if (filteredColleges == null || filteredColleges.isEmpty()) {
            return; // Or handle the case as needed
        }
        Collections.sort(filteredColleges, Comparator.comparing(College::getCutoff));
    }

    private void removeFilterChip(String text) {
        Log.d("removeFilterChip", "Removing chip: " + text);
        Log.d("removeFilterChip", "Current courses: " + currentCourses);

        // Remove the chip from the group
        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupFilters.getChildAt(i);
            if (chip.getText().toString().equals(text)) {
                Log.d("removeFilterChip", "Found chip to remove: " + chip.getText());
                chipGroupFilters.removeViewAt(i);
                break;
            }
        }

        // Remove the course from the currentCourses list
        Log.d("removeFilterChip", "Removing course from currentCourses: " + text);
        currentCourses.remove(text);
        Log.d("removeFilterChip", "Updated currentCourses: " + currentCourses);

        // Reapply filters only if currentCourses is not empty
        if (!currentCourses.isEmpty() || !currentLocations.isEmpty() || !TextUtils.isEmpty(currentMinRating)) {
            onFilterApplied(currentMinRating, currentCourses, currentLocations);
        } else {
            // If all filters are removed, show the original list
            filteredColleges.clear();
            filteredColleges.addAll(allColleges);
            collegeAdapter.notifyDataSetChanged();
        }

        // Hide horizontal scroll if no chips
        if (chipGroupFilters.getChildCount() == 0) {
            horizontalScrollViewFilters.setVisibility(View.GONE);
        }
    }

    // Method to remove a specific filter
    private void removeFilter(String chipText) {
        // Implement logic to remove corresponding filter
        if (chipText.startsWith("Rating")) {
            currentMinRating = null;
        } else if (chipText.startsWith("Course:")) {
            String course = chipText.replace("Course: ", "");
            currentCourses.remove(course);
        } else if (chipText.startsWith("Location:")) {
            String location = chipText.replace("Location: ", "");
            currentLocations.remove(location);
        } else if (chipText.contains("Sort")) {
            currentSortType = null;
            currentSortOrder = null;
        }

        // Reapply filters or reset sorting
        onFilterApplied(currentMinRating, currentCourses, currentLocations);
    }

    // Add filter chip for rating
    private void addRatingFilterChip(double rating) {
        String filterText = "Rating: " + rating;
        addFilterChip(filterText, "filter");
    }

    // Add filter chip for course
    private void addCourseFilterChip(String course) {
        String filterText = "Course: " + course;
        addFilterChip(filterText, "filter");
    }

    // Add filter chip for location
    private void addLocationFilterChip(String location) {
        String filterText = "Location: " + location;
        addFilterChip(filterText, "filter");
    }

    // Add sort/filter applied chip
    private void addFilterChip(String text, String type) {
        View chipView = getLayoutInflater().inflate(R.layout.item_filter_chip, null);
        Chip chip = chipView.findViewById(R.id.chipFilter);

        chip.setText(text);
        chip.setTag(type);

        // Categorize chip colors
        switch (type) {
            case "filter":
                if (text.startsWith("Rating")) {
                    chip.setChipBackgroundColorResource(R.color.chip_background_primary);
                    chip.setTextColor(getColor(R.color.chip_text_primary));
                    chip.setCloseIconTintResource(R.color.chip_close_icon_primary);
                } else if (text.startsWith("Course:")) {
                    chip.setChipBackgroundColorResource(R.color.chip_background_filter);
                    chip.setTextColor(getColor(R.color.chip_text_filter));
                    chip.setCloseIconTintResource(R.color.chip_close_icon_filter);
                } else if (text.startsWith("Location:")) {
                    chip.setChipBackgroundColorResource(R.color.chip_background_sort);
                    chip.setTextColor(getColor(R.color.chip_text_sort));
                    chip.setCloseIconTintResource(R.color.chip_close_icon_sort);
                }
                break;
            case "sort":
                chip.setChipBackgroundColorResource(R.color.chip_background_sort);
                chip.setTextColor(getColor(R.color.chip_text_sort));
                chip.setCloseIconTintResource(R.color.chip_close_icon_sort);
                break;
            default:
                chip.setChipBackgroundColorResource(R.color.chip_background_primary);
                chip.setTextColor(getColor(R.color.chip_text_primary));
                chip.setCloseIconTintResource(R.color.chip_close_icon_primary);
        }

        chip.setOnCloseIconClickListener(v -> {
            chipGroupFilters.removeView(chip);
            removeFilter(text);
        });

        // Check if a chip with the same text already exists
        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {
            Chip existingChip = (Chip) chipGroupFilters.getChildAt(i);
            if (existingChip.getText().toString().equals(text)) {
                //Dont add the chip again , so just return without adding the chip , here there was a toast message , which was removed as its being showed
                //everytime as this is beimg checked again and again.
                return;
            }
        }

        chipGroupFilters.addView(chip);
        horizontalScrollViewFilters.setVisibility(View.VISIBLE);
    }


    // Implement the OnFilterAppliedListener interface method
    @Override
    public void onFilterApplied(String minRating, List<String> courses, List<String> locations) {
        currentMinRating = minRating;

        // Only add the course if it's not already in the list
        for (String course : courses) {
            if (!currentCourses.contains(course)) {
                currentCourses.add(course);
            }
        }

        // Only add the  location if it's not already in the list
        for (String location : locations) {
            if (!currentLocations.contains(location)) {
                currentLocations.add(location);
            }
        }

        // Create a temporary list of the original maxcutoff colleges
        int maxcutoff = Integer.parseInt(etMaxCutoff.getText().toString());
        List<College> tempCollegeList = new ArrayList<>(allColleges.subList(0, Math.min(maxcutoff, allColleges.size())));

        // Apply filters to the temporary list
        if (!TextUtils.isEmpty(minRating)) {
            try {
                double rating = Double.parseDouble(minRating);
                tempCollegeList = filterByRating(tempCollegeList, rating);
                addFilterChip("Rating: " + minRating, "filter");
            } catch (Exception e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }



        if (!currentCourses.isEmpty()) {
            try {
                tempCollegeList = filterByCourses(tempCollegeList, currentCourses);
                for (String course : currentCourses) {
                    addFilterChip("Course: " + course, "filter");
                }
            }catch (Exception e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        if (!currentLocations.isEmpty()) {
            try {
                tempCollegeList = filterByLocations(tempCollegeList, currentLocations);
                for (String location : currentLocations) {
                    addFilterChip("Location: " + location, "filter");
                }
            } catch (Exception e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        // Update the filtered college list
        filteredColleges.clear();
        filteredColleges.addAll(tempCollegeList);

        collegeAdapter.setData(filteredColleges); // Update adapter data reference
        collegeAdapter.notifyDataSetChanged();
        Log.d("Adapter Data", "Adapter data size: " + collegeAdapter.getData().size());
        Log.d("Adapter Count", "Adapter item count: " + collegeAdapter.getItemCount());

        recyclerViewCutoffColleges.invalidate(); // Try invalidating the RecyclerView

        updateUIWithResults(filteredColleges);
    }


    @Override
    public void onFilterApplied(String filterType, String filterValue) {
    }

    private List<College> filterByRating(List<College> colleges, double minRating) {
        Log.d("Filtering", "Filtering by rating: " + minRating);
        return colleges.stream()
                .filter(college -> college.getRating() >= minRating)
                .peek(college -> Log.d("Filtering", "College passed rating filter: " + college.getName()))
                .collect(Collectors.toList());
    }

    private List<College> filterByCourses(List<College> colleges, List<String> courses) {
        Log.d("Filtering", "Filtering by courses: " + courses);
        return colleges.stream()
                .filter(college -> college.getCourses().stream().anyMatch(courses::contains))
                .peek(college -> Log.d("Filtering", "College passed courses filter: " + college.getName()))
                .collect(Collectors.toList());
    }

    private List<College> filterByLocations(List<College> colleges, List<String> locations) {
        Log.d("Filtering", "Filtering by locations: " + locations);
        return colleges.stream()
                .filter(college -> locations.contains(college.getLocation()))
                .peek(college -> Log.d("Filtering", "College passed locations filter: " + college.getName()))
                .collect(Collectors.toList());
    }
}