package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationListResponse {
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

        @SerializedName("notification_id")
        @Expose
        public String notificationId;
        @SerializedName("notification_type")
        @Expose
        public String notificationType;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("json_data")
        @Expose
        public String jsonData;
        //for convert user NotificationJsonData

        @SerializedName("notification_status")
        @Expose
        public String notificationStatus;
        @SerializedName("create_date")
        @Expose
        public String createDate;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("from_user_master_id")
        @Expose
        public String fromUserMasterId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("profile_link")
        @Expose
        public String profileLink;
        @SerializedName("profile_pic")
        @Expose
        public String profilePic;
        @SerializedName("position")
        @Expose
        public String position;
        @SerializedName("send_user_master_id")
        @Expose
        public String sendUserMasterId;
        @SerializedName("chat_master_id")
        @Expose
        public String chatMasterId;
        @SerializedName("msg")
        @Expose
        public String msg;
        @SerializedName("msg_file")
        @Expose
        public String msgFile;
        @SerializedName("file_type")
        @Expose
        public String fileType;
        @SerializedName("msg_type")
        @Expose
        public String msgType;
        @SerializedName("msg_send_time")
        @Expose
        public String msgSendTime;
        @SerializedName("msg_status")
        @Expose
        public String msgStatus;
        @SerializedName("feed_master_id")
        @Expose
        public String feedMasterId;
        @SerializedName("feed_link")
        @Expose
        public String feedLink;
        @SerializedName("job_post_id")
        @Expose
        public String jobPostId;
        @SerializedName("title_post")
        @Expose
        public String titlePost;
        @SerializedName("requirements")
        @Expose
        public String requirements;
        @SerializedName("post_expire")
        @Expose
        public String postExpire;
        @SerializedName("business_page_id")
        @Expose
        public String businessPageId;
        @SerializedName("bus_name")
        @Expose
        public String busName;
        @SerializedName("logo")
        @Expose
        public String logo;
        @SerializedName("business_page_link")
        @Expose
        public String businessPageLink;

    }

    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    @SerializedName("feedImgPath")
    @Expose
    public String feedImgPath;
    @SerializedName("chatImgPath")
    @Expose
    public String chatImgPath;
}
