package com.example.yot.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yot.Activity.MainActivity;
import com.example.yot.Adapter.ContentAdapter;
import com.example.yot.Model.ContentModel;
import com.example.yot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private ImageView searchBack;
    private EditText searchEd;
    private ArrayList<ContentModel> listContent;
    private ContentAdapter contentAdapter;
    private RecyclerView recyclerSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seartch, container, false);

        searchBack = view.findViewById(R.id.searchBack);
        searchEd = view.findViewById(R.id.searchEd);
        recyclerSearch = view.findViewById(R.id.recyclerSearch);
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        listContent = new ArrayList<>();
        contentAdapter = new ContentAdapter(getContext(), listContent);
        recyclerSearch.setAdapter(contentAdapter);

        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        searchEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Content")
                .orderByChild("video_title").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listContent.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ContentModel contentModel = snapshot.getValue(ContentModel.class);
                    listContent.add(contentModel);
                }
                contentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}