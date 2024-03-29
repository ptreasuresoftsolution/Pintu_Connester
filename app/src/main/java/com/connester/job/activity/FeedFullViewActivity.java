package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.connester.job.module.VisitMaster;

import java.util.ArrayList;

public class FeedFullViewActivity extends AppCompatActivity {
    String feed_master_id;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    FeedsMaster feedsMaster;
    LinearLayout feeds_mainList;
    ScrollView scrollView;
    FrameLayout progressBar;
    ImageView back_iv;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_full_view);

        if (getIntent() != null) {
            feed_master_id = getIntent().getStringExtra("feed_master_id");
        }
        if (feed_master_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
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
        feedsMaster.setFeedsFullView(true);
        feedsMaster.setChkClose(false);
        feedsMaster.loadSingleFeeds(feeds_mainList, scrollView, feed_master_id);
        feedsMaster.setTitleView(title);

        new VisitMaster(context, activity).visitedFeedsItem(feed_master_id);
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