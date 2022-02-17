package com.example.yot.Adapter;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yot.Dashboard.AboutChannelDashboard;
import com.example.yot.Dashboard.ChannelsDashboard;
import com.example.yot.Dashboard.HomeDashboard;
import com.example.yot.Dashboard.PlaylistsDashboard;
import com.example.yot.Dashboard.VideoDashboard;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> strings = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeDashboard();
            case 1:
                return new VideoDashboard();
            case 2:
                return new PlaylistsDashboard();
            case 3:
                return new ChannelsDashboard();
            case 4:
                return new AboutChannelDashboard();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void add(Fragment fr, String st) {
        fragments.add(fr);
        strings.add(st);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return strings.get(position);
    }
}
