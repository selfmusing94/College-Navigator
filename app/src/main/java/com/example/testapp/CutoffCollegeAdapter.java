package com.example.testapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.List;

public  class CutoffCollegeAdapter extends RecyclerView.Adapter<CutoffCollegeAdapter.CutoffViewHolder> {
    private Context context;
    private List<College> collegeList;
    private TopCollegesAdapter.OnCollegeClickListener onCollegeClickListener;

    public CutoffCollegeAdapter(Context context, List<College> collegeList) {
        this.context = context;
        this.collegeList = collegeList;
    }

    public void setData(List<College> data) {
        this.collegeList = data;
    }

    public List<College> getData() {
        return collegeList;
    }

    // Optional: Click Listener Interface
    public interface OnCollegeClickListener {
        void onCollegeClick(College college);
    }

    // Optional: Method to set click listener
    public void setOnCollegeClickListener(TopCollegesAdapter.OnCollegeClickListener listener) {
        this.onCollegeClickListener = listener;
    }

    @NonNull
    @Override
    public CutoffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cutoff_college, parent, false);
        return new CutoffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CutoffViewHolder holder, int position) {
        College college = collegeList.get(position);
        Typeface customfont = ResourcesCompat.getFont(context, R.font.acme_regular);

        // Set college details
        holder.tvCollegeName.setText(college.getName());
        holder.chipCutoff.setText(String.format("Cutoff : %d", college.getCutoff()));
        holder.chipCutoff.setTypeface(customfont);
        holder.tvLocation.setText(college.getLocation());
        holder.tvrating.setText(String.valueOf(college.getRating()));

        // Color coding based on cutoff
        int cutoff = college.getCutoff();
        int backgroundColor;
        if (cutoff < 2000) {
            backgroundColor = ContextCompat.getColor(context, R.color.cutoff_low);
        } else if (cutoff < 4000) {
            backgroundColor = ContextCompat.getColor(context, R.color.cutoff_medium);
        } else {
            backgroundColor = ContextCompat.getColor(context, R.color.cutoff_high);
        }
        holder.cardView.setCardBackgroundColor(backgroundColor);

    }

    @Override
    public int getItemCount() {
        return collegeList.size();
    }

    public static class CutoffViewHolder extends RecyclerView.ViewHolder {
        TextView tvCollegeName,  tvLocation, tvrating ;
        CardView cardView;
        Chip chipCutoff;

        public CutoffViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCollegeName = itemView.findViewById(R.id.tvCollegeName);
            chipCutoff = itemView.findViewById(R.id.chipCutoff);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            cardView = itemView.findViewById(R.id.cardViewofcutoff);
            tvrating = itemView.findViewById(R.id.tvRating);
        }
    }
}
