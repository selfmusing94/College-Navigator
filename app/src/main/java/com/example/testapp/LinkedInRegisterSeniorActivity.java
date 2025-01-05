package com.example.testapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class LinkedInRegisterSeniorActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private TextView nameTextView;
    private TextInputEditText collegeNameInput;
    private TextInputLayout branchInputLayout;
    private AutoCompleteTextView branchDropdown;
    private TextInputEditText customBranchInput;
    private AutoCompleteTextView startYearDropdown;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_as_senior);

        // Initialize Views
        profileImageView = findViewById(R.id.registerasseniorprofileImageView);
        nameTextView = findViewById(R.id.tvregisterasseniorname);
        collegeNameInput = findViewById(R.id.registerasseniorcollegeNameInput);
        branchInputLayout = findViewById(R.id.branchInputLayout);
        branchDropdown = findViewById(R.id.branchDropdown);
        customBranchInput = findViewById(R.id.customBranchInput);
        startYearDropdown = findViewById(R.id.startYearDropdown);
        submitButton = findViewById(R.id.submitProfileButton);

        // Retrieve LinkedIn Profile Data from Intent
        String firstName = getIntent().getStringExtra("firstName");
        String lastName = getIntent().getStringExtra("lastName");
        String profilePictureUrl = getIntent().getStringExtra("profilePictureUrl");

        // Set Name
        nameTextView.setText(String.format("%s %s", firstName, lastName));

        // Load Profile Picture
        if (profilePictureUrl != null) {
            Glide.with(this)
                    .load(profilePictureUrl)
                    .into(profileImageView);
        }

        // Setup Dropdowns
        setupBranchDropdown();
        setupStartYearDropdown();

        // Submit Button Listener
        submitButton.setOnClickListener(v -> validateAndSubmitProfile());
    }

    private void setupBranchDropdown() {
        // Predefined Branch Options with Custom Option
        String[] branches = {
                "Computer Science",
                "Mechanical Engineering",
                "Electrical Engineering",
                "Civil Engineering",
                "Electronics and Communication",
                "Information Technology",
                "Chemical Engineering",
                "Computer Engineering",
                "Aerospace Engineering",
                "Custom Branch"  // Add this as the last option
        };

        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                branches
        );
        branchDropdown.setAdapter(branchAdapter);

        // Handle branch selection
        branchDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBranch = branches[position];

            if ("Custom Branch".equals(selectedBranch)) {
                // Switch to custom branch input
                branchDropdown.setVisibility(View.GONE);
                customBranchInput.setVisibility(View.VISIBLE);
                customBranchInput.requestFocus();
            } else {
                // Ensure custom input is hidden
                customBranchInput.setVisibility(View.GONE);
                branchDropdown.setVisibility(View.VISIBLE);
            }
        });

        // Add a way to go back to dropdown from custom input
        customBranchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && customBranchInput.getText().toString().trim().isEmpty()) {
                customBranchInput.setVisibility(View.GONE);
                branchDropdown.setVisibility(View.VISIBLE);
                branchDropdown.setText(""); // Clear previous selection
            }
        });
    }

    private void setupStartYearDropdown() {
        // Generate last 10 years
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> startYears = IntStream.rangeClosed(currentYear - 10, currentYear)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());

        ArrayAdapter<String> startYearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                startYears
        );
        startYearDropdown.setAdapter(startYearAdapter);
    }

    private void validateAndSubmitProfile() {
        // Validate Inputs
        String collegeName = collegeNameInput.getText().toString().trim();
        String startYear = startYearDropdown.getText().toString().trim();

        // Determine Branch
        String branch;
        if (customBranchInput.getVisibility() == View.VISIBLE) {
            branch = customBranchInput.getText().toString().trim();
            if (branch.isEmpty()) {
                customBranchInput.setError("Custom branch is required");
                return;
            }
        } else {
            branch = branchDropdown.getText().toString().trim();
            if (branch.isEmpty() || "Custom Branch".equals(branch)) {
                branchDropdown.setError("Branch is required");
                return;
            }
        }

        // Validate College Name
        if (collegeName.isEmpty()) {
            collegeNameInput.setError("College name is required");
            return;
        }

        // Validate Start Year
        if (startYear.isEmpty()) {
            startYearDropdown.setError("Start year is required");
            return;
        }

        // Prepare Senior Data
        Map<String, Object> seniorData = new HashMap<>();
        seniorData.put("firstName", getIntent().getStringExtra("firstName"));
        seniorData.put("lastName", getIntent().getStringExtra("lastName"));
        seniorData.put("profilePictureUrl", getIntent().getStringExtra("profilePictureUrl"));
        seniorData.put("collegeName", collegeName);
        seniorData.put("branch", branch);
        seniorData.put("startYear", startYear);
        seniorData.put("isCustomBranch", customBranchInput.getVisibility() == View.VISIBLE);

        // Save to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("seniors")
                .add(seniorData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


