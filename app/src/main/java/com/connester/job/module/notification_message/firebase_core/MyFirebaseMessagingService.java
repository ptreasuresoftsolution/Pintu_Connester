package com.connester.job.module.notification_message.firebase_core;

import android.app.ActivityManager;
import android.content.Context;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sessionPref = new SessionPref(getApplicationContext());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(LogTag.CHECK_DEBUG, "Message data payload: " + remoteMessage.getData());
            Log.e(LogTag.CHECK_DEBUG, "onMessageReceived : " + remoteMessage.getData().get("data"));

            if (sessionPref.isLogin()) {
                String jsonEncStr = remoteMessage.getData().get("data");
                String pushJson = CommonFunction.base64Decode(jsonEncStr);
                try {
                    JSONObject jsonObject = new JSONObject(pushJson);
                    String type = jsonObject.getString("type");
                    if (type.equalsIgnoreCase("SEND")) {
                        //logic (you are received the message from other user) you are receiver
                        //SendMessageResponse use class

                    } else if (type.equalsIgnoreCase("DELIVER")) {
                        //logic (your message is deliver to other user) you are sender
                        //MessageStatusUpdateResponse use class

                    } else if (type.equalsIgnoreCase("READ")) {
                        //logic (your message is read a other user) you are sender
                        //MessageStatusUpdateResponse use class

                    } else if (type.equalsIgnoreCase("STATUS_UPDATE")) {
                        //logic (you are received update from other user status is change) you are sender/receiver
                        //UserStatusUpdateResponse use class
                    }
                } catch (Exception e) {
                    Log.e(LogTag.EXCEPTION, "Firebase service msg received OnException", e);
                }
            }
        }

        //extra log // ---------
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(LogTag.CHECK_DEBUG, "From: " + remoteMessage.getFrom());
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(LogTag.CHECK_DEBUG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private boolean applicationInForeground() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName
                .equalsIgnoreCase(getPackageName()) && services.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            isActivityFound = true;
        }

        return isActivityFound;
    }


    //token process update and add
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
