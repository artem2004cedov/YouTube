package com.example.yot.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yot.Adapter.ViewPagerAdapter;
import com.example.yot.Dashboard.AboutChannelDashboard;
import com.example.yot.Dashboard.ChannelsDashboard;
import com.example.yot.Dashboard.HomeDashboard;
import com.example.yot.Dashboard.PlaylistsDashboard;
import com.example.yot.Dashboard.VideoDashboard;
import com.example.yot.Activity.MainActivity;
import com.example.yot.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChannelDashboardFragment extends Fragment {

    private TextView channelNameDas;
    private ImageView backChannelDas;
    private ViewPager viewPagerChannelDas;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayoutDas;


    public ChannelDashboardFragment() {
        // Required empty public constructor
    }

    public static ChannelDashboardFragment newInstance() {
        ChannelDashboardFragment fragment = new ChannelDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_dashboard, container, false);

        channelNameDas = view.findViewById(R.id.channelNameDas);
        backChannelDas = view.findViewById(R.id.backChannelDas);
        viewPagerChannelDas = view.findViewById(R.id.viewPagerChannelDas);
        tabLayoutDas = view.findViewById(R.id.tabLayoutDas);

        initAdapter();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Channels");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("channel_name").getValue().toString();
                    channelNameDas.setText(name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backChannelDas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        return view;
    }

    private void initAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.add(new HomeDashboard(), "Главная");
        viewPagerAdapter.add(new VideoDashboard(), "Видео");
        viewPagerAdapter.add(new PlaylistsDashboard(), "ПлейЛисты");
        viewPagerAdapter.add(new ChannelsDashboard(), "Каналы");
        viewPagerAdapter.add(new AboutChannelDashboard(), "О канале");

        viewPagerChannelDas.setAdapter(viewPagerAdapter);
        tabLayoutDas.setupWithViewPager(viewPagerChannelDas);
    }
}