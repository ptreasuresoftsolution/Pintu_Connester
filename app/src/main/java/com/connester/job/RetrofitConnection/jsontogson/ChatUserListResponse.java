package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatUserListResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("dt")
    @Expose
    public List<Dt> dt;

    public class Dt {

        @SerializedName("chat_master_id")
        @Expose
        public String chatMasterId;
        @SerializedName("send_user_master_id")
        @Expose
        public String sendUserMasterId;
        @SerializedName("rec_user_master_id")
        @Expose
        public String recUserMasterId;
        @SerializedName("msg_removing_user_ids")
        @Expose
        public String msgRemovingUserIds;
        @SerializedName("msg")
        @Expose
        public String msg;
        @SerializedName("msg_file")
        @Expose
        public Object msgFile;
        @SerializedName("file_type")
        @Expose
        public String fileType;
        @SerializedName("msg_type")
        @Expose
        public String msgType;
        @SerializedName("msg_send_time")
        @Expose
        public String msgSendTime;
        @SerializedName("msg_deliver_time")
        @Expose
        public String msgDeliverTime;
        @SerializedName("msg_read_time")
        @Expose
        public String msgReadTime;
        @SerializedName("replay_msg_id")
        @Expose
        public Object replayMsgId;
        @SerializedName("msg_error")
        @Expose
        public Object msgError;
        @SerializedName("msg_status")
        @Expose
        public String msgStatus;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("create_date")
        @Expose
        public String createDate;
        @SerializedName("modify_time")
        @Expose
        public Object modifyTime;
        @SerializedName("remote_ip")
        @Expose
        public Object remoteIp;
        @SerializedName("UNREAD")
        @Expose
        public String unread;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("profile_link")
        @Expose
        public String profileLink;
        @SerializedName("profile_pic")
        @Expose
        public String profilePic;

    }

    @SerializedName("imgPath")
    @Expose
    public String imgPath;
}
