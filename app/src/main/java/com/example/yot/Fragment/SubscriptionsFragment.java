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


public class SubscriptionsFragment extends Fragment {
    private LinearLayout linearИoAccount;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        linearИoAccount = view.findViewById(R.id.linearИoAccount);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            linearИoAccount.setVisibility(View.GONE);
        } else {
            linearИoAccount.setVisibility(View.VISIBLE);
        }
        linearИoAccount.setVisibility(View.VISIBLE);

        return view;
    }
}