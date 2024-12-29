package com.example.testapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Context;

public class TopCollegesAdapter extends RecyclerView.Adapter<TopCollegesAdapter.CollegeViewHolder> {
    private Context context;
    private List<College> collegeList;
    private OnCollegeClickListener onCollegeClickListener;

    // Constructor
    public TopCollegesAdapter(Context context, List<College> collegeList) {
        this.context = context;
        this.collegeList = collegeList;
    }

    // Optional: Click Listener Interface
    public interface OnCollegeClickListener {
        void onCollegeClick(College college);
    }

    // Optional: Method to set click listener
    public void setOnCollegeClickListener(OnCollegeClickListener listener) {
        this.onCollegeClickListener = listener;
    }

    @NonNull
    @Override
    public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_top_colleges, parent, false);
        return new CollegeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollegeViewHolder holder, int position) {
        College college = collegeList.get(position);

        // Set data to views
        holder.tvRank.setText(String.valueOf(college.getRank()));
        holder.tvCollegeName.setText(college.getName());
        holder.tvLocation.setText(college.getLocation());
        holder.tvRating.setText(String.format("%.1f", college.getRating()));
        holder.tvEstablishedYear.setText(String.valueOf(college.getEstablishedYear()));

        // Set courses
        String coursesText = String.join(", ", college.getCourses());
        holder.tvCourses.setText(coursesText);

        // Set click listener
        if (onCollegeClickListener != null) {
            holder.itemView.setOnClickListener(v ->
                    onCollegeClickListener.onCollegeClick(college)
            );
        }
    }

    @Override
    public int getItemCount() {
        return collegeList.size();
    }

    // Update method to refresh list
    public void updateList(List<College> newList) {
        collegeList.clear();
        collegeList.addAll(newList);
        notifyDataSetChanged();
    }

    // ViewHolder Class
    static class CollegeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvCollegeName, tvLocation, tvRating, tvEstablishedYear, tvCourses;

        public CollegeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRank = itemView.findViewById(R.id.tvRank);
            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvEstablishedYear = itemView.findViewById(R.id.tvEstablishedYear);
            tvCourses = itemView.findViewById(R.id.tvCourses);
        }
    }
}