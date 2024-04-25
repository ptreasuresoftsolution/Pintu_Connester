package com.connester.job.activity.businesspage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.connester.job.R;
import com.connester.job.activity.businesspage.ManageMyPageActivity;
import com.connester.job.activity.nonslug.AddFeedsActivity;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.google.android.material.card.MaterialCardView;

public class PagePostFragment extends Fragment {
    ScrollView scrollView;
    String business_page_id;
    LinearLayout main_ll;
    MaterialCardView feeds_add_ly;
    FeedsMaster feedsMaster;
    SessionPref sessionPref;
    FrameLayout progressBar;

    public PagePostFragment(ScrollView scrollView, String business_page_id, FrameLayout progressBar) {
        this.scrollView = scrollView;
        this.business_page_id = business_page_id;
        this.progressBar = progressBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_post, container, false);
        main_ll = view.findViewById(R.id.main_ll);
        feeds_add_ly = view.findViewById(R.id.feeds_add_ly);
        feeds_add_ly.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddFeedsActivity.class);
            intent.putExtra("feed_for", "BUSINESS");
            intent.putExtra("business_page_id", business_page_id);
            startActivity(intent);
        });

        sessionPref = new SessionPref(getContext());
        feedsMaster = new FeedsMaster(getContext(), getActivity(), null);
        feedsMaster.setUserMaster(ManageMyPageActivity.userMaster);
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