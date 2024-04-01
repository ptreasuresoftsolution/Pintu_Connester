package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

import java.util.ArrayList;

public class Activity_Activity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;
    LinearLayout feeds_mainList;
    ScrollView scrollView;
    FrameLayout progressBar;
    ImageView back_iv;

    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        feeds_mainList = findViewById(R.id.feeds_mainList);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);

        feedsMaster = new FeedsMaster(context, activity);
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.setFeedForId(sessionPref.getUserMasterId());
        feedsMaster.setFeedFor("USER");
        feedsMaster.setTblName("MEDIA,POST");
        feedsMaster.loadFeedMaster(feeds_mainList, scrollView, 25);


        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                feedsMaster.setMainLinearLayoutChange(new FeedsMaster.MainLinearLayoutChange() {
                    @Override
                    public void itemAddEditChange(LinearLayout linearLayout) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
                feedsMaster.loadFeedMaster(feeds_mainList, scrollView, 25);
            }
        });
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