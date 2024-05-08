package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
    String user_master_id;
    SwipeRefreshLayout swipe_refresh;
    TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        user_master_id = sessionPref.getUserMasterId();
        title_tv = findViewById(R.id.title_tv);
        if (getIntent() != null) {
            Intent gIntent = getIntent();
            if (gIntent.getStringExtra("title") != null && gIntent.getStringExtra("title") != "") {
                title_tv.setText(gIntent.getStringExtra("title"));
            }
            if (gIntent.getStringExtra("user_master_id") != null && gIntent.getStringExtra("user_master_id") != "") {
                user_master_id = gIntent.getStringExtra("user_master_id");
            }
        }

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        feeds_mainList = findViewById(R.id.feeds_mainList);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);

        feedsMaster = new FeedsMaster(context, activity, Activity_Activity.this);
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.setFeedForId(user_master_id);
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