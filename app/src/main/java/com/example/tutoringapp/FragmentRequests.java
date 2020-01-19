package com.example.tutoringapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;


public class FragmentRequests extends Fragment {

    View view;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_requests, container, false);


        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        setupViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentPendingRequests(), "Pending");
        adapter.addFragment(new FragmentSentRequests(), "Sent");
        adapter.addFragment(new FragmentResponse(), "Responses");
        viewPager.setAdapter(adapter);
    }

}