package com.example.testapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Make sure to add Glide dependency
import java.util.ArrayList;
import java.util.List;

public class SeniorListActivity extends AppCompatActivity {

    private TextView collegeName;
    private RecyclerView seniorsRecyclerView;
    private SeniorAdapter seniorAdapter;
    private List<Senior> seniors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.senior_list);

        collegeName = findViewById(R.id.collegeName);
        seniorsRecyclerView = findViewById(R.id.seniorsRecyclerView);

        // Get the college name from the intent
        String collegeNameStr = getIntent().getStringExtra("COLLEGE_NAME");
        Log.d("SeniorListActivity", "Received College Name: " + collegeNameStr); // Log the college name
        collegeName.setText(collegeNameStr); // Set the college name

        // Initialize the list of seniors (mock data with drawable resources)
        seniors = new ArrayList<>();
        seniors.add(new Senior("John Doe", "MIT", "Computer Science", "Batch 2023", "drawable/boy"));
        seniors.add(new Senior("Jane Smith", "Stanford", "Electrical Engineering", "Batch 2022", "drawable/boy"));
        seniors.add(new Senior("Alice Johnson", "MIT", "Mechanical Engineering", "Batch 2023", "drawable/boy"));
        seniors.add(new Senior("Bob Brown", "Stanford", "Civil Engineering", "Batch 2023", "drawable/boy"));

        // Filter seniors based on the selected college
        List<Senior> filteredSeniors = new ArrayList<>();
        for (Senior senior : seniors) {
            if (senior.getCollege().equals(collegeNameStr)) {
                filteredSeniors.add(senior);
            }
        }

        // Log the filtered seniors count for debugging
        Log.d("SeniorListActivity", "Filtered Seniors Count: " + filteredSeniors.size());

        // Set up the RecyclerView with filtered seniors
        seniorAdapter = new SeniorAdapter(this, filteredSeniors);
        seniorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        seniorsRecyclerView.setAdapter(seniorAdapter);
    }
}