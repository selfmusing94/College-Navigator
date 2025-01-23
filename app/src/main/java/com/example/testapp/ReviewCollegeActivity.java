package com.example.testapp;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReviewCollegeActivity extends AppCompatActivity {

    private RecyclerView reviewsListView;
    private EditText collegeNameInput;
    private EditText reviewInput;
    private MaterialButton submitReviewButton;
    private TextInputLayout batchInputLayout;
    private AutoCompleteTextView batchDropdown;
    private List<Review> reviews;
    private ReviewAdapter adapter;
    private String profileImageUrl;
    private  String Username;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_college);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser  currentUser  = mAuth.getCurrentUser ();

        reviewsListView = findViewById(R.id.reviewsRecyclerView);
        collegeNameInput = findViewById(R.id.reviewcollegeNameInput);
        reviewInput = findViewById(R.id.reviewInput);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        batchInputLayout = findViewById(R.id.batchInputLayout);
        batchDropdown = findViewById(R.id.batchDropdown);


        reviews = new ArrayList<>();
        adapter = new ReviewAdapter(this, reviews);
        reviewsListView.setLayoutManager(new LinearLayoutManager(this));
        reviewsListView.setAdapter(adapter);


        if (currentUser  != null) {
            String userId = currentUser .getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        profileImageUrl = dataSnapshot.child("profilepic").getValue(String.class);
                        Username = dataSnapshot.child("username").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ReviewCollegeActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String collegeName = collegeNameInput.getText().toString();
                String reviewText = reviewInput.getText().toString();

                if (!collegeName.isEmpty() && !reviewText.isEmpty()) {
                    Toast.makeText(ReviewCollegeActivity.this, "Review Submitted", Toast.LENGTH_SHORT).show();

                    Review newReview = new Review(Username, reviewText, collegeName, profileImageUrl);
                    reviews.add(newReview);
                    adapter.notifyDataSetChanged();
                    collegeNameInput.setText("");
                    reviewInput.setText("");
                } else {
                    Toast.makeText(ReviewCollegeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Setup date picker
        setupYearOnlyDatePicker();

        // Set click listener to open date picker
        batchDropdown.setOnClickListener(v -> showYearPicker());
    }

    private void setupYearOnlyDatePicker() {
        // Get current year
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        // Create custom DatePickerDialog
        datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Format batch year (e.g., 2023-27)
                    String batchYear = formatBatchYear(year);
                    batchDropdown.setText(batchYear);
                },
                currentYear,
                0, // Month set to 0
                1  // Day set to 1
        ) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                // Hide day and month spinners, show only year
                try {
                    // Find and hide month and day spinners
                    Field[] fields = DatePickerDialog.class.getDeclaredFields();
                    for (Field field : fields) {
                        if (field.getName().equals("mDatePicker")) {
                            field.setAccessible(true);
                            DatePicker datePicker = (DatePicker) field.get(this);

                            // Remove day and month spinners
                            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                            int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");

                            View daySpinner = datePicker.findViewById(daySpinnerId);
                            View monthSpinner = datePicker.findViewById(monthSpinnerId);

                            if (daySpinner != null) daySpinner.setVisibility(View.GONE);
                            if (monthSpinner != null) monthSpinner.setVisibility(View.GONE);

                            // Alternatively, for newer Android versions
                            datePicker.setCalendarViewShown(false);
                            datePicker.findViewById(
                                    Resources.getSystem().getIdentifier("calendar_view", "id", "android")
                            ).setVisibility(View.GONE);

                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Prevent selecting future years if needed
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private void showYearPicker() {
        datePickerDialog.show();
    }

    // Helper method to format batch year
    private String formatBatchYear(int selectedYear) {
        // Calculate end year (assuming 4-year batch)
        int endYear = selectedYear + 4;

        // Format as "YYYY-YY"
        return selectedYear + "-" + String.format("%02d", endYear % 100);
    }

    // Optional: Populate dropdown with predefined batch years
    private void populateBatchYears() {
        List<String> batchYears = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // Generate last 10 batch years
        for (int i = 0; i < 10; i++) {
            int year = currentYear - i;
            batchYears.add(formatBatchYear(year));
        }

        // Create ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                batchYears
        );

        // Set adapter to AutoCompleteTextView
        batchDropdown.setAdapter(adapter);
        batchDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBatchYear = batchYears.get(position);
            batchDropdown.setText(selectedBatchYear);
        });
    }

    // Optional: Validate batch year
    private boolean validateBatchYear() {
        String batchYear = batchDropdown.getText().toString().trim();

        if (TextUtils.isEmpty(batchYear)) {
            batchInputLayout.setError("Please select a batch year");
            return false;
        }

        // Additional validation if needed
        batchInputLayout.setError(null);
        return true;
    }
}