package com.connester.job.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.NotificationJsonData;
import com.connester.job.RetrofitConnection.jsontogson.NotificationListResponse;
import com.connester.job.activity.message.ChatHistoryUsersActivity;
import com.connester.job.function.ActionCallBack;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.DateUtils;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.MyListRowSet;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    LinearLayout main_ll;
    ScrollView scrollView;
    FrameLayout progressBar;
    ApiInterface apiInterface;
ImageView back_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        main_ll = findViewById(R.id.main_ll);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        setData();
    }

    List<NotificationListResponse.Dt> notificationList = new ArrayList<>();
    String chatImgPath, feedImgPath, imgPath;

    private void setData() {
        main_ll.removeAllViews();
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        apiInterface.NOTIFICATION_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NotificationListResponse notificationListResponse = (NotificationListResponse) response.body();
                        if (notificationListResponse.status) {
                            chatImgPath = notificationListResponse.chatImgPath;
                            imgPath = notificationListResponse.imgPath;
                            feedImgPath = notificationListResponse.feedImgPath;
                            if (notificationListResponse.dt.size() > 0) {
                                notificationList.addAll(notificationListResponse.dt);
                                SetListView();
                            }
                        } else
                            Toast.makeText(context, notificationListResponse.msg, Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void SetListView() {
        progressBar.setVisibility(View.VISIBLE);

        new MyListRowSet(main_ll, notificationList, context) {
            @Override
            public View setView(int position) {
                NotificationListResponse.Dt dt = notificationList.get(position);
                View view = getLayoutInflater().inflate(R.layout.notification_list_item, null);

                MaterialCardView main_cv = view.findViewById(R.id.main_cv);
                if (dt.notificationStatus.equalsIgnoreCase("READ"))
                    main_cv.setCardBackgroundColor(getColor(R.color.secondary_7));
                else main_cv.setCardBackgroundColor(getColor(R.color.secondary_6));

                ImageView notification_pic = view.findViewById(R.id.notification_pic);
                TextView datetime_tv = view.findViewById(R.id.datetime_tv);
                TextView title_tv = view.findViewById(R.id.title_tv);
                TextView subtitle_tv = view.findViewById(R.id.subtitle_tv);
                LinearLayout second_line_ll = view.findViewById(R.id.second_line_ll);

                TextView req_accept = view.findViewById(R.id.req_accept);
                TextView req_decline = view.findViewById(R.id.req_decline);
                TextView view_msg_job = view.findViewById(R.id.view_msg_job);
                TextView delete_notification = view.findViewById(R.id.delete_notification);

                datetime_tv.setText(FeedsMaster.feedTimeCount(dt.createDate));

                if (dt.notificationType.equalsIgnoreCase(CONNECT_REQ)) {
                    View.OnClickListener openUserProfile = v -> {
                        statusAsRead(dt.notificationId, new ActionCallBack() {
                            @Override
                            public void callBack() {
                                main_cv.setCardBackgroundColor(getColor(R.color.secondary_7));
                                Intent intent = new Intent(context, ProfileActivity.class);
                                intent.putExtra("user_master_id", dt.fromUserMasterId);
                                startActivity(intent);
                            }
                        });
                    };
                    title_tv.setText(dt.name);
                    title_tv.setOnClickListener(openUserProfile);
                    subtitle_tv.setText("send connect invitation request");
                    Glide.with(context).load(imgPath + dt.profilePic).centerCrop().into(notification_pic);
                    notification_pic.setOnClickListener(openUserProfile);

                    //action set accept && decline
                    req_accept.setVisibility(View.VISIBLE);
                    req_accept.setOnClickListener(v -> {
                        ProfileActivity.networkActionMange(new NetworkActivity.NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) main_ll.removeView(view);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, NetworkActivity.ActionName.InvReqAccept, dt.fromUserMasterId, context);
                    });
                    req_decline.setVisibility(View.VISIBLE);
                    req_decline.setOnClickListener(v -> {
                        ProfileActivity.networkActionMange(new NetworkActivity.NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) main_ll.removeView(view);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, NetworkActivity.ActionName.InvReqDecline, dt.fromUserMasterId, context);
                    });
                } else if (dt.notificationType.equalsIgnoreCase(FOLLOW_REQ)) {
                    View.OnClickListener openUserProfile = v -> {
                        statusAsRead(dt.notificationId, new ActionCallBack() {
                            @Override
                            public void callBack() {
                                main_cv.setCardBackgroundColor(getColor(R.color.secondary_7));
                                Intent intent = new Intent(context, ProfileActivity.class);
                                intent.putExtra("user_master_id", dt.fromUserMasterId);
                                startActivity(intent);
                            }
                        });
                    };
                    title_tv.setText(dt.name);
                    title_tv.setOnClickListener(openUserProfile);
                    subtitle_tv.setText("send follow request");
                    Glide.with(context).load(imgPath + dt.profilePic).centerCrop().into(notification_pic);
                    notification_pic.setOnClickListener(openUserProfile);

                    //action set accept && decline
                    req_accept.setVisibility(View.VISIBLE);
                    req_accept.setOnClickListener(v -> {
                        ProfileActivity.networkActionMange(new NetworkActivity.NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) main_ll.removeView(view);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, NetworkActivity.ActionName.FollowReqAccept, dt.fromUserMasterId, context);
                    });
                    req_decline.setVisibility(View.VISIBLE);
                    req_decline.setOnClickListener(v -> {
                        ProfileActivity.networkActionMange(new NetworkActivity.NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) main_ll.removeView(view);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, NetworkActivity.ActionName.FollowReqReject, dt.fromUserMasterId, context);
                    });
                } else if (dt.notificationType.equalsIgnoreCase(MESSAGE)) {
                    Glide.with(context).load(imgPath + dt.profilePic).centerCrop().into(notification_pic);
                    title_tv.setText(dt.name);
                    View.OnClickListener openUserProfile = v -> {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra("user_master_id", dt.sendUserMasterId);
                        startActivity(intent);
                    };
                    title_tv.setOnClickListener(openUserProfile);
                    notification_pic.setOnClickListener(openUserProfile);

                    subtitle_tv.setText("send message(Error):");
                    if (dt.msgType != null && !dt.msgType.equalsIgnoreCase("")) {
                        subtitle_tv.setText("send message: " + dt.msg);
                        if (dt.msgType.equalsIgnoreCase("FILE")) {
                            subtitle_tv.setText("send file: " + dt.fileType.toLowerCase());
                        }
                    }
                    View.OnClickListener openMessage = v -> {
                        statusAsRead(dt.notificationId, new ActionCallBack() {
                            @Override
                            public void callBack() {
                                main_cv.setCardBackgroundColor(getColor(R.color.secondary_7));
                                Intent intent = new Intent(context, ChatHistoryUsersActivity.class);
                                intent.putExtra("action", "startChat");
                                intent.putExtra("userId", dt.fromUserMasterId);
                                startActivity(intent);
                            }
                        });
                    };
                    view_msg_job.setVisibility(View.VISIBLE);
                    view_msg_job.setOnClickListener(openMessage);
                    title_tv.setOnClickListener(openMessage);
                    subtitle_tv.setOnClickListener(openMessage);
                } else if (dt.notificationType.equalsIgnoreCase(RECOMMENDED_JOB)) {
                    Glide.with(context).load(imgPath + dt.logo).centerCrop().into(notification_pic);
                    title_tv.setText(dt.titlePost);
                    Date endDate = DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", dt.postExpire);
                    String exipreString = "Job Expire on " + DateUtils.getStringDate("dd MMM", endDate);
                    if (new Date().getTime() > endDate.getTime()) {//is expire
                        exipreString = "Job is expire";
                    }
                    subtitle_tv.setText("experience: " + dt.requirements + ", Job at " + dt.busName + " (" + exipreString + ")");

                    View.OnClickListener openFullViewJob = v -> {
                        statusAsRead(dt.notificationId, new ActionCallBack() {
                            @Override
                            public void callBack() {
                                main_cv.setCardBackgroundColor(getColor(R.color.secondary_7));
                                Intent intent = new Intent(context, FeedFullViewActivity.class);
                                intent.putExtra("feed_master_id", dt.feedMasterId);
                                context.startActivity(intent);
                            }
                        });
                    };
                    view_msg_job.setVisibility(View.VISIBLE);
                    view_msg_job.setOnClickListener(openFullViewJob);
                }
                delete_notification.setOnClickListener(v -> {
                    //call api remove notification in list
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("notification_id", dt.notificationId);
                    apiInterface.NOTIFICATION_ITEM_DELETE(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        main_ll.removeView(view);
                                    } else
                                        Toast.makeText(NotificationActivity.this, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                });
                return view;
            }
        }.createView();
    }

    private void statusAsRead(String notification_id, ActionCallBack actionCallBack) {
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("notification_id", notification_id);
        hashMap.put("status", "READ");
        apiInterface.NOTIFICATION_STATUS_UPDATE(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) actionCallBack.callBack();
                        else
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    final static String CONNECT_REQ = "CONNECT_REQ", FOLLOW_REQ = "FOLLOW_REQ", MESSAGE = "MESSAGE", RECOMMENDED_JOB = "RECOMMENDED_JOB";
    public final static String BROADCAST_CONNECT_REQ = "BROADCAST_CONNECT_REQ", BROADCAST_FOLLOW_REQ = "BROADCAST_FOLLOW_REQ", BROADCAST_MESSAGE = "BROADCAST_MESSAGE", BROADCAST_RECOMMENDED_JOB = "BROADCAST_RECOMMENDED_JOB";

    BroadcastReceiver allNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonData = intent.getExtras().getString("jsonData");
            NotificationJsonData notificationJsonData = new Gson().fromJson(jsonData, NotificationJsonData.class);
            if (notificationJsonData.notificationId != null && !notificationJsonData.notificationId.equalsIgnoreCase("")){
                setData();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilterReadDelivered = new IntentFilter();
        intentFilterReadDelivered.addAction(BROADCAST_CONNECT_REQ);
        intentFilterReadDelivered.addAction(BROADCAST_FOLLOW_REQ);
        intentFilterReadDelivered.addAction(BROADCAST_MESSAGE);
        intentFilterReadDelivered.addAction(BROADCAST_RECOMMENDED_JOB);
        registerReceiver(allNotificationReceiver, intentFilterReadDelivered);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(allNotificationReceiver);
    }
}