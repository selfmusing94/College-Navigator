package com.example.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

// SeniorAdapter.java
public class SeniorAdapter extends RecyclerView.Adapter<SeniorAdapter.SeniorViewHolder> {
    private List<Senior> seniorList;
    private Context context;

    public SeniorAdapter(Context context, List<Senior> seniorList) { // Renamed parameter
        this.context = context;
        this.seniorList = seniorList; // Renamed variable
    }

    @NonNull
    @Override
    public SeniorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.senior_item, parent, false);
        return new SeniorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeniorViewHolder holder, int position) {
        Senior senior = seniorList.get(position);
        holder.seniorName.setText(senior.getSeniorName());
        holder.branchName.setText(senior.getSeniorBranch());
        holder.batchYear.setText(senior.getSeniorBatch());

        // Load profile image using Glide
        Glide.with(context)
                .load(senior.getSeniorImageUrl())
                .placeholder(R.drawable.boy)
                .into(holder.seniorImage);
    }

    @Override
    public int getItemCount() {
        return seniorList.size();
    }

    static class SeniorViewHolder extends RecyclerView.ViewHolder {
        CircleImageView seniorImage;
        TextView seniorName;
        TextView branchName;
        TextView batchYear;

        SeniorViewHolder(@NonNull View itemView) {
            super(itemView);
            seniorImage = itemView.findViewById(R.id.seniorImage);
            seniorName = itemView.findViewById(R.id.seniorName);
            branchName = itemView.findViewById(R.id.branchName);
            batchYear = itemView.findViewById(R.id.batchYear);
        }
    }
}