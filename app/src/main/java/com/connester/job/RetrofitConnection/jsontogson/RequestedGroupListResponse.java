package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestedGroupListResponse {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("dt")
    @Expose
    public List<Dt> dt;
    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    public class Dt {

        @SerializedName("members")
        @Expose
        public String members;
        @SerializedName("community_member_master_id")
        @Expose
        public String communityMemberMasterId;
        @SerializedName("community_master_id")
        @Expose
        public String communityMasterId;
        @SerializedName("community_link")
        @Expose
        public String communityLink;
        @SerializedName("logo")
        @Expose
        public String logo;
        @SerializedName("banner")
        @Expose
        public String banner;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("bio")
        @Expose
        public String bio;

    }
}
