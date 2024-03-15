package com.connester.job.activity.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

public class BusinessPostsFragment extends Fragment {
    ScrollView scrollView;
    String business_page_id;
    LinearLayout main_ll;
    FeedsMaster feedsMaster;
    SessionPref sessionPref;
    FrameLayout progressBar;

    public BusinessPostsFragment(ScrollView scrollView, String business_page_id, FrameLayout progressBar) {
        this.scrollView = scrollView;
        this.business_page_id = business_page_id;
        this.progressBar = progressBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_posts, container, false);

        main_ll = view.findViewById(R.id.main_ll);
        sessionPref = new SessionPref(getContext());
        feedsMaster = new FeedsMaster(getContext(), getActivity());
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.setFeedForForward("BUSINESS");
        feedsMaster.setFeedForIdForward(business_page_id);
        feedsMaster.setFeedForId(business_page_id);
        feedsMaster.setFeedFor("BUSINESS");
        feedsMaster.setTblName("MEDIA,POST");
        feedsMaster.loadFeedMaster(main_ll, scrollView);
        return view;
    }
}