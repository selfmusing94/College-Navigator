package com.example.testapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationCollegeAdapter extends RecyclerView.Adapter<LocationCollegeAdapter.ViewHolder> {
        private List<LocationCollege> collegeList;

        public LocationCollegeAdapter(List<LocationCollege> collegeList) {
            this.collegeList = collegeList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_college_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LocationCollege college = collegeList.get(position);
            holder.collegeName.setText(college.getName());
        }

        @Override
        public int getItemCount() {
            return collegeList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView collegeName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                collegeName = itemView.findViewById(R.id.tvLocationCollegeName);
            }
        }
}
