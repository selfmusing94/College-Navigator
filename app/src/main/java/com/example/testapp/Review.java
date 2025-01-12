package com.example.testapp;

public class Review {
    private String username;
    private String reviewText;
    private String collegeName;
    private String profileImageResId;

    public Review(String username, String reviewText, String collegeName, String profileImageResId) {
        this.username = username;
        this.reviewText = reviewText;
        this.collegeName = collegeName;
        this.profileImageResId = profileImageResId;
    }

    public String getUsername() {
        return username;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getProfileImageURL() {
        return profileImageResId;
    }
}