package com.connester.job.activity.businesspage;

import android.app.Activity;
import android.content.Context;
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

public class PageJobMangeActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;
    LinearLayout list_ll;
    ScrollView scrollView;
    FrameLayout progressBar;
    ImageView back_iv;
    String business_page_id;
    TextView create_job;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_job_mange);

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        if (getIntent() != null) {
            if (getIntent().getStringExtra("business_page_id") != null) {
                business_page_id = getIntent().getStringExtra("business_page_id");
            } else return;
        } else return;
        list_ll = findViewById(R.id.list_ll);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);

        feedsMaster = new FeedsMaster(context, activity, PageJobMangeActivity.this);
        feedsMaster.setProgressBar(progressBar);

        feedsMaster.setFeedForForward("BUSINESS");
        feedsMaster.setFeedForIdForward(business_page_id);

        feedsMaster.setFeedForId(business_page_id);
        feedsMaster.setFeedFor("BUSINESS");
        feedsMaster.setTblName("SJOB");
        feedsMaster.setStart(-1);
        feedsMaster.setChkClose(false);

        feedsMaster.setJobEventEdit(true);

        feedsMaster.loadFeedMaster(list_ll, scrollView);

        create_job = findViewById(R.id.create_job);
        create_job.setOnClickListener(v -> {
            feedsMaster.openAddJobDialog();
        });

        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                feedsMaster.setMainLinearLayoutChange(new FeedsMaster.MainLinearLayoutChange() {
                    @Override
                    public void itemAddEditChange(LinearLayout linearLayout) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing())
                            swipe_refresh.setRefreshing(false);
                    }
                });
                feedsMaster.loadFeedMaster(list_ll, scrollView);
            }
        });
    }
}