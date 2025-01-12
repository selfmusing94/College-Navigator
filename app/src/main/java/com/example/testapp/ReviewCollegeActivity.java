package com.example.testapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewCollegeActivity extends AppCompatActivity {

    private RecyclerView reviewsListView;
    private EditText collegeNameInput;
    private EditText reviewInput;
    private MaterialButton submitReviewButton;
    private List<Review> reviews;
    private ReviewAdapter adapter;
    private String profileImageUrl;
    private  String Username;

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
    }
}