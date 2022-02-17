package com.example.yot.Dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yot.R;

public class VideoDashboard extends Fragment {


    public VideoDashboard() {
        // Required empty public constructor
    }

    public static VideoDashboard newInstance(String param1, String param2) {
        VideoDashboard fragment = new VideoDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_dashboard, container, false);
    }
}