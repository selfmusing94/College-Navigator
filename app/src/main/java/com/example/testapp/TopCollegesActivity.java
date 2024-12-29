package com.example.testapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;

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
    private LottieAnimationView lottieEmptyState;

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

        // Setup Listeners
        setupListeners();
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
        lottieEmptyState = findViewById(R.id.lottieEmptyState);
        btnSort = findViewById(R.id.btnSort);
        btnFilter = findViewById(R.id.btnFilter);
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
        // Use the list of 50 colleges created here
        List<College> colleges = new ArrayList<>();
        colleges.add(new College(1, "Massachusetts Institute of Technology (MIT)", "Cambridge, MA", 9.5, 1861,Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(2, "Stanford University", "Stanford, CA", 9.3, 1885, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(3, "Harvard University", "Cambridge, MA", 9.7, 1636, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(4, "California Institute of Technology (Caltech)", "Pasadena, CA", 9.2, 1891, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(5, "University of Chicago", "Chicago, IL", 9.0, 1890, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(6, "Columbia University", "New York, NY", 8.9, 1754, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(7, "Princeton University", "Princeton, NJ", 9.1, 1746, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(8, "Yale University", "New Haven, CT", 9.0, 1701, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(9, "University of Pennsylvania", "Philadelphia, PA", 8.8, 1740, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(10, "University of California, Berkeley", "Berkeley, CA", 8.7, 1868, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(11, "University of Michigan", "Ann Arbor, MI", 8.6, 1817, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(12, "Duke University", "Durham, NC", 8.5, 1838, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(13, "Northwestern University", "Evanston, IL", 8.4, 1851, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(14, "University of California, Los Angeles (UCLA)", "Los Angeles, CA", 8.3, 1919, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(15, "University of Virginia", "Charlottesville, VA", 8.2, 1819, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(16, "University of Southern California (USC)", "Los Angeles, CA", 8.1, 1880, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(17, "University of Washington", "Seattle, WA", 8.0, 1861, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(18, "University of Texas at Austin", "Austin, TX", 7.9, 1883, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(19, "University of Wisconsin-Madison", "Madison, WI", 7.8, 1848, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(20, "University of Illinois at Urbana-Champaign", "Champaign, IL", 7.7, 1867, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(21, "University of Florida", "Gainesville, FL", 7.6, 1853, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(22, "University of North Carolina at Chapel Hill", "Chapel Hill, NC", 7.5, 1789, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(23, "University of California, San Diego (UCSD)", "La Jolla, CA", 7.4, 1960, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(24, "University of California, Irvine (UCI)", "Irvine, CA", 7.3, 1965, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(25, "University of Maryland, College Park", "College Park, MD", 7.2, 1856, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(26, "University of California, Davis", "Davis, CA", 7.1, 1905, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(27, "University of Minnesota", "Minneapolis, MN", 7.0, 1851, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(28, "University of Pittsburgh", "Pittsburgh, PA", 6.9, 1787, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(29, "University of Colorado Boulder", "Boulder, CO", 6.8, 1876, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(30, "University of Arizona", "Tucson, AZ", 6.7, 1885, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(31, "University of Notre Dame", "Notre Dame, IN", 6.6, 1842, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(32, "University of California, Santa Barbara", "Santa Barbara, CA", 6.5, 1944, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(33, "University of Miami", "Coral Gables, FL", 6.4, 1925, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(34, "University of Connecticut", "Storrs, CT", 6.3, 1881, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(35, "University of Massachusetts Amherst", "Amherst, MA", 6.2, 1863, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(36, "University of Iowa", "Iowa City, IA", 6.1, 1847, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(37, "University of Oregon", "Eugene, OR", 6.0, 1876, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(38, "University of Tennessee", "Knoxville, TN", 5.9, 1794, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(39, "University of South Carolina", "Columbia, SC", 5.8, 1801, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(40, "University of Kansas", "Lawrence, KS", 5.7, 1865, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(41, "University of Nebraska-Lincoln", "Lincoln, NE", 5.6, 1869, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(42, "University of Alabama", "Tuscaloosa, AL", 5.5, 1831, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(43, "University of Kentucky", "Lexington, KY", 5.4, 1865, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(44, "University of Arkansas", "Fayetteville, AR", 5.3, 1871, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(45, "Florida State University", "Tallahassee, FL", 5.2, 1851, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(46, "Louisiana State University", "Baton Rouge, LA", 5.1, 1860, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(47, "Oregon State University", "Corvallis, OR", 5.0, 1868, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));
        colleges.add(new College(48, "Michigan State University", "East Lansing, MI", 4.9, 1855, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS")));
        colleges.add(new College(49, "Clemson University", "Clemson, SC", 4.8, 1889, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML")));
        colleges.add(new College(50, "Iowa State University", "Ames, IA", 4.7, 1858, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS")));

        originalCollegeList.clear();
        originalCollegeList.addAll(colleges); // Assuming 'colleges' is the list from the example
        filteredCollegeList.addAll(originalCollegeList);
    }

    @Override
    public void onSortApplied(Integer sortType, Integer sortOrder) {
        // Update current sort state
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
            }
        }

        // Apply sort order
        if (sortOrder != null) {
            if (sortOrder == CollegeSortBottomSheet.SORT_DESCENDING) {
                Collections.reverse(filteredCollegeList);
            }
        }

        collegeAdapter.notifyDataSetChanged();
        updateNoCollegesView();
    }

    @Override
    public void onFilterApplied(String minRating,
                                List<String> courses,
                                List<String> locations) {
        // Update current filter state
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
                .filter(college ->
                        college.getCourses().stream()
                                .anyMatch(courses::contains)
                )
                .collect(Collectors.toList());
    }

    private List<College> filterByLocations(List<College> colleges, List<String> locations) {
        return colleges.stream()
                .filter(location -> locations.contains(location.getLocation()))
                .collect(Collectors.toList());
    }

    private void sortByEstablishedYear() {
        Collections.sort(filteredCollegeList,
                (c1, c2) -> Integer.compare(c1.getEstablishedYear(), c2.getEstablishedYear()));
    }

    private void sortAlphabetically() {
        Collections.sort(filteredCollegeList,
                (c1, c2) -> c1.getName().compareTo(c2.getName()));
    }

    private void updateNoCollegesView() {
        tvNoCollegeFound.setVisibility(
                filteredCollegeList.isEmpty() ? View.VISIBLE : View.GONE
        );
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

}