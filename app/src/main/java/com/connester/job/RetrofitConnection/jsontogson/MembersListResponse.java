package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MembersListResponse {
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
}
