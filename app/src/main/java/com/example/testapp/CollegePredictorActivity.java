package com.example.testapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CollegePredictorActivity extends AppCompatActivity {

    // Static Inner Adapter Class
    public static class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.CollegeViewHolder> {
        private Context context;
        private List<College> collegeList;
        private OnCollegeClickListener onCollegeClickListener;

        // Constructor
        public CollegeAdapter(Context context, List<College> collegeList) {
            this.context = Objects.requireNonNull(context, "Context cannot be null");
            this.collegeList = collegeList != null ? new ArrayList<>(collegeList) : new ArrayList<>();
        }

        // Click Listener Interface
        public interface OnCollegeClickListener {
            void onCollegeClick(College college);
        }

        // Method to set click listener
        public void setOnCollegeClickListener(OnCollegeClickListener listener) {
            this.onCollegeClickListener = listener;
        }

        @NonNull
        @Override
        public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            try {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.item_top_colleges, parent, false);
                return new CollegeViewHolder(view);
            } catch (Exception e) {
                Log.e("AdapterError", "Error creating ViewHolder", e);
                throw new RuntimeException("Failed to create ViewHolder", e);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull CollegeViewHolder holder, int position) {
            try {
                College college = collegeList.get(position);

                // Set rank (position + 1)
                holder.tvRank.setText(String.valueOf(position + 1));

                // Set college details
                holder.tvCollegeName.setText(college.getName());
                holder.tvLocation.setText(college.getLocation());
                holder.tvRating.setText(String.format("%.1f", college.getRating()));
                holder.tvEstablishedYear.setText(String.valueOf(college.getEstablishedYear()));
                holder.tvCutoff.setText(String.format("Cutoff: %d", college.getCutoff()));

                // Set courses
                String coursesText = String.join(", ", college.getCourses());
                holder.tvCourses.setText(coursesText);

                // Set click listener
                if (onCollegeClickListener != null) {
                    holder.itemView.setOnClickListener(v ->
                            onCollegeClickListener.onCollegeClick(college));
                }
            } catch (Exception e) {
                Log.e("AdapterError", "Error binding ViewHolder", e);
            }
        }

        @Override
        public int getItemCount() {
            return collegeList.size();
        }

        // Update method to refresh list
        public void updateList(List<College> newList) {
            collegeList.clear();
            collegeList.addAll(newList != null ? newList : new ArrayList<>());
            notifyDataSetChanged();
        }

        // ViewHolder Class
        public static class CollegeViewHolder extends RecyclerView.ViewHolder {
            TextView tvRank, tvCollegeName, tvLocation, tvRating,
                    tvEstablishedYear, tvCourses, tvCutoff;

            public CollegeViewHolder(@NonNull View itemView) {
                super(itemView);

                tvRank = itemView.findViewById(R.id.tvRank);
                tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvRating = itemView.findViewById(R.id.tvRating);
                tvEstablishedYear = itemView.findViewById(R.id.tvEstablishedYear);
                tvCourses = itemView.findViewById(R.id.tvCourses);
                tvCutoff = itemView.findViewById(R.id.tvGeneralCutoff);
            }
        }
    }

    // Activity Components
    private TextView fileNameText;
    private TextInputEditText rankInput;
    private MaterialButton uploadButton;
    private MaterialButton predictButton;
    private RecyclerView collegeRecyclerView;
    private CardView recommendationCardView;
    private TextInputLayout textInputLayout;

    // File and Rank Variables
    private String selectedFilePath;
    private int userRank;

    // Request Codes
    private static final int FILE_PICK_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_predictor);

        // Initialize Views
        initializeViews();

        // Setup Listeners
        setupListeners();

        // Initially hide the recommendation card
        recommendationCardView.setVisibility(View.GONE);
    }

    private void initializeViews() {
        fileNameText = findViewById(R.id.fileNameText);
        rankInput = findViewById(R.id.rankInput);
        uploadButton = findViewById(R.id.uploadButton);
        predictButton = findViewById(R.id.predictButton);
        collegeRecyclerView = findViewById(R.id.collegeRecyclerView);
        recommendationCardView = findViewById(R.id.recommendationCardView);
        textInputLayout=findViewById(R.id.RankInputLayout);
    }

    private void setupListeners() {
        // File Upload Button
        uploadButton.setOnClickListener(v -> openFilePicker());

        // Predict Button
        predictButton.setOnClickListener(v -> {
            // Validate Rank Input
            String rankStr = rankInput.getText().toString().trim();
            if (!rankStr.isEmpty()) {
                try {
                    userRank = Integer.parseInt(rankStr);
                    // Check if file is selected
                    if (selectedFilePath != null) {
                        predictColleges();
                    } else {
                        Toast.makeText(this, "Please upload a file first", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid Rank", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter your rank", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types
        startActivityForResult(Intent.createChooser(intent, "Select File"), FILE_PICK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    selectedFilePath = fileUri.getPath();
                    fileNameText.setText(selectedFilePath);
                }
            }
        }
    }

    private void predictColleges() {
        // Sample implementation - replace with your actual logic
        List<College> recommendedColleges = new ArrayList<>();

        // Mock data - replace with actual parsing from uploaded file
        recommendedColleges.add(new College("Massachusetts Institute of Technology (MIT)", "Cambridge, MA", 9.5, 1861, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 519));
        recommendedColleges.add(new College("Stanford University", "Stanford, CA", 9.3, 1885, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 834));
        recommendedColleges.add(new College("Harvard University", "Cambridge, MA", 9.7, 1636, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 215));
        recommendedColleges.add(new College("California Institute of Technology (Caltech)", "Pasadena, CA", 9.2, 1891, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 265));
        recommendedColleges.add(new College("University of Chicago", "Chicago, IL", 9.0, 1890, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 918));
        recommendedColleges.add(new College("Columbia University", "New York, NY", 8.9, 1754, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "CSBS"), 186));
        recommendedColleges.add(new College("Princeton University", "Princeton, NJ", 9.1, 1746, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIML"), 1819));
        recommendedColleges.add(new College("Yale University", "New Haven, CT", 9.0, 1701, Arrays.asList("Mechanical", "Civil", "ECE", "CSE", "AIDS"), 926));

        try {
            if (userRank <= 0 ) {
                textInputLayout.setError("Enter a Valid Rank");
                recommendationCardView.setVisibility(View.GONE);
                return;
            } else {
                textInputLayout.setError(null);
            }
        } catch (NumberFormatException e) {
            textInputLayout.setError("Invalid number");
        }
        // Filter colleges based on user's rank
        List<College> eligibleColleges = new ArrayList<>();
        for (College college : recommendedColleges) {
            if (college.getCutoff() >= userRank && userRank>0) {
                eligibleColleges.add(college);
            }
        }

        // Sort eligible colleges by cutoff (closest to user's rank)
        eligibleColleges.sort((c1, c2) ->
                Integer.compare(Math.abs(c1.getCutoff() - userRank),
                        Math.abs(c2.getCutoff() - userRank))
        );

        // Show recommendation card
        recommendationCardView.setVisibility(View.VISIBLE);

        // Setup RecyclerView
        CollegeAdapter adapter = new CollegeAdapter(this, eligibleColleges);

        // Optional: Add click listener
        adapter.setOnCollegeClickListener(college -> {
            // Handle college click - e.g., open college details
            Toast.makeText(this, "Selected " + college.getName(), Toast.LENGTH_SHORT).show();
        });

        collegeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collegeRecyclerView.setAdapter(adapter);

        // Update UI with results
        if (eligibleColleges.isEmpty()) {
            Toast.makeText(this, "No colleges found matching your rank", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, eligibleColleges.size() + " colleges found", Toast.LENGTH_SHORT).show();
        }
    }

    // Optional: Add method to reset prediction
    private void resetPrediction() {
        selectedFilePath = null;
        userRank = 0;
        fileNameText.setText("No file selected");
        rankInput.setText("");
        recommendationCardView.setVisibility(View.GONE);
    }

    // Optional: Add method to handle file parsing (to be implemented)
    private List<College> parseUploadedFile(String filePath) {
        // Implement file parsing logic
        // This could be CSV, Excel, or any other format parsing
        List<College> parsedColleges = new ArrayList<>();

        // Placeholder - replace with actual parsing
        try {
            // Your file parsing logic here
            // Example: Read file, extract college information
        } catch (Exception e) {
            Log.e("FileParser", "Error parsing file", e);
            Toast.makeText(this, "Error parsing file", Toast.LENGTH_SHORT).show();
        }

        return parsedColleges;
    }
}