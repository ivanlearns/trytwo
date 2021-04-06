package com.example.trytwoapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<String> listOfGroups;
    private View GroupChatView;

    private DatabaseReference groupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");


        InitializeFields();

        RetrieveAndDisplayGroups();

        ChatClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ChatClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        String title1 = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView
                                .findViewById(R.id.group_name)).getText().toString();
                        Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                        groupChatIntent.putExtra("mGroupName", title1);
                        startActivity(groupChatIntent);

                    }
                });

        return groupFragmentView;
    }

    public String getItem(int position, ArrayList<String> listOfGroups) {
        return listOfGroups.get(position);
    }



    private void InitializeFields() {
        listOfGroups = new ArrayList<>();
        recyclerView = groupFragmentView.findViewById(R.id.recycler_contacts);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(listOfGroups, getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    private void RetrieveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                listOfGroups.clear();
                listOfGroups.addAll(set);
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}