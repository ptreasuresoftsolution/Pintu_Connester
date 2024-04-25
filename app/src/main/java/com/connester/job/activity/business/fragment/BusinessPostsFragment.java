package com.connester.job.activity.business.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.connester.job.R;
import com.connester.job.activity.business.BusinessActivity;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

import java.util.ArrayList;

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
        feedsMaster = new FeedsMaster(getContext(), getActivity(), null);
        feedsMaster.setUserMaster(BusinessActivity.userMaster);
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.setFeedForForward("BUSINESS");
        feedsMaster.setFeedForIdForward(business_page_id);
        feedsMaster.setFeedForId(business_page_id);
        feedsMaster.setFeedFor("BUSINESS");
        feedsMaster.setTblName("MEDIA,POST");
        feedsMaster.loadFeedMaster(main_ll, scrollView);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (feedsMaster != null) {
            ArrayList<FeedsMaster.FeedStorage> feedsViews = feedsMaster.getFeedsViews();
            for (FeedsMaster.FeedStorage feedStorage : feedsViews) {
                if (feedStorage.isVideoFeeds) {
                    if (feedStorage.styledPlayerView.getTag().equals("play")) {
                        if (feedStorage.player != null && feedStorage.player.isPlaying()) {
                            feedStorage.player.pause();
                        }
                    }
                }
            }
        }
    }
}