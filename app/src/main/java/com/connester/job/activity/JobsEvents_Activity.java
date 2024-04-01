package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    SwipeRefreshLayout swipe_refresh;
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
                feedsMaster.callSuggestedJobsEventsFeeds(main_ll, scrollView);
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
}