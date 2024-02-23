package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;

public class MyNetworkActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_network);
        context = MyNetworkActivity.this;
        activity = MyNetworkActivity.this;
        sessionPref = new SessionPref(context);
        MaterialButton manage_nt_btn = findViewById(R.id.manage_nt_btn);
        manage_nt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show full screen dialog for list network
            }
        });

        setTopBar();
        setBottomNavBar();
    }

    private void setBottomNavBar() {
        ImageView navHome_btn = findViewById(R.id.navHome_btn),
                navNetwork_btn = findViewById(R.id.navNetwork_btn),
                navAddFeeds_btn = findViewById(R.id.navAddFeeds_btn),
                navNotification_btn = findViewById(R.id.navNotification_btn),
                navJob_btn = findViewById(R.id.navJob_btn);
        navHome_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
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
        navNetwork_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));

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
}