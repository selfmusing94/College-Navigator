package com.example.testapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
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




        /*linkedinbutton.setOnClickListener(v-> {
            rippleCardClick(v);
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
        });*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            // Handle the result from LinkedInLoginActivity
            // If registration is successful, disable the button
           // linkedinbutton.setEnabled(false);
        }
    }

        private List<SeniorCollege> getSeniorColleges () {
            // Sample data - replace with your actual data source
            List<SeniorCollege> seniorCollegeList = new ArrayList<>();
            seniorCollegeList.add((new SeniorCollege("1","BMSIT&M","Bengaluru,KA","drawable/university")));
            seniorCollegeList.add(new SeniorCollege("3", "MIT", "Cambridge, MA", "drawable/university"));
            seniorCollegeList.add(new SeniorCollege("2", "Stanford", "Stanford, CA", "drawable/university"));
            // Add more senior colleges
            return seniorCollegeList;
        }

        @Override
        public void onSeniorCollegeClick (SeniorCollege seniorCollege){
            Intent intent = new Intent(this, SeniorListActivity.class); // Updated target activity
            intent.putExtra("COLLEGE_ID", seniorCollege.getSeniorId()); // Updated method call
            intent.putExtra("COLLEGE_NAME", seniorCollege.getSeniorName()); // Updated method call
            startActivity(intent);
        }

        private void rippleCardClick (View view){
            // Create ripple effect
            view.setPressed(true);

            // Scale and ripple animation
            view.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        // Return to original state
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        // Release pressed state
                        view.setPressed(false);
                    })
                    .start();
        }
}


