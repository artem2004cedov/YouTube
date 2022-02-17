package com.example.yot.Dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yot.R;

public class ChannelsDashboard extends Fragment {

    // TODO: Rename and change types and number of parameters
    public static ChannelsDashboard newInstance(String param1, String param2) {
        ChannelsDashboard fragment = new ChannelsDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channels_dashboard, container, false);
    }
}