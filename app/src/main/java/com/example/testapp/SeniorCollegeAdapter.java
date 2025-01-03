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

// SeniorCollegeAdapter.java
public class SeniorCollegeAdapter extends RecyclerView.Adapter<SeniorCollegeAdapter.SeniorCollegeViewHolder> {
    private List<SeniorCollege> seniorColleges;
    private Context context;
    private OnSeniorCollegeClickListener listener;

    public interface OnSeniorCollegeClickListener {
        void onSeniorCollegeClick(SeniorCollege college);
    }

    public SeniorCollegeAdapter(Context context, List<SeniorCollege> seniorColleges, OnSeniorCollegeClickListener listener) {
        this.context = context;
        this.seniorColleges = seniorColleges;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeniorCollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.senior_college_item, parent, false); // Renamed layout
        return new SeniorCollegeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeniorCollegeViewHolder holder, int position) {
        SeniorCollege college = seniorColleges.get(position); // Renamed variable
        holder.seniorCollegeName.setText(college.getSeniorName()); // Renamed variable
        holder.seniorCollegeLocation.setText(college.getSeniorLocation()); // Renamed variable

        // Load image using Glide
        Glide.with(context)
                .load(college.getSeniorImageUrl())
                .placeholder(R.drawable.university) // Renamed placeholder
                .into(holder.seniorCollegeImage); // Renamed variable

        holder.itemView.setOnClickListener(v -> listener.onSeniorCollegeClick(college)); // Renamed method
    }

    @Override
    public int getItemCount() {
        return seniorColleges.size(); // Renamed variable
    }

    static class SeniorCollegeViewHolder extends RecyclerView.ViewHolder {
        ImageView seniorCollegeImage;
        TextView seniorCollegeName;
        TextView seniorCollegeLocation;

        SeniorCollegeViewHolder(@NonNull View itemView) {
            super(itemView);
            seniorCollegeImage = itemView.findViewById(R.id.collegeImage);
            seniorCollegeName = itemView.findViewById(R.id.collegeName);
            seniorCollegeLocation = itemView.findViewById(R.id.collegeLocation);
        }
    }
}