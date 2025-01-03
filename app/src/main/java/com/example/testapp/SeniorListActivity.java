package com.example.testapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
            String collegeNameStr = getIntent().getStringExtra("college_name");
            collegeName.setText(collegeNameStr);

            // Initialize the list of seniors (mock data)
            seniors = new ArrayList<>();
            seniors.add(new Senior("John Doe", "Computer Science", "Batch 2023", "https://app/src/main/res/drawable-xhdpi/boy.png"));
            seniors.add(new Senior("Jane Smith", "Electrical Engineering", "Batch 2022", "https://app/src/main/res/drawable-xhdpi/boy.png"));
            seniors.add(new Senior("Alice Johnson", "Mechanical Engineering", "Batch 2023", "https://app/src/main/res/drawable-xhdpi/boy.png"));
            // Add more seniors as needed

            // Set up the RecyclerView
            seniorAdapter = new SeniorAdapter(this, seniors);

            // Set up the RecyclerView
            seniorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            seniorsRecyclerView.setAdapter(seniorAdapter);
        }
}