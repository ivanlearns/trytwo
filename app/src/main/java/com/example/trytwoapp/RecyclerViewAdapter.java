package com.example.trytwoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    private ArrayList<String> listOfGroups;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> listOfGroups, Context context) {
        this.listOfGroups = listOfGroups;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.groupName.setText(listOfGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return listOfGroups.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
        }


    }

    public String getItem(int position) {
        return listOfGroups.get(position);
    }
}