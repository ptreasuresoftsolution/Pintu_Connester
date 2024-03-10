package com.connester.job.activity.mysaveditem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.connester.job.R;

public class EventFragment extends Fragment {
    ScrollView scrollView;
    LinearLayout feeds_event_list;
    FrameLayout progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        scrollView = view.findViewById(R.id.scrollView);
        feeds_event_list = view.findViewById(R.id.feeds_event_list);
        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }
}