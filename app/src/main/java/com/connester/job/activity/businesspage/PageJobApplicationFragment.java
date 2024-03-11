package com.connester.job.activity.businesspage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.connester.job.R;

public class PageJobApplicationFragment extends Fragment {


    public PageJobApplicationFragment(String businessPageId, ViewPager view_pager) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page_job_application, container, false);
    }
}