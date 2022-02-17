package com.example.yot.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yot.Adapter.ContentAdapter;
import com.example.yot.Model.ContentModel;
import com.example.yot.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerHome;
    private ArrayList<ContentModel> contentModelArrayList;
    private ContentAdapter contentAdapter;
    private ProgressBar progressBarHome;

    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerHome = view.findViewById(R.id.recyclerHome);
        progressBarHome = view.findViewById(R.id.progressBarHome);
        recyclerHome.setHasFixedSize(true);
        recyclerHome.setLayoutManager(new LinearLayoutManager(getContext()));

        getAllVideos();
        return view;
    }

    private void getAllVideos() {
        contentModelArrayList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Content");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    contentModelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ContentModel model = dataSnapshot.getValue(ContentModel.class);
                        contentModelArrayList.add(model);
                    }

                    Collections.shuffle(contentModelArrayList);
                    contentAdapter = new ContentAdapter(getActivity(), contentModelArrayList);
                    recyclerHome.setAdapter(contentAdapter);
                    contentAdapter.notifyDataSetChanged();
                    progressBarHome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}