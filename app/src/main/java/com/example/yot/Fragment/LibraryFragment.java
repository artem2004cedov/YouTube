package com.example.yot.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.yot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LibraryFragment extends Fragment {
    private LinearLayout linearИoAccount, linearAccount;
    private DatabaseReference databaseReference;
    private FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        linearИoAccount = view.findViewById(R.id.linearИoAccount);
        linearAccount = view.findViewById(R.id.linearAccount);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            linearAccount.setVisibility(View.VISIBLE);
            linearИoAccount.setVisibility(View.GONE);
        } else {
            linearAccount.setVisibility(View.GONE);
            linearИoAccount.setVisibility(View.VISIBLE);
        }
    }
}