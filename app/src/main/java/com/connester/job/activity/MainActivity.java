package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        activity = MainActivity.this;
        sessionPref = new SessionPref(context);
        redirectSettings();

        LinearLayout feeds_mainList = findViewById(R.id.feeds_mainList);
        ScrollView scrollView = findViewById(R.id.scrollView);
        feedsMaster = new FeedsMaster(context, activity);
        feedsMaster.setNeedCloseBtn(true);
        feedsMaster.loadHomeFeeds(feeds_mainList, scrollView);
//        check();
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
}