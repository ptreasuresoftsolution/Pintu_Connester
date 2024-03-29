package com.connester.job.module;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.NotificationJsonData;
import com.connester.job.activity.HomeActivity;
import com.connester.job.activity.JobsEvents_Activity;
import com.connester.job.activity.NetworkActivity;
import com.connester.job.activity.NotificationActivity;
import com.connester.job.activity.UserMenuActivity;
import com.connester.job.activity.message.ChatHistoryUsersActivity;
import com.connester.job.activity.nonslug.AddFeedsActivity;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.notification_message.ChatModule;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

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
    TextView notification_dot;

    public void setBottomNavBar(MenuItem activeItem) {
        navHome_btn = activity.findViewById(R.id.navHome_btn);
        navNetwork_btn = activity.findViewById(R.id.navNetwork_btn);
        navAddFeeds_btn = activity.findViewById(R.id.navAddFeeds_btn);
        navNotification_btn = activity.findViewById(R.id.navNotification_btn);
        notification_dot = activity.findViewById(R.id.notification_dot);
        navJob_btn = activity.findViewById(R.id.navJob_btn);
        if (!activeItem.equals(MenuItem.navHome_btn))
            navHome_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, HomeActivity.class));
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
                    notification_dot.setVisibility(View.GONE);
                }
            });
        if (!activeItem.equals(MenuItem.navJob_btn))
            navJob_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, JobsEvents_Activity.class));
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

    TextView message_dot;

    public void setTopBar() {
        ImageView user_pic = activity.findViewById(R.id.user_pic);
        Glide.with(context).load(sessionPref.getUserProfilePic()).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);
        user_pic.setOnClickListener(v -> {
            context.startActivity(new Intent(context, UserMenuActivity.class));
        });

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
        message_dot = activity.findViewById(R.id.message_dot);
        open_message_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatHistoryUsersActivity.class));
                message_dot.setVisibility(View.GONE);
            }
        });
    }

    BroadcastReceiver allNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonData = intent.getExtras().getString("jsonData");
            NotificationJsonData notificationJsonData = new Gson().fromJson(jsonData, NotificationJsonData.class);
            if (notificationJsonData.notificationId != null && !notificationJsonData.notificationId.equalsIgnoreCase("")) {
                if (notification_dot != null)
                    notification_dot.setVisibility(View.VISIBLE);
            }
        }
    };
    BroadcastReceiver msgReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chat_master_id = intent.getExtras().getString("chat_master_id");
            String rec_user_master_id = intent.getExtras().getString("rec_user_master_id");
            String send_user_master_id = intent.getExtras().getString("send_user_master_id");
            String pushJsonString = intent.getExtras().getString("pushJson");//normal format
            Log.e(LogTag.CHECK_DEBUG, "Message received call Broadcast : " + chat_master_id);
            if (sessionPref.getUserMasterId().equals(rec_user_master_id)) {
                if (pushJsonString != null) {
                    if (message_dot != null)
                        message_dot.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    public void onResume() {
        IntentFilter intentFilterReadDelivered = new IntentFilter();
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_CONNECT_REQ);
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_FOLLOW_REQ);
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_MESSAGE);
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_RECOMMENDED_JOB);
        activity.registerReceiver(allNotificationReceiver, intentFilterReadDelivered);

        IntentFilter intentFilterReceived = new IntentFilter(ChatModule.MSG_RECEIVED_FILTER);
        activity.registerReceiver(msgReceived, intentFilterReceived);

        setCountingNewMessageNotification(sessionPref.getUserMasterId(), sessionPref.getApiKey());
    }

    public void onPause() {
        activity.unregisterReceiver(allNotificationReceiver);

        activity.unregisterReceiver(msgReceived);
    }

    private void setCountingNewMessageNotification(String userMasterId, String apiKey) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", userMasterId);
        hashMap.put("apiKey", apiKey);
        ApiClient.getClient().create(ApiInterface.class).NEW_MESSAGE_NOTIFICATION_COUNT(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            if (normalCommonResponse.totalMessage != null && !normalCommonResponse.totalMessage.equalsIgnoreCase("") && !normalCommonResponse.totalMessage.equalsIgnoreCase("0")) {
                                if (message_dot != null)
                                    message_dot.setVisibility(View.VISIBLE);
                            }
                            if (normalCommonResponse.totalNotification != null && !normalCommonResponse.totalNotification.equalsIgnoreCase("") && !normalCommonResponse.totalNotification.equalsIgnoreCase("0")) {
                                if (notification_dot != null)
                                    notification_dot.setVisibility(View.VISIBLE);
                            }
                        }
                        Log.d(LogTag.CHECK_DEBUG, "New Message Notification counter message : " + normalCommonResponse.msg);
                    }
                }
            }
        });
    }
}
