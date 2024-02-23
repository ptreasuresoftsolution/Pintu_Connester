package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FeedsMaster feedsMaster;
    LinearLayout feeds_mainList;
    ScrollView scrollView;
    FrameLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        activity = MainActivity.this;
        sessionPref = new SessionPref(context);
        redirectSettings();


        feeds_mainList = findViewById(R.id.feeds_mainList);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);


        feedsMaster = new FeedsMaster(context, activity);
        feedsMaster.setNeedCloseBtn(true);
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.loadHomeFeeds(feeds_mainList, scrollView);

        setTopBar();
        setBottomNavBar();
    }

    private void setBottomNavBar() {
        ImageView navHome_btn = findViewById(R.id.navHome_btn),
                navNetwork_btn = findViewById(R.id.navNetwork_btn),
                navAddFeeds_btn = findViewById(R.id.navAddFeeds_btn),
                navNotification_btn = findViewById(R.id.navNotification_btn),
                navJob_btn = findViewById(R.id.navJob_btn);
        navNetwork_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MyNetworkActivity.class));
            }
        });
        navAddFeeds_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddFeedsActivity.class);
                intent.putExtra("feed_for", "USER");//USER/COMMUNITY/BUSINESS
                startActivity(intent);
            }
        });
        navNotification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, NotificationActivity.class));
            }
        });
        navJob_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, JobsAndEventsActivity.class));
            }
        });
        navHome_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));

    }

    private void setTopBar() {
        ImageView user_pic = findViewById(R.id.user_pic);
        Glide.with(context).load(sessionPref.getUserProfilePic()).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);

        SearchView search_master_sv = findViewById(R.id.search_master_sv);
        search_master_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView open_message_iv = findViewById(R.id.open_message_iv);
        open_message_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MessageActivity.class));
            }
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(MainActivity.this);
    }
}