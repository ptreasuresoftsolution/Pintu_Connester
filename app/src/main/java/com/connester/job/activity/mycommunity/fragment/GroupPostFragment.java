package com.connester.job.activity.mycommunity.fragment;

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
import com.connester.job.activity.nonslug.AddFeedsActivity;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.google.android.material.card.MaterialCardView;

public class GroupPostFragment extends Fragment {
    ScrollView scrollView;
    String communityMasterId;
    FrameLayout progressBar;
    LinearLayout main_ll;
    MaterialCardView feeds_add_ly;
    FeedsMaster feedsMaster;
    SessionPref sessionPref;

    public GroupPostFragment(ScrollView scrollView, String communityMasterId, FrameLayout progressBar) {
        this.scrollView = scrollView;
        this.communityMasterId = communityMasterId;
        this.progressBar = progressBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_post, container, false);

        main_ll = view.findViewById(R.id.main_ll);
        feeds_add_ly = view.findViewById(R.id.feeds_add_ly);
        feeds_add_ly.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddFeedsActivity.class);
            intent.putExtra("feed_for", "COMMUNITY");
            intent.putExtra("community_master_id", communityMasterId);
            startActivity(intent);
        });

        sessionPref = new SessionPref(getContext());
        feedsMaster = new FeedsMaster(getContext(), getActivity(),getActivity());
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.setFeedForForward("COMMUNITY");
        feedsMaster.setFeedForIdForward(communityMasterId);
        feedsMaster.setFeedForId(communityMasterId);
        feedsMaster.setFeedFor("COMMUNITY");
        feedsMaster.setTblName("MEDIA,POST");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (feedsMaster != null)
            feedsMaster.loadFeedMaster(main_ll, scrollView);
    }
}