package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupRowResponse {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("isMember")
    @Expose
    public Boolean isMember;
    @SerializedName("isRequested")
    @Expose
    public Boolean isRequested;
    @SerializedName("dt")
    @Expose
    public GroupRow groupRow;

    public class GroupRow {

        @SerializedName("community_master_id")
        @Expose
        public String communityMasterId;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("community_link")
        @Expose
        public String communityLink;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("logo")
        @Expose
        public String logo;
        @SerializedName("banner")
        @Expose
        public String banner;
        @SerializedName("bio")
        @Expose
        public String bio;
        @SerializedName("rules")
        @Expose
        public String rules;
        @SerializedName("industry")
        @Expose
        public String industry;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("group_status")
        @Expose
        public String groupStatus;
        @SerializedName("group_off_reason")
        @Expose
        public String groupOffReason;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("create_date")
        @Expose
        public String createDate;
        @SerializedName("modify_time")
        @Expose
        public String modifyTime;
        @SerializedName("remote_ip")
        @Expose
        public Object remoteIp;
        @SerializedName("members")
        @Expose
        public String members;

    }

    @SerializedName("imgPath")
    @Expose
    public String imgPath;
}
