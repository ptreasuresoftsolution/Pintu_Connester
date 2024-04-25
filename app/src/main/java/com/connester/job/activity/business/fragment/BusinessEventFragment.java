package com.connester.job.activity.business.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.connester.job.R;
import com.connester.job.activity.business.BusinessActivity;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

public class BusinessEventFragment extends Fragment {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;
    LinearLayout list_ll;
    ScrollView scrollView;
    FrameLayout progressBar;
    String business_page_id;

    public BusinessEventFragment(ScrollView scrollView, String business_page_id, FrameLayout progressBar) {
        this.business_page_id = business_page_id;
        this.progressBar = progressBar;
        this.scrollView = scrollView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_event, container, false);
        context = getContext();
        activity = getActivity();
        sessionPref = new SessionPref(context);
        list_ll = view.findViewById(R.id.list_ll);

        feedsMaster = new FeedsMaster(context, activity, null);
        feedsMaster.setUserMaster(BusinessActivity.userMaster);
        feedsMaster.setProgressBar(progressBar);

        feedsMaster.setFeedForForward("BUSINESS");
        feedsMaster.setFeedForIdForward(business_page_id);

        feedsMaster.setFeedForId(business_page_id);
        feedsMaster.setFeedFor("BUSINESS");
        feedsMaster.setTblName("EVENT");
        feedsMaster.setStart(-1);

        feedsMaster.loadFeedMaster(list_ll, scrollView);

        return view;
    }

}