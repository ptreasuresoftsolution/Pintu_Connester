package com.connester.job.module.notification_message;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.MessageListResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserStatusUpdateResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class ChatModule {
    public static final String MSG_RECEIVED_FILTER = "MSG_RECEIVED_FILTER";
    public static final String MSG_DELIVERED_FILTER = "MSG_DELIVERED_FILTER";
    public static final String MSG_READ_FILTER = "MSG_READ_FILTER";
    public static final String CHAT_STATUS_UPDATE_FILTER = "CHAT_STATUS_UPDATE_FILTER";

    public static final long MSG_ROW_LIMIT = 10;


    public enum FileType {
        IMAGE("IMAGE"), VIDEO("VIDEO"), DOC("DOC");
        private String val;

        FileType(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    Context context;
    Activity activity;
    ApiInterface apiInterface;
    SessionPref sessionPref;
    HashMap defaultUserData = new HashMap();

    public ChatModule(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        defaultUserData.put("user_master_id", sessionPref.getUserMasterId());
        defaultUserData.put("apiKey", sessionPref.getApiKey());
        defaultUserData.put("device", "ANDROID");
    }

    public interface MessageListCallBack {
        public void callBack(MessageListResponse messageListResponse);
    }

    public void getCusMessage(String view_cus_id, long start, MessageListCallBack messageListCallBack) {

        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.putAll(defaultUserData);
        hashMap.put("sl_user_master_id", view_cus_id);
        hashMap.put("start", start);
        apiInterface.MESSAGE_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MessageListResponse messageListResponse = (MessageListResponse) response.body();
                        if (messageListResponse.status) {
                            messageListCallBack.callBack(messageListResponse);
                        } else
                            Toast.makeText(context, messageListResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void chatStatusApiCall(String status) {
        CommonFunction.PleaseWaitShow(context);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.putAll(defaultUserData);
        hashMap.put("status", status);
        apiInterface.USER_STATUS_UPDATE_CALL(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        UserStatusUpdateResponse userStatusUpdateResponse = (UserStatusUpdateResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "User status update: " + userStatusUpdateResponse.status);
                    }
                }
            }
        });
    }

    public int findIndexOf(ArrayList<MessageListResponse.Dt> tableChatDatas, String chatMasterId) {
        for (int i = 0; i < tableChatDatas.size(); i++) {
            if (tableChatDatas.get(i).chatMasterId.equalsIgnoreCase(chatMasterId)) {
                return i;
            }
        }
        return -1;
    }
}
