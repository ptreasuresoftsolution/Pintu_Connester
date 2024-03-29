package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.connester.job.module.SetTopBottomBar;

public class JobsEvents_Activity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FrameLayout progressBar;
    SetTopBottomBar setTopBottomBar;
    ApiInterface apiInterface;
    LinearLayout main_ll;
    FeedsMaster feedsMaster;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs_and_events);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        setTopBottomBar = new SetTopBottomBar(context, activity);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        scrollView = findViewById(R.id.scrollView);
        main_ll = findViewById(R.id.main_ll);
        progressBar = findViewById(R.id.progressBar);

        feedsMaster = new FeedsMaster(context, activity);
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.callSuggestedJobsEventsFeeds(main_ll, scrollView);

        setTopBottomBar.setTopBar();
        setTopBottomBar.setBottomNavBar(SetTopBottomBar.MenuItem.navJob_btn);
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
}