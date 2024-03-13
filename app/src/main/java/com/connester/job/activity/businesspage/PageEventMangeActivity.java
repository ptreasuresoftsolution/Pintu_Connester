package com.connester.job.activity.businesspage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

public class PageEventMangeActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;
    LinearLayout list_ll;
    ScrollView scrollView;
    FrameLayout progressBar;
    ImageView back_iv;
    String business_page_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_event_mange);
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

        feedsMaster = new FeedsMaster(context, activity);
        feedsMaster.setProgressBar(progressBar);

        feedsMaster.setFeedForForward("BUSINESS");
        feedsMaster.setFeedForIdForward(business_page_id);

        feedsMaster.setFeedForId(business_page_id);
        feedsMaster.setFeedFor("BUSINESS");
        feedsMaster.setTblName("EVENT");
        feedsMaster.setStart(-1);
        feedsMaster.setChkClose(false);

        feedsMaster.setJobEventEdit(true);

        feedsMaster.loadFeedMaster(list_ll, scrollView);

        feedsMaster.activityResultLauncherForEventBanner = registerForActivityResult(new ActivityResultContracts.GetContent(), feedsMaster.activityResultCallbackForEventBanner);

        TextView create_event = findViewById(R.id.create_event);
        create_event.setOnClickListener(v -> {
            feedsMaster.openAddEventDialog();
        });
    }

}