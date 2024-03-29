package com.connester.job.module;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class VisitMaster {
    Context context;
    Activity activity;
    ApiInterface apiInterface;
    SessionPref sessionPref;
    HashMap defaultHashMap = new HashMap();

    public VisitMaster(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        defaultHashMap.put("user_master_id", sessionPref.getUserMasterId());
        defaultHashMap.put("apiKey", sessionPref.getApiKey());
    }

    public void visitedBusinessPage(String visited_business_page_id) {
        HashMap hashMap = new HashMap();
        hashMap.put("visited_business_page_id", visited_business_page_id);
        hashMap.putAll(defaultHashMap);
        apiInterface.VISITED_BUSINESS_PAGE(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "status -> " + normalCommonResponse.status + " & visited_business_page_id :" + visited_business_page_id);
                    }
                }
            }
        });
    }

    public void visitedCommunity(String visited_community_master_id) {
        HashMap hashMap = new HashMap();
        hashMap.put("visited_community_master_id", visited_community_master_id);
        hashMap.putAll(defaultHashMap);
        apiInterface.VISITED_COMMUNITY(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "status -> " + normalCommonResponse.status + " & visited_community_master_id :" + visited_community_master_id);
                    }
                }
            }
        });
    }

    public void visitedUserProfile(String visited_profile_user_master_id) {
        HashMap hashMap = new HashMap();
        hashMap.put("visited_profile_user_master_id", visited_profile_user_master_id);
        hashMap.putAll(defaultHashMap);
        apiInterface.VISITED_USER_PROFILE(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "status -> " + normalCommonResponse.status + " & visited_profile_user_master_id :" + visited_profile_user_master_id);
                    }
                }
            }
        });
    }

    public void visitedFeedsItem(String visited_feed_master_id) {
        HashMap hashMap = new HashMap();
        hashMap.put("visited_feed_master_id", visited_feed_master_id);
        hashMap.putAll(defaultHashMap);
        apiInterface.VISITED_FEEDS_ITEM(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "status -> " + normalCommonResponse.status + " & visited_feed_master_id :" + visited_feed_master_id);
                    }
                }
            }
        });
    }

    public void impressionFeeds(String feed_master_id) {
        HashMap hashMap = new HashMap();
        hashMap.put("feed_master_id", feed_master_id);
        hashMap.putAll(defaultHashMap);
        apiInterface.IMPRESSION_FEEDS(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "status -> " + normalCommonResponse.status + " & impressionFeeds feed_master_id :" + feed_master_id);
                    }
                }
            }
        });
    }

}
