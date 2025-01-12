package com.example.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.usernameTextView.setText(review.getUsername());
        holder.collegeNameTextView.setText(review.getCollegeName());
        holder.reviewTextView.setText(review.getReviewText());

        // Load the profile image using Glide
        Glide.with(context)
                .load(review.getProfileImageURL()) // Use the URL from the Review object
                .placeholder(R.drawable.boy ) // Placeholder image
                .into(holder.profileImageView);

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView collegeNameTextView;
        TextView reviewTextView;
        ImageView profileImageView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.reviewUsername);
            collegeNameTextView = itemView.findViewById(R.id.reviewCollegeName);
            reviewTextView = itemView.findViewById(R.id.reviewText);
            profileImageView = itemView.findViewById(R.id.reviewProfilePhoto );
        }
    }
}