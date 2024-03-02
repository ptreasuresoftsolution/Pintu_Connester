package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetworkMenuListCounter {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("dt")
    @Expose
    public Dt dt;

    public class Dt {

        @SerializedName("totalConnection")
        @Expose
        public String totalConnection;
        @SerializedName("totalFollowing")
        @Expose
        public String totalFollowing;
        @SerializedName("totalFollowers")
        @Expose
        public String totalFollowers;
        @SerializedName("totalFollowReq")
        @Expose
        public String totalFollowReq;
        @SerializedName("totalInvatation")
        @Expose
        public String totalInvatation;
        @SerializedName("totalGroups")
        @Expose
        public String totalGroups;
        @SerializedName("totalPages")
        @Expose
        public String totalPages;
        @SerializedName("totalEvent")
        @Expose
        public String totalEvent;
    }
}
