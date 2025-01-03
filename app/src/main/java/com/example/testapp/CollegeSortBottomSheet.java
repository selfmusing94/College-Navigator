package com.example.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

public class CollegeSortBottomSheet extends BottomSheetDialogFragment {
    // Interfaces
    public interface OnSortAppliedListener {
        void onSortApplied(Integer sortType, Integer sortOrder);

        void onFilterApplied(String filterType, String filterValue);
    }

    // Sort Type Constants
    public static final int SORT_BY_ESTABLISHED = 1;
    public static final int SORT_ALPHABETICAL = 2;
    public static final double SORT_BY_RATING = 3;
    public static final int SORT_BY_CUTOFF = 4;

    // Sort Order Constants
    public static final int SORT_ASCENDING = 1;
    public static final int SORT_DESCENDING = 2;

    // View Declarations
    private ChipGroup chipGroupSortType;
    private ChipGroup chipGroupSortOrder;
    private MaterialButton btnApplySort;

    // Selected Sort Variables
    private Integer selectedSortType = null;
    private Integer selectedSortOrder = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_college_sort, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        initializeViews(view);

        // Setup Listeners
        setupSortTypeListener();
        setupSortOrderListener();
        setupApplySortListener();
    }

    private void initializeViews(View view) {
        chipGroupSortType = view.findViewById(R.id.chipGroupSortType);
        chipGroupSortOrder = view.findViewById(R.id.chipGroupSortOrder);
        btnApplySort = view.findViewById(R.id.btnApplySort);
    }

    private void setupSortTypeListener() {
        chipGroupSortType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipSortByEstablished) {
                selectedSortType = SORT_BY_ESTABLISHED;
            } else if (checkedId == R.id.chipSortAlphabetical) {
                selectedSortType = SORT_ALPHABETICAL;
            } else if (checkedId == R.id.chipSortRating) {
                selectedSortType =  (int)SORT_BY_RATING;
            } else if (checkedId == R.id.chipSortCutoff) {
                selectedSortType =  SORT_BY_CUTOFF;
            } else {
                selectedSortType = null;
            }
        });
    }

    private void setupSortOrderListener() {
        chipGroupSortOrder.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipSortAscending) {
                selectedSortOrder = SORT_ASCENDING;
            } else if (checkedId == R.id.chipSortDescending) {
                selectedSortOrder = SORT_DESCENDING;
            } else {
                selectedSortOrder = null;
            }
        });
    }

    private void setupApplySortListener() {
        btnApplySort.setOnClickListener(v -> {
            // Find appropriate listener
            OnSortAppliedListener listener = findSortAppliedListener();

            if (listener != null) {
                // Apply sort even if some selections are null
                listener.onSortApplied(selectedSortType, selectedSortOrder);
                dismiss();
            } else {
                // Notify user if no listener found
                showNoListenerError();
            }
        });
    }

    private OnSortAppliedListener findSortAppliedListener() {
        // Check parent fragment first
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof OnSortAppliedListener) {
            return (OnSortAppliedListener) parentFragment;
        }

        // Check activity if no parent fragment listener
        Activity activity = getActivity();
        if (activity instanceof OnSortAppliedListener) {
            return (OnSortAppliedListener) activity;
        }

        return null;
    }

    private void showNoListenerError() {
        Toast.makeText(
                requireContext(),
                "No listener found to handle sorting",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Method to reset selections if needed
    public void resetSelections() {
        chipGroupSortType.clearCheck();
        chipGroupSortOrder.clearCheck();
        selectedSortType = null;
        selectedSortOrder = null;
    }
}