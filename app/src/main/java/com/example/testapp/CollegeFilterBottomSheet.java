package com.example.testapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CollegeFilterBottomSheet extends BottomSheetDialogFragment {
    // View declarations
    private Chip chipRating, chipCourses, chipLocation;
    private TextInputLayout inputLayoutRating, inputLayoutCourses, inputLayoutLocation;
    private TextInputEditText etMinRating, etCourses, etLocation;
    private ChipGroup chipGroupSelectedCourses, chipGroupSelectedLocations,chipGroupSelectedRating;
    private HorizontalScrollView scrollViewCourses, scrollViewLocations,scrollViewRating;
    private MaterialButton btnApplyFilters;

    // Data storage
    private List<String> selectedCourses = new ArrayList<>();
    private List<String> selectedLocations = new ArrayList<>();
    private String  minRating = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_college_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Setup listeners
        setupChipListeners();
        setupInputListeners();
        setupApplyFiltersListener();
    }

    private void initializeViews(View view) {
        // Rating views
        chipRating = view.findViewById(R.id.chipRating);
        inputLayoutRating = view.findViewById(R.id.inputLayoutRating);
        etMinRating = view.findViewById(R.id.etMinRating);
        chipGroupSelectedRating = view.findViewById(R.id.chipGroupSelectedRating);
        scrollViewRating=view.findViewById(R.id.scrollViewRating);

        // Courses views
        chipCourses = view.findViewById(R.id.chipCourses);
        inputLayoutCourses = view.findViewById(R.id.inputLayoutCourses);
        etCourses = view.findViewById(R.id.etCourses);
        chipGroupSelectedCourses = view.findViewById(R.id.chipGroupSelectedCourses);
        scrollViewCourses = view.findViewById(R.id.scrollViewCourses);

        // Location views
        chipLocation = view.findViewById(R.id.chipLocation);
        inputLayoutLocation = view.findViewById(R.id.inputLayoutLocation);
        etLocation = view.findViewById(R.id.etLocation);
        chipGroupSelectedLocations = view.findViewById(R.id.chipGroupSelectedLocations);
        scrollViewLocations = view.findViewById(R.id.scrollViewLocations);

        // Apply filters button
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
    }

    private void setupChipListeners() {
        // Rating chip listener
        chipRating.setOnClickListener(v -> {
            chipRating.setVisibility(View.GONE);
            inputLayoutRating.setVisibility(View.VISIBLE);
            etMinRating.requestFocus();
        });

        // Courses chip listener
        chipCourses.setOnClickListener(v -> {
            chipCourses.setVisibility(View.GONE);
            inputLayoutCourses.setVisibility(View.VISIBLE);
            etCourses.requestFocus();
        });

        // Location chip listener
        chipLocation.setOnClickListener(v -> {
            chipLocation.setVisibility(View.GONE);
            inputLayoutLocation.setVisibility(View.VISIBLE);
            etLocation.requestFocus();
        });
    }

    private void setupInputListeners() {
        // Rating input listener
        etMinRating.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addRatingChip();
                return true;
            }
            return false;
        });

        // Courses input listener
        etCourses.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCourseChip();
                return true;
            }
            return false;
        });

        // Location input listener
        etLocation.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addLocationChip();
                return true;
            }
            return false;
        });
    }

    private void addRatingChip() {
        String rating = etMinRating.getText().toString().trim();
        if(!TextUtils.isEmpty(rating)) {
            minRating = rating;
            // Remove any existing rating chip
            chipGroupSelectedRating.removeAllViews();

            Chip chip = new Chip(requireContext());
            chip.setText(rating);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                chipGroupSelectedRating.removeView(chip);
                minRating = "";
                etMinRating.setText("");
                updateRatingVisibility();
            });

            chipGroupSelectedRating.addView(chip);
            etMinRating.setText("");
            updateRatingVisibility();
        }
    }

    private void addCourseChip() {
        String course = etCourses.getText().toString().trim();
        if (!TextUtils.isEmpty(course) && !selectedCourses.contains(course)) {
            selectedCourses.add(course);

            Chip chip = new Chip(requireContext());
            chip.setText(course);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                chipGroupSelectedCourses.removeView(chip);
                selectedCourses.remove(course);
                updateCoursesVisibility();
            });

            chipGroupSelectedCourses.addView(chip);
            etCourses.setText("");
            updateCoursesVisibility();
        }
    }

    private void addLocationChip() {
        String location = etLocation.getText().toString().trim();
        if (!TextUtils.isEmpty(location) && !selectedLocations.contains(location)) {
            selectedLocations.add(location);

            Chip chip = new Chip(requireContext());
            chip.setText(location);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                chipGroupSelectedLocations.removeView(chip);
                selectedLocations.remove(location);
                updateLocationsVisibility();
            });

            chipGroupSelectedLocations.addView(chip);
            etLocation.setText("");
            updateLocationsVisibility();
        }
    }

    private void updateRatingVisibility() {
        scrollViewRating.setVisibility(minRating.isEmpty() ? View.GONE : View.VISIBLE);
        inputLayoutRating.setVisibility(View.GONE);
        chipRating.setVisibility(View.VISIBLE);
    }

    private void updateCoursesVisibility() {
        scrollViewCourses.setVisibility(selectedCourses.isEmpty() ? View.GONE : View.VISIBLE);
        inputLayoutCourses.setVisibility(View.GONE);
        chipCourses.setVisibility(View.VISIBLE);
    }

    private void updateLocationsVisibility() {
        scrollViewLocations.setVisibility(selectedLocations.isEmpty() ? View.GONE : View.VISIBLE);
        inputLayoutLocation.setVisibility(View.GONE);
        chipLocation.setVisibility(View.VISIBLE);
    }

    private void setupApplyFiltersListener() {
        btnApplyFilters.setOnClickListener(v -> {
            // Implement filter application logic
            if (getActivity() instanceof OnFilterAppliedListener) {
                ((OnFilterAppliedListener) getActivity())
                        .onFilterApplied(minRating, selectedCourses, selectedLocations);
                dismiss();
            }
        });
    }

    public interface OnFilterAppliedListener {
        void onFilterApplied(String minRating,
                             List<String> courses,
                             List<String> locations);
    }
}