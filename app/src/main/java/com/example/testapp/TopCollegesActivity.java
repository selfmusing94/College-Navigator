package com.example.testapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TopCollegesActivity extends AppCompatActivity
        implements CollegeSortBottomSheet.OnSortAppliedListener,
        CollegeFilterBottomSheet.OnFilterAppliedListener {

    // UI Components
    private RecyclerView recyclerViewTopColleges;
    private TextInputEditText etTopCollegesCount;
    private TextInputLayout inputLayoutCollegeCount;
    private MaterialButton btnShowColleges, btnFilter, btnSort;
    private LinearLayout emptyStateLayout;
    private TextView tvNoCollegeFound;
    private ChipGroup chipGroupFilters;
    private HorizontalScrollView horizontalScrollViewFilters;

    // Data Management
    private List<College> originalCollegeList = new ArrayList<>();
    private List<College> filteredCollegeList = new ArrayList<>();
    private TopCollegesAdapter collegeAdapter;

    // Filter and Sort State
    private Integer currentSortType = null;
    private Integer currentSortOrder = null;
    private String currentMinRating = null;
    private List<String> currentCourses = new ArrayList<>();
    private List<String> currentLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_colleges);

        // Initialize UI Components
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load Initial Data
        loadColleges();

        // Setup Listeners
        setupSortFilterButtons();
        setupListeners();
        recyclerViewTopColleges.setVisibility(View.GONE);
    }

    private void initializeViews() {
        recyclerViewTopColleges = findViewById(R.id.recyclerViewTopColleges);
        etTopCollegesCount = findViewById(R.id.etTopCollegesCount);
        inputLayoutCollegeCount = findViewById(R.id.inputLayoutCollegeCount);
        btnShowColleges = findViewById(R.id.btnShowColleges);
        btnFilter = findViewById(R.id.btnFilter);
        btnSort = findViewById(R.id.btnSort);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvNoCollegeFound = findViewById(R.id.tvNoCollegeFound);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        horizontalScrollViewFilters = findViewById(R.id.horizontalScrollViewFilters);

    }

    private void setupRecyclerView() {
        collegeAdapter = new TopCollegesAdapter(this, filteredCollegeList);
        recyclerViewTopColleges.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTopColleges.setAdapter(collegeAdapter);
    }

    private void setupSortFilterButtons() {
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

    private void setupListeners() {
        // Show Colleges Button
        btnShowColleges.setOnClickListener(v -> filterCollegesByCount());

        // College Count Input Validation
        etTopCollegesCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateCollegeCount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadColleges() {
        // Simulated data loading - replace with your actual data source
        List<College> colleges = new ArrayList<>();
        colleges.add(new College("Massachusetts Institute of Technology (MIT)", "Cambridge, MA", 9.5, 1861,Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"),519));
        colleges.add(new College("Stanford University", "Stanford, CA", 9.3, 1885, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"),834));
        colleges.add(new College("Harvard University", "Cambridge, MA", 9.7, 1636, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"),215));
        colleges.add(new College("California Institute of Technology (Caltech)", "Pasadena, CA", 9.2, 1891, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"),265));
        colleges.add(new College("University of Chicago", "Chicago, IL", 9.0, 1890, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 918));
        colleges.add(new College("Columbia University", "New York, NY", 8.9, 1754, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 186));
        colleges.add(new College( "Princeton University", "Princeton, NJ", 9.1, 1746, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 1819));
        colleges.add(new College( "Yale University", "New Haven, CT", 9.0, 1701, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 926));
        colleges.add(new College( "University of Pennsylvania", "Philadelphia, PA", 8.8, 1740, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 1523));
        colleges.add(new College( "University of California, Berkeley", "Berkeley, CA", 8.7, 1868, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 1519));
        colleges.add(new College( "University of Michigan", "Ann Arbor, MI", 8.6, 1817, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 5195));
        colleges.add(new College( "Duke University", "Durham, NC", 8.5, 1838, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 5192));
        colleges.add(new College( "Northwestern University", "Evanston, IL", 8.4, 1851, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 5189));
        colleges.add(new College( "University of California, Los Angeles (UCLA)", "Los Angeles, CA", 8.3, 1919, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 2519));
        colleges.add(new College( "University of Virginia", "Charlottesville, VA", 8.2, 1819, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 1919));
        colleges.add(new College( "University of Southern California (USC)", "Los Angeles, CA", 8.1, 1880, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 11519));
        colleges.add(new College( "University of Washington", "Seattle, WA", 8.0, 1861, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 1218));
        colleges.add(new College( "University of Texas at Austin", "Austin, TX", 7.9, 1883, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 5619));
        colleges.add(new College( "University of Wisconsin-Madison", "Madison, WI", 7.8, 1848, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 651));
        colleges.add(new College( "University of Illinois at Urbana-Champaign", "Champaign, IL", 7.7, 1867, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 3284));
        colleges.add(new College( "University of Florida", "Gainesville, FL", 7.6, 1853, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 3215));
        colleges.add(new College( "University of North Carolina at Chapel Hill", "Chapel Hill, NC", 7.5, 1789, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 5312));
        colleges.add(new College( "University of California, San Diego (UCSD)", "La Jolla, CA", 7.4, 1960, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 1862));
        colleges.add(new College( "University of California, Irvine (UCI)", "Irvine, CA", 7.3, 1965, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 1567));
        colleges.add(new College( "University of Maryland, College Park", "College Park, MD", 7.2, 1856, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 4561));
        colleges.add(new College( "University of California, Davis", "Davis, CA", 7.1, 1905, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 8965));
        colleges.add(new College( "University of Minnesota", "Minneapolis, MN", 7.0, 1851, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 4544));
        colleges.add(new College( "University of Pittsburgh", "Pittsburgh, PA", 6.9, 1787, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 7854));
        colleges.add(new College( "University of Colorado Boulder", "Boulder, CO", 6.8, 1876, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 1231));
        colleges.add(new College( "University of Arizona", "Tucson, AZ", 6.7, 1885, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 6516));
        colleges.add(new College( "University of Notre Dame", "Notre Dame, IN", 6.6, 1842, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 8541));
        colleges.add(new College( "University of California, Santa Barbara", "Santa Barbara, CA", 6.5, 1944, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 5981));
        colleges.add(new College( "University of Miami", "Coral Gables, FL", 6.4, 1925, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 4865));
        colleges.add(new College( "University of Connecticut", "Storrs, CT", 6.3, 1881, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 5172));
        colleges.add(new College( "University of Massachusetts Amherst", "Amherst, MA", 6.2, 1863, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 1023));
        colleges.add(new College( "University of Iowa", "Iowa City, IA", 6.1, 1847, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 2036));
        colleges.add(new College( "University of Oregon", "Eugene, OR", 6.0, 1876, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 9092));
        colleges.add(new College( "University of Tennessee", "Knoxville, TN", 5.9, 1794, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 6932));
        colleges.add(new College( "University of South Carolina", "Columbia, SC", 5.8, 1801, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 5789));
        colleges.add(new College( "University of Kansas", "Lawrence, KS", 5.7, 1865, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 2806));
        colleges.add(new College( "University of Nebraska-Lincoln", "Lincoln, NE", 5.6, 1869, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 2206));
        colleges.add(new College( "University of Alabama", "Tuscaloosa, AL", 5.5, 1831, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 5103));
        colleges.add(new College( "University of Kentucky", "Lexington, KY", 5.4, 1865, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 5129));
        colleges.add(new College( "University of Arkansas", "Fayetteville, AR", 5.3, 1871, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 2689));
        colleges.add(new College( "Florida State University", "Tallahassee, FL", 5.2, 1851, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 5150));
        colleges.add(new College( "Louisiana State University", "Baton Rouge, LA", 5.1, 1860, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 1506));
        colleges.add(new College( "Oregon State University", "Corvallis, OR", 5.0, 1868, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 3519));
        colleges.add(new College( "Michigan State University", "East Lansing, MI", 4.9, 1855, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 1232));
        colleges.add(new College( "Clemson University", "Clemson, SC", 4.8, 1889, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 1231));
        colleges.add(new College( "Iowa State University", "Ames, IA", 4.7, 1858, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 5141));

        originalCollegeList.clear();
        originalCollegeList.addAll(colleges);
        filteredCollegeList.addAll(originalCollegeList);
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
            Collections.reverse(filteredCollegeList);
        }

        removeChipsByType("sort");

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
        updateNoCollegesView();
    }

    @Override
    public void onFilterApplied(String filterType, String filterValue) {

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
                return  "Sort by Cutoff";
            default:
                return "Unknown Sort";
        }
    }

    @Override
    public void onFilterApplied(String minRating, List<String> courses, List<String> locations) {
        currentMinRating = minRating;
        currentCourses.clear();
        currentCourses.addAll(courses);
        currentLocations.clear();
        currentLocations.addAll(locations);

        // Apply filters
        filteredCollegeList.clear();
        filteredCollegeList.addAll(originalCollegeList);

        // Filter by rating
        if (!TextUtils.isEmpty(minRating)) {
            double rating = Double.parseDouble(minRating);
            filteredCollegeList = filterByRating(filteredCollegeList, rating);
        }

        // Filter by courses
        if (!courses.isEmpty()) {
            filteredCollegeList = filterByCourses(filteredCollegeList, courses);
        }

        // Filter by locations
        if (!locations.isEmpty()) {
            filteredCollegeList = filterByLocations(filteredCollegeList, locations);
        }

        collegeAdapter.notifyDataSetChanged();
        updateNoCollegesView();
    }

    private List<College> filterByRating(List<College> colleges, double minRating) {
        return colleges.stream()
                .filter(college -> college.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    private List<College> filterByCourses(List<College> colleges, List<String> courses) {
        return colleges.stream()
                .filter(college -> college.getCourses().stream().anyMatch(courses::contains))
                .collect(Collectors.toList());
    }

    private List<College> filterByLocations(List<College> colleges, List<String> locations) {
        return colleges.stream()
                .filter(college -> locations.contains(college.getLocation()))
                .collect(Collectors.toList());
    }

    private void sortByEstablishedYear() {
        Collections.sort(filteredCollegeList, Comparator.comparingInt(College::getEstablishedYear));
    }

    private void sortAlphabetically() {
        Collections.sort(filteredCollegeList, Comparator.comparing(College::getName));
    }

    private void sortByRating() { // New method for sorting by rank
        Collections.sort(filteredCollegeList, Comparator.comparing(College::getRating));
    }

    private void sortByCutoff() { // New method for sorting by rank
        Collections.sort(filteredCollegeList, Comparator.comparing(College::getCutoff));
    }



    private void updateNoCollegesView() {
        tvNoCollegeFound.setVisibility(filteredCollegeList.isEmpty() ? View.VISIBLE : View.GONE);
    }

        private void filterCollegesByCount() {
            String countStr = etTopCollegesCount.getText().toString().trim();
            if (countStr.isEmpty()) {
                Toast.makeText(this, "Please enter number of colleges", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = Integer.parseInt(countStr);
            if (count <= 0 || count > originalCollegeList.size()) {
                Toast.makeText(this, "Invalid number of colleges", Toast.LENGTH_SHORT).show();
                return;
            }

            filteredCollegeList.clear();
            filteredCollegeList.addAll(
                    originalCollegeList.stream()
                            .limit(count)
                            .collect(Collectors.toList())
            );

            // Show RecyclerView when colleges are filtered
            recyclerViewTopColleges.setVisibility(View.VISIBLE);

            collegeAdapter.notifyDataSetChanged();
            updateEmptyState();
        }


    private void validateCollegeCount(String count) {
        try {
            int collegeCount = Integer.parseInt(count);
            if (collegeCount <= 0 || collegeCount > originalCollegeList.size()) {
                inputLayoutCollegeCount.setError("Invalid number");
            } else {
                inputLayoutCollegeCount.setError(null);
            }
        } catch (NumberFormatException e) {
            inputLayoutCollegeCount.setError("Invalid number");
        }
    }

    private void updateEmptyState() {
        if (filteredCollegeList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerViewTopColleges.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerViewTopColleges.setVisibility(View.VISIBLE);
        }
    }

    private void removeChipsByType(String type) {
        for (int i = chipGroupFilters.getChildCount() - 1; i >= 0; i--) {
            Chip chip = (Chip) chipGroupFilters.getChildAt(i);
            if (type.equals(chip.getTag())) {
                chipGroupFilters.removeView(chip);
            }
        }

        // Hide horizontal scroll if no chips
        if (chipGroupFilters.getChildCount() == 0) {
            horizontalScrollViewFilters.setVisibility(View.GONE);
        }
    }

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

    //Assign color to sort/filter applied chip
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

        chipGroupFilters.addView(chip);
        horizontalScrollViewFilters.setVisibility(View.VISIBLE);
    }
}