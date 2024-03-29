package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.connester.job.RetrofitConnection.jsontogson.NotificationListResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.MyListRowSet;
import com.connester.job.function.SessionPref;

import java.util.ArrayList;
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

                ImageView notification_pic = view.findViewById(R.id.notification_pic);
                TextView datetime_tv = view.findViewById(R.id.datetime_tv);
                TextView title_tv = view.findViewById(R.id.title_tv);
                TextView subtitle_tv = view.findViewById(R.id.subtitle_tv);
                LinearLayout second_line_ll = view.findViewById(R.id.second_line_ll);

                TextView req_accept = view.findViewById(R.id.req_accept);
                TextView req_decline = view.findViewById(R.id.req_decline);
                TextView view_msg_job = view.findViewById(R.id.view_msg_job);
                TextView delete_notification = view.findViewById(R.id.delete_notification);

                if (dt.notificationType.equalsIgnoreCase(CONNECT_REQ)) {
                    View.OnClickListener openUserProfile = v -> {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra("user_master_id", dt.fromUserMasterId);
                        startActivity(intent);
                    };

                    Glide.with(context).load(imgPath + dt.profilePic).centerCrop().into(notification_pic);

                } else if (dt.notificationType.equalsIgnoreCase(FOLLOW_REQ)) {
//                    Glide.with(context).load(groupLogoFile).centerCrop().into(notification_pic);

                } else if (dt.notificationType.equalsIgnoreCase(MESSAGE)) {
//                    Glide.with(context).load(groupLogoFile).centerCrop().into(notification_pic);

                } else if (dt.notificationType.equalsIgnoreCase(RECOMMENDED_JOB)) {
//                    Glide.with(context).load(groupLogoFile).centerCrop().into(notification_pic);

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

    final static String CONNECT_REQ = "CONNECT_REQ", FOLLOW_REQ = "FOLLOW_REQ", MESSAGE = "MESSAGE", RECOMMENDED_JOB = "RECOMMENDED_JOB";
}