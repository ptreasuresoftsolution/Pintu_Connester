package com.connester.job.activity.settingActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.MembersListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class Blocking_PersonActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    ApiInterface apiInterface;
    ListView list_lt;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocking);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        ImageView back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        list_lt = findViewById(R.id.list_lt);

        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                setData();
            }
        });
        setData();
    }

    private void setData() {

        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");

        apiInterface.BLOCKED_USER_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                        MembersListResponse membersListResponse = (MembersListResponse) response.body();
                        if (membersListResponse.status) {
                            list_lt.setAdapter(getBlockedMemberAdapter(membersListResponse));
                        } else
                            Toast.makeText(context, membersListResponse.msg, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private ListAdapter getBlockedMemberAdapter(MembersListResponse membersListResponse) {
        String imgPath = membersListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return membersListResponse.dt.size();
            }

            @Override
            public MembersListResponse.Dt getItem(int position) {
                return membersListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.user_pic_two_btn_list_item, parent, false);

                MembersListResponse.Dt row = getItem(position);
                View.OnClickListener openUser = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", String.valueOf(row.userMasterId));
                    startActivity(intent);
                };

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_groups_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUser);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUser);
                MaterialButton first_mbtn = view.findViewById(R.id.first_mbtn);
                first_mbtn.setVisibility(View.GONE);

                MaterialButton second_mbtn = view.findViewById(R.id.second_mbtn);
                second_mbtn.setText("Unblock");
                second_mbtn.setOnClickListener(v1 -> {
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("id", row.userMasterId);

                    apiInterface.UN_BLOCKED_USER(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        removeItem(position);
                                    }
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                });
                return view;
            }

            private void removeItem(int position) {
                membersListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }
}