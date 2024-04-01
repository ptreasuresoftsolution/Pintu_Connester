package com.connester.job.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.connester.job.module.SetTopBottomBar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;
    LinearLayout feeds_mainList;
    ScrollView scrollView;
    FrameLayout progressBar;
    SetTopBottomBar setTopBottomBar;
    MaterialButton open_jobs;

    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = HomeActivity.this;
        activity = HomeActivity.this;
        sessionPref = new SessionPref(context);
        setTopBottomBar = new SetTopBottomBar(context, activity);
        redirectSettings();


        feeds_mainList = findViewById(R.id.feeds_mainList);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        open_jobs = findViewById(R.id.open_jobs);
        open_jobs.setOnClickListener(v -> {
            context.startActivity(new Intent(context, JobsEvents_Activity.class));
        });

        feedsMaster = new FeedsMaster(context, activity);
        feedsMaster.setChkClose(true);
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.setFeedForForward("USER");
        feedsMaster.loadHomeFeeds(feeds_mainList, scrollView);

        setTopBottomBar.setTopBar();
        setTopBottomBar.setBottomNavBar(SetTopBottomBar.MenuItem.navHome_btn);


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
                feedsMaster.loadHomeFeeds(feeds_mainList, scrollView);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBottomBar.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setTopBottomBar.onPause();
    }

    public void check() {

        for (int i = 0; i < 7; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.feeds_photos_layout, null);
            TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
            feeds_content_txt.setText("index " + i);
            feeds_mainList.addView(view, i);
        }

        //remove item in middle
        feeds_mainList.removeViewAt(3);

        if (feeds_mainList.getChildAt(4) != null) {
            View view = feeds_mainList.getChildAt(4);
            TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
            feeds_content_txt.setText("index CHECK");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    private void redirectSettings() {
        Intent intent = getIntent();
        if (sessionPref.getUserName().isEmpty() || sessionPref.getUserName().equalsIgnoreCase("")) {//check setupOne
            startActivity(new Intent(context, StepActivity.class));
            finish();
        }
        if (!sessionPref.isLogin()) { // check is notLogin
            startActivity(new Intent(context, SignInActivity.class));
            finish();
        }
        //redirect triggers Click from notification
        if (intent != null) {
            if (intent.getStringExtra("trigger") != null &&
                    intent.getStringExtra("trigger").equalsIgnoreCase("EditProfileActivity")) {
                startActivity(new Intent(context, EditProfileActivity.class));
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("Are you sure to exit?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HomeActivity.super.onBackPressed();
                ActivityCompat.finishAffinity(HomeActivity.this);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}