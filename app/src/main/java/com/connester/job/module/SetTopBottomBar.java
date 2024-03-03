package com.connester.job.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.activity.AddFeedsActivity;
import com.connester.job.activity.JobsAndEventsActivity;
import com.connester.job.activity.MainActivity;
import com.connester.job.activity.MessageActivity;
import com.connester.job.activity.NetworkActivity;
import com.connester.job.activity.NotificationActivity;
import com.connester.job.function.SessionPref;

public class SetTopBottomBar {
    Context context;
    Activity activity;
    SessionPref sessionPref;

    public SetTopBottomBar(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
    }

    private ImageView navHome_btn, navNetwork_btn, navAddFeeds_btn, navNotification_btn, navJob_btn;

    public void setBottomNavBar(MenuItem activeItem) {
        navHome_btn = activity.findViewById(R.id.navHome_btn);
        navNetwork_btn = activity.findViewById(R.id.navNetwork_btn);
        navAddFeeds_btn = activity.findViewById(R.id.navAddFeeds_btn);
        navNotification_btn = activity.findViewById(R.id.navNotification_btn);
        navJob_btn = activity.findViewById(R.id.navJob_btn);
        if (!activeItem.equals(MenuItem.navHome_btn))
            navHome_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            });
        if (!activeItem.equals(MenuItem.navNetwork_btn))
            navNetwork_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, NetworkActivity.class));
                }
            });
        if (!activeItem.equals(MenuItem.navAddFeeds_btn))
            navAddFeeds_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddFeedsActivity.class);
                    intent.putExtra("feed_for", "USER");//USER/COMMUNITY/BUSINESS
                    context.startActivity(intent);
                }
            });
        if (!activeItem.equals(MenuItem.navNotification_btn))
            navNotification_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, NotificationActivity.class));
                }
            });
        if (!activeItem.equals(MenuItem.navJob_btn))
            navJob_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, JobsAndEventsActivity.class));
                }
            });

        setBottomActiveItem(activeItem);
    }

    public enum MenuItem {navHome_btn, navNetwork_btn, navAddFeeds_btn, navNotification_btn, navJob_btn}

    public void setBottomActiveItem(MenuItem itemNm) {
        if (itemNm.equals(MenuItem.navHome_btn)) {
            navHome_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navNetwork_btn)) {
            navNetwork_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navAddFeeds_btn)) {
            navAddFeeds_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navNotification_btn)) {
            navNotification_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navJob_btn)) {
            navJob_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
    }

    public void setTopBar() {
        ImageView user_pic = activity.findViewById(R.id.user_pic);
        Glide.with(context).load(sessionPref.getUserProfilePic()).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);

        SearchView search_master_sv = activity.findViewById(R.id.search_master_sv);
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

        ImageView open_message_iv = activity.findViewById(R.id.open_message_iv);
        open_message_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MessageActivity.class));
            }
        });
    }
}
