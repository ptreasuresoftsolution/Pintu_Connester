package com.connester.job.module.notification_message.firebase_core;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.FirebaseFCMResponse;
import com.connester.job.RetrofitConnection.jsontogson.MessageStatusUpdateResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.SendMessageResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserStatusUpdateResponse;
import com.connester.job.activity.NotificationActivity;
import com.connester.job.activity.message.ChatActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.notification_message.ChatModule;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

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
                String pushJsonString = CommonFunction.base64Decode(jsonEncStr);
                try {
                    JSONObject jsonObject = new JSONObject(pushJsonString);
                    String type = jsonObject.getString("type");
                    //message type data received
                    if (type.equalsIgnoreCase("SEND")) {
                        //logic (you are received the message from other user) you are receiver
                        //SendMessageResponse use class
                        SendMessageResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, SendMessageResponse.PushJson.class);
                        if (pushJson.chatData.recUserMasterId.equalsIgnoreCase(sessionPref.getUserMasterId())) {
                            Intent intent = new Intent(ChatModule.MSG_RECEIVED_FILTER);
                            intent.putExtra("chat_master_id", pushJson.chatData.chatMasterId);
                            intent.putExtra("rec_user_master_id", pushJson.chatData.recUserMasterId);
                            intent.putExtra("send_user_master_id", pushJson.chatData.sendUserMasterId);
                            intent.putExtra("pushJson", pushJsonString);
                            sendBroadcast(intent);

                            if (!applicationInForeground()) {
                                String message = "File (Photos,Video)";
                                if (!pushJson.chatData.msg.equalsIgnoreCase("") && pushJson.chatData.msgType.equalsIgnoreCase("TEXT")) {
                                    message = pushJson.chatData.msg;
                                }
                                sendNotification(String.valueOf(pushJson.chatData.sendUserMasterId), pushJson.sendUser.name, message);
                                addMessageInNotificationList(pushJson.chatData.sendUserMasterId, pushJson.chatData.chatMasterId, sessionPref.getUserMasterId(), sessionPref.getApiKey());
                                Log.e(LogTag.TMP_LOG, "Notify message notification");
                            }

                            //call message deliver api
                            callMessageDeliverApi(pushJson.chatData.chatMasterId, sessionPref.getUserMasterId(), sessionPref.getApiKey());
                        }
                    } else if (type.equalsIgnoreCase("DELIVER")) {
                        //logic (your message is deliver to other user) you are sender
                        //MessageStatusUpdateResponse use class
                        MessageStatusUpdateResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, MessageStatusUpdateResponse.PushJson.class);

                        Intent intent = new Intent(ChatModule.MSG_DELIVERED_FILTER);
                        intent.putExtra("chat_master_id", pushJson.chatData.chatMasterId);
                        intent.putExtra("pushJson", pushJsonString);
                        sendBroadcast(intent);
                        Log.e(LogTag.CHECK_DEBUG, "message delivered id " + pushJson.chatData.chatMasterId);
                    } else if (type.equalsIgnoreCase("READ")) {
                        //logic (your message is read a other user) you are sender
                        //MessageStatusUpdateResponse use class
                        MessageStatusUpdateResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, MessageStatusUpdateResponse.PushJson.class);

                        Intent intent = new Intent(ChatModule.MSG_READ_FILTER);
                        intent.putExtra("chat_master_id", pushJson.chatData.chatMasterId);
                        intent.putExtra("pushJson", pushJsonString);
                        sendBroadcast(intent);
                        Log.e(LogTag.CHECK_DEBUG, "message read id " + pushJson.chatData.chatMasterId);
                    } else if (type.equalsIgnoreCase("STATUS_UPDATE")) {
                        //logic (you are received update from other user status is change) you are sender/receiver
                        //UserStatusUpdateResponse use class
                        UserStatusUpdateResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, UserStatusUpdateResponse.PushJson.class);

                        Intent intent = new Intent(ChatModule.CHAT_STATUS_UPDATE_FILTER);
                        intent.putExtra("user_master_id", pushJson.chatData.userMasterId);
                        intent.putExtra("pushJson", pushJsonString);
                        sendBroadcast(intent);
                        Log.e(LogTag.CHECK_DEBUG, "User-Status-Change id " + pushJson.chatData.userMasterId);
                    }
                    //notification data received
                    else if (type.equalsIgnoreCase("CONNECT_REQ")) {
//                        NotificationJsonData notificationJsonData = new Gson().fromJson(pushJsonString, NotificationJsonData.class);
                        Intent intent = new Intent(NotificationActivity.BROADCAST_CONNECT_REQ);
                        intent.putExtra("jsonData", pushJsonString);
                        sendBroadcast(intent);
                    } else if (type.equalsIgnoreCase("FOLLOW_REQ")) {
                        Intent intent = new Intent(NotificationActivity.BROADCAST_FOLLOW_REQ);
                        intent.putExtra("jsonData", pushJsonString);
                        sendBroadcast(intent);
                    } else if (type.equalsIgnoreCase("MESSAGE")) {
                        Intent intent = new Intent(NotificationActivity.BROADCAST_MESSAGE);
                        intent.putExtra("jsonData", pushJsonString);
                        sendBroadcast(intent);
                    } else if (type.equalsIgnoreCase("RECOMMENDED_JOB")) {
                        Intent intent = new Intent(NotificationActivity.BROADCAST_RECOMMENDED_JOB);
                        intent.putExtra("jsonData", pushJsonString);
                        sendBroadcast(intent);
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

    private void sendNotification(String user_master_id, String title, String message) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user_master_id", user_master_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, user_master_id)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(user_master_id, "Connester Message", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(Integer.parseInt(user_master_id), notificationBuilder.build());
        notificationBuilder.setAutoCancel(true);
    }


    private void callMessageDeliverApi(String chatMasterId, String userMasterId, String apiKey) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", userMasterId);
        hashMap.put("apiKey", apiKey);
        hashMap.put("chat_master_id", chatMasterId);

        apiInterface.MESSAGE_STATUS_DELIVERY(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MessageStatusUpdateResponse messageStatusUpdateResponse = (MessageStatusUpdateResponse) response.body();
                        FirebaseFCMResponse firebaseFCMResponse = new Gson().fromJson(messageStatusUpdateResponse.fcmResponse, FirebaseFCMResponse.class);
                        if (messageStatusUpdateResponse.status) {
                            Log.e(LogTag.CHECK_DEBUG, "Delivery status update id " + chatMasterId);
                        }
                    }
                }
            }
        });
    }

    private void addMessageInNotificationList(String sendUserMasterId, String chatMasterId, String userMasterId, String apiKey) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", userMasterId);
        hashMap.put("apiKey", apiKey);
        hashMap.put("chat_master_id", chatMasterId);
        hashMap.put("send_user_master_id", sendUserMasterId);
        apiInterface.NOTIFICATION_IN_ADD_TYPE_MESSAGE(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "Add Notification type message Status: " + normalCommonResponse.status);
                    }
                }
            }
        });
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
