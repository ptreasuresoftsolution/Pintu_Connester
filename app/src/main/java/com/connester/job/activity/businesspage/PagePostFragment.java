package com.connester.job.activity.businesspage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.connester.job.R;

public class PagePostFragment extends Fragment {
    ScrollView scrollView;

    public PagePostFragment(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_post, container, false);
    }
}