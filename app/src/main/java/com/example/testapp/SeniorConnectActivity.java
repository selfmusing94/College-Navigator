package com.example.testapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class SeniorConnectActivity extends AppCompatActivity implements SeniorCollegeAdapter.OnSeniorCollegeClickListener {
    private RecyclerView seniorRecyclerView;
    private SeniorCollegeAdapter seniorCollegeAdapter;
    private List<SeniorCollege> seniorColleges;
    private MaterialButton linkedinbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_connect);

        seniorRecyclerView = findViewById(R.id.seniorCollegeRecyclerView);
        seniorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize senior colleges list
        seniorColleges = getSeniorColleges(); // Updated method call

        seniorCollegeAdapter = new SeniorCollegeAdapter(this, seniorColleges, this);
        seniorRecyclerView.setAdapter(seniorCollegeAdapter);

        linkedinbutton = findViewById(R.id.linkedinRegisterButton);

        linkedinbutton.setOnClickListener(v -> {
            String clientId = getString(R.string.LINKEDIN_CLIENT_ID);
            String redirectUri = getString(R.string.LINKEDIN_REDIRECT_URI);
            String state = "random_unique_state";
            String authorizationUrl = "https://www.linkedin.com/oauth/v2/authorization" +
                    "?response_type=code" +
                    "&client_id=" + clientId +
                    "&redirect_uri=" + redirectUri +
                    "&state=" + state +
                    "&scope=r_liteprofile%20r_emailaddress";

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl));
            startActivity(browserIntent);
        });
    }

    private List<SeniorCollege> getSeniorColleges() {
        // Sample data - replace with your actual data source
        List<SeniorCollege> seniorCollegeList = new ArrayList<>();
        seniorCollegeList.add(new SeniorCollege("1", "MIT", "Cambridge, MA", "drawable/university"));
        seniorCollegeList.add(new SeniorCollege("2", "Stanford", "Stanford, CA", "drawable/university"));
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

