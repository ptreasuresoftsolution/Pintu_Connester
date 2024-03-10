package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

public class Activity_Activity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;
    LinearLayout feeds_mainList;
    ScrollView scrollView;
    FrameLayout progressBar;
    ImageView back_iv;

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
        feedsMaster.loadFeedMaster(feeds_mainList, scrollView,25);
    }
}