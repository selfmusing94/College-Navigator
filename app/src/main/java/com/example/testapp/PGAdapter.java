package com.example.testapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PGAdapter extends RecyclerView.Adapter<PGAdapter.ViewHolder> {
    private List<PG> pgList;

    public PGAdapter(List<PG> pgList) {
        this.pgList = pgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pg_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PG pg = pgList.get(position);
        holder.pgName.setText(pg.getName());
        holder.pgLocation.setText(pg.getLocation());
        holder.pgRoomType.setText(pg.getRoomType());
        holder.pgRent.setText(pg.getRent());
        holder.pgGenderType.setText(pg.getGender());
    }

    @Override
    public int getItemCount() {
        return pgList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pgName;
        TextView pgLocation;
        TextView pgRoomType;
        TextView pgRent;
        TextView pgGenderType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pgName = itemView.findViewById(R.id.pgName); // Ensure this ID matches your item layout
            pgLocation = itemView.findViewById(R.id.pgLocation);
            pgRoomType = itemView.findViewById(R.id.pgRoomType);
            pgRent = itemView.findViewById(R.id.pgRent);
            pgGenderType = itemView.findViewById(R.id.pgGenderType);
        }
    }
}