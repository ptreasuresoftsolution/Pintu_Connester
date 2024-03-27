package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageStatusUpdateResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("chat_master_id")
    @Expose
    public String chatMasterId;
    @SerializedName("pushJson")
    @Expose
    public PushJson pushJson;

    public class PushJson {

        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("chatData")
        @Expose
        public ChatData chatData;

        public class ChatData {

            @SerializedName("chat_master_id")
            @Expose
            public String chatMasterId;
            @SerializedName("msg_status")
            @Expose
            public String msgStatus;
            @SerializedName("status_time")
            @Expose
            public String statusTime;

        }
    }

    @SerializedName("fcmResponse")
    @Expose
    public String fcmResponse;
    @SerializedName("msg")
    @Expose
    public String msg;
}
