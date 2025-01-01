package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class SeniorConnectActivity extends AppCompatActivity implements SeniorCollegeAdapter.OnSeniorCollegeClickListener {
    private RecyclerView seniorRecyclerView; // Renamed variable
    private SeniorAdapter seniorAdapter; // Renamed variable
    private List<SeniorCollege> seniorColleges; // Renamed variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_connect);

        seniorRecyclerView = findViewById(R.id.seniorCollegeRecyclerView);
        seniorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize senior colleges list
        seniorColleges = getSeniorColleges(); // Updated method call

        seniorAdapter = new SeniorCollegeAdapter(this, seniorColleges);
        seniorRecyclerView.setAdapter(seniorAdapter);
    }

    private List<SeniorCollege> getSeniorColleges() {
        // Sample data - replace with your actual data source
        List<SeniorCollege> seniorCollegeList = new ArrayList<>();
        seniorCollegeList.add(new SeniorCollege("1", "MIT", "Cambridge, MA", "url_to_image"));
        seniorCollegeList.add(new SeniorCollege("2", "Stanford", "Stanford, CA", "url_to_image"));
        // Add more senior colleges
        return seniorCollegeList;
    }

    @Override
    public void onSeniorCollegeClick(SeniorCollege seniorCollege) {
        Intent intent = new Intent(this, SeniorListActivity.class); // Updated target activity
        intent.putExtra("COLLEGE_ID", seniorCollege.getSeniorId()); // Updated method call
        intent.putExtra("COLLEGE_NAME", seniorCollege.getSeniorName()); // Updated method call
        startActivity(intent);
    }
}

