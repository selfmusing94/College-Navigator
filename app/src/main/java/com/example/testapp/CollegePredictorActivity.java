package com.example.testapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollegePredictorActivity extends AppCompatActivity {

    // Data Model for College
    public static class College {
        private String name;
        private String branch;
        private int rank;

        public College(String name, String branch, int rank) {
            this.name = name;
            this.branch = branch;
            this.rank = rank;
        }

        // Getters
        public String getName() { return name; }
        public String getBranch() { return branch; }
        public int getRank() { return rank; }
    }

    // RecyclerView Adapter
    public static class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.CollegeViewHolder> {
        private List<College> collegeList;

        public CollegeAdapter(List<College> collegeList) {
            this.collegeList = collegeList;
        }

        @NonNull
        @Override
        public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_college, parent, false);
            return new CollegeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CollegeViewHolder holder, int position) {
            College college = collegeList.get(position);
            holder.collegeNameTextView.setText(college.getName());
            holder.collegeBranchTextView.setText(college.getBranch());
            holder.collegeRankTextView.setText(String.valueOf(college.getRank()));
        }

        @Override
        public int getItemCount() {
            return collegeList.size();
        }

        public static class CollegeViewHolder extends RecyclerView.ViewHolder {
            TextView collegeNameTextView;
            TextView collegeBranchTextView;
            TextView collegeRankTextView;

            public CollegeViewHolder(@NonNull View itemView) {
                super(itemView);
                collegeNameTextView = itemView.findViewById(R.id.collegeNameTextView);
                collegeBranchTextView = itemView.findViewById(R.id.collegeBranchTextView);
                collegeRankTextView = itemView.findViewById(R.id.collegeRankTextView);
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

    private void predictColleges() {
        // Sample implementation - replace with your actual logic
        List<College> recommendedColleges = new ArrayList<>();

        // Mock data - replace with actual parsing from uploaded file
        recommendedColleges.add(new College("IIT Bombay", "Computer Science", 500));
        recommendedColleges.add(new College("NIT Trichy", "Mechanical Engineering", 1000));
        recommendedColleges.add(new College("IIT Delhi", "Electrical Engineering", 750));

        // Filter colleges based on user's rank
        List<College> eligibleColleges = new ArrayList<>();
        for (College college : recommendedColleges) {
            if (college.getRank() >= userRank) {
                eligibleColleges.add(college);
            }
        }

        // Show recommendation card
        recommendationCardView.setVisibility(View.VISIBLE);

        // Setup RecyclerView
        CollegeAdapter adapter = new CollegeAdapter(eligibleColleges);
        collegeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        collegeRecyclerView.setAdapter(adapter);
    }

    private int extractRankFromPdf(String filePath) {
        int rank = -1;
        try {
            File file = new File(filePath);
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();

            // Extract rank from the PDF text (customize this as needed based on PDF format)
            rank = extractRankFromText(text);  // Assume rank extraction logic is based on PDF text
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rank;
    }

    private int extractRankFromText(String text) {
        // Example: Let's assume the rank is in the format "Rank: 123"
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains("Rank:")) {
                String rankText = line.split(":")[1].trim();
                try {
                    return Integer.parseInt(rankText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;  // Return -1 if rank is not found
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

                    // Extract rank from PDF file
                    int rank = extractRankFromPdf(selectedFilePath);
                    if (rank != -1) {
                        // Set the rank input to the extracted rank
                        rankInput.setText(String.valueOf(rank));
                    } else {
                        Toast.makeText(this, "Rank not found in the uploaded file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }



}