package com.example.yot.Dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yot.R;

public class AboutChannelDashboard extends Fragment {


    public AboutChannelDashboard() {
        // Required empty public constructor
    }

    public static AboutChannelDashboard newInstance(String param1, String param2) {
        AboutChannelDashboard fragment = new AboutChannelDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_channel_dashboard, container, false);
    }
}