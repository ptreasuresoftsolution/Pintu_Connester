package com.connester.job.module.notification_message.firebase_core;

import android.util.Log;

import androidx.annotation.NonNull;

import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final String TAG = LogTag.CHECK_DEBUG;

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    SessionPref sessionPref;
    ApiInterface apiInterface;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(LogTag.CHECK_DEBUG, "Refreshed token: " + token);

        sessionPref = new SessionPref(getApplicationContext());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if (sessionPref.isLogin()) {
            if (!sessionPref.getDEVICE_TOKEN().equalsIgnoreCase("")) {
                if (!token.equalsIgnoreCase(sessionPref.getDEVICE_TOKEN())) {
                    //call change token api
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("oldToken", sessionPref.getDEVICE_TOKEN());
                    hashMap.put("newToken", token);
                    apiInterface.CHANGE_UPDATE_TOKEN(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    Log.d(LogTag.CHECK_DEBUG, "Token is changed : " + normalCommonResponse.msg);
                                    addOrUpdateToken(token);
                                }
                            }
                        }
                    });
                }
            } else {
                //add or update token
                addOrUpdateToken(token);
            }
        }
        sessionPref.setDEVICE_TOKEN(token);
    }

    private void addOrUpdateToken(String token) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("mobile_token", token);
        hashMap.put("device_unique", CommonFunction.getDeviceId(getApplicationContext()));
        hashMap.put("device_type", "ANDROID");
        hashMap.put("device_info", CommonFunction.getDeviceName(getApplicationContext()));
        apiInterface.ADD_REGISTER_TOKEN(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "Token is Add or Register : " + normalCommonResponse.msg);
                    }
                }
            }
        });
    }
}
