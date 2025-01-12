package com.example.testapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Log.d("SeniorListActivity", "Received College Name: " + collegeNameStr);
        collegeName.setText(collegeNameStr); // Set the college name

        // Initialize the list of seniors (mock data with drawable resources)
        seniors = new ArrayList<>();
        seniors.add(new Senior("John Doe", "MIT", "Computer Science", "Batch 2023", "drawable/boy", "https://www.linkedin.com/in/gauravnayakk"));
        seniors.add(new Senior("Jane Smith", "Stanford", "Electrical Engineering", "Batch 2022", "drawable/boy", "https://www.linkedin.com/in/senior6"));
        seniors.add(new Senior("Alice Johnson", "MIT", "Mechanical Engineering", "Batch 2023", "drawable/boy", "https://www.linkedin.com/in/senior3"));
        seniors.add(new Senior("Bob Brown", "Stanford", "Civil Engineering", "Batch 2023", "drawable/boy", "https://www.linkedin.com/in/senior4"));
        seniors.add(new Senior("Steve Smith", "Stanford", "Artificial Intelligence", "Batch 2023", "drawable/boy", "https://www.linkedin.com/in/senior5"));

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
        seniorAdapter = new SeniorAdapter(this, filteredSeniors, this::onSeniorClick);
        seniorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        seniorsRecyclerView.setAdapter(seniorAdapter);

        // Fetch LinkedIn profile images
        //fetchLinkedInProfileImages();
    }

    public void onSeniorClick(Senior senior) {
        String linkedinUrl = senior.getLinkedInUrl();
        Log.d("LINKEDIN", "URL: " + linkedinUrl);
        if (linkedinUrl != null && !linkedinUrl.isEmpty()) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open LinkedIn profile. Please check the URL.", Toast.LENGTH_SHORT).show();
                Log.e("LINKEDIN", "Error opening URL", e);
            }
        } else {
            Toast.makeText(this, "Invalid LinkedIn profile link for " + senior.getSeniorName(), Toast.LENGTH_SHORT).show();
        }
    }

   /* // Method to fetch LinkedIn profile images
    private void fetchLinkedInProfileImages() {
        String accessToken = "LINKEDIN_ACCESS_TOKEN"; // Replace with your actual access token
        String url = "https://api.linkedin.com/v2/me?projection=(profilePicture(displayImage~:playableStreams))";

        // Create the Volley request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Parse JSON to get the profile image URL
                        String imageUrl = response.getJSONObject("profilePicture")
                                .getJSONObject("displayImage~")
                                .getJSONArray("elements")
                                .getJSONObject(0)
                                .getJSONArray("identifiers")
                                .getJSONObject(0)
                                .getString("identifier");

                        Log.d("LinkedIn", "Profile image URL: " + imageUrl);

                        // Here you can update your senior objects or UI with the image URL.
                        // Example: Update the first senior's image
                        seniors.get(0).setLinkedInPhotoUrl(imageUrl);
                        seniorAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Log.e("LinkedIn", "Error parsing profile image", e);
                    }
                },
                error -> Log.e("LinkedIn", "Error fetching profile image", error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        // Add the request to the queue
        queue.add(jsonObjectRequest);
    } */
}
