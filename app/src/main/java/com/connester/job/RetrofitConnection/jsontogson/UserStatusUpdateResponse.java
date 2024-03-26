package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserStatusUpdateResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("pushJson")
    @Expose
    public PushJson pushJson;
    @SerializedName("fcmResponse")
    @Expose
    public String fcmResponse;
    @SerializedName("msg")
    @Expose
    public String msg;

    public class PushJson {

        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("chatData")
        @Expose
        public ChatData chatData;

        public class ChatData {

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("status")
            @Expose
            public String status;

        }
    }
}
