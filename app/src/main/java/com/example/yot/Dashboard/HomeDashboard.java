package com.example.yot.Dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yot.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeDashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeDashboard extends Fragment {



    public HomeDashboard() {
        // Required empty public constructor
    }
    public static HomeDashboard newInstance(String param1, String param2) {
        HomeDashboard fragment = new HomeDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_dashboard, container, false);
    }
}