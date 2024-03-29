package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationJsonData {
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {

        @SerializedName("req_from_user_master_id")
        @Expose
        public String reqFromUserMasterId;
        @SerializedName("send_connect_req")
        @Expose
        public String sendConnectReq;
        @SerializedName("rec_connect_req")
        @Expose
        public String recConnectReq;
        @SerializedName("send_following_req")
        @Expose
        public String sendFollowingReq;
        @SerializedName("rec_follower_req")
        @Expose
        public String recFollowerReq;
        @SerializedName("msg_from_user_master_id")
        @Expose
        public String msgFromUserMasterId;
        @SerializedName("send_message_user_id")
        @Expose
        public String sendMessageUserId;
        @SerializedName("chat_master_id")
        @Expose
        public String chatMasterId;
        @SerializedName("feed_master_id")
        @Expose
        public String feedMasterId;
        @SerializedName("job_post_id")
        @Expose
        public String jobPostId;

    }

    @SerializedName("notification_id")
    @Expose
    public String notificationId;
}
