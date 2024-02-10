package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class UserRowResponse {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("dt")
    @Expose
    public Dt dt;
    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    public class Dt {

        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("user_name")
        @Expose
        public String userName;
        @SerializedName("profile_link")
        @Expose
        public String profileLink;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("password")
        @Expose
        public String password;
        @SerializedName("t_password")
        @Expose
        public String tPassword;
        @SerializedName("main_phone")
        @Expose
        public String mainPhone;
        @SerializedName("other_phone")
        @Expose
        public String otherPhone;
        @SerializedName("birth_date")
        @Expose
        public String birthDate;
        @SerializedName("gender")
        @Expose
        public String gender;
        @SerializedName("profile_pic")
        @Expose
        public String profilePic;
        @SerializedName("profile_banner")
        @Expose
        public String profileBanner;
        @SerializedName("bio")
        @Expose
        public String bio;
        @SerializedName("position")
        @Expose
        public String position;
        @SerializedName("company_name")
        @Expose
        public String companyName;
        @SerializedName("skill")
        @Expose
        public String skill;
        @SerializedName("language")
        @Expose
        public String language;
        @SerializedName("industry")
        @Expose
        public String industry;
        @SerializedName("country_region")
        @Expose
        public String countryRegion;
        @SerializedName("city")
        @Expose
        public String city;
        @SerializedName("is_auth")
        @Expose
        public String isAuth;
        @SerializedName("auth_by")
        @Expose
        public String authBy;
        @SerializedName("profile_type")
        @Expose
        public String profileType;
        @SerializedName("chat_status")
        @Expose
        public String chatStatus;
        @SerializedName("latitude_location")
        @Expose
        public String latitudeLocation;
        @SerializedName("longitude_location")
        @Expose
        public String longitudeLocation;
        @SerializedName("save_feeds")
        @Expose
        public String saveFeeds;
        @SerializedName("close_feeds")
        @Expose
        public String closeFeeds;
        @SerializedName("followerIds")
        @Expose
        public String followerIds;
        @SerializedName("rec_follower_req")
        @Expose
        public String recFollowerReq;
        @SerializedName("followingIds")
        @Expose
        public String followingIds;
        @SerializedName("send_following_req")
        @Expose
        public String sendFollowingReq;
        @SerializedName("connect_user")
        @Expose
        public String connectUser;
        @SerializedName("rec_connect_req")
        @Expose
        public String recConnectReq;
        @SerializedName("send_connect_req")
        @Expose
        public String sendConnectReq;
        @SerializedName("blocked_user")
        @Expose
        public String blockedUser;
        @SerializedName("profile_status")
        @Expose
        public String profileStatus;
        @SerializedName("profile_off_reason")
        @Expose
        public String profileOffReason;
        @SerializedName("platform_uses")
        @Expose
        public String platformUses;
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
        public String remoteIp;

    }
}