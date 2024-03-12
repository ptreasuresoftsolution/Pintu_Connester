package com.connester.job.activity.businesspage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.connester.job.R;

public class PageAnalyticsFragment extends Fragment {
    String businessPageId;
    ViewPager view_pager;

    public PageAnalyticsFragment(String businessPageId, ViewPager view_pager) {
        this.businessPageId = businessPageId;
        this.view_pager = view_pager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_analytics, container, false);
        ViewGroup.LayoutParams params = view_pager.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view_pager.setLayoutParams(params);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ViewGroup.LayoutParams params = view_pager.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view_pager.setLayoutParams(params);
        }
    }
}