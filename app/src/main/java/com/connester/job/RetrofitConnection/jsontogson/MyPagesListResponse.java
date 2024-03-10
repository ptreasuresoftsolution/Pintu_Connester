package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyPagesListResponse {
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

        @SerializedName("members")
        @Expose
        public String members;
        @SerializedName("business_page_id")
        @Expose
        public String businessPageId;
        @SerializedName("cus_link")
        @Expose
        public String cusLink;
        @SerializedName("logo")
        @Expose
        public String logo;
        @SerializedName("banner")
        @Expose
        public String banner;
        @SerializedName("bus_name")
        @Expose
        public String busName;
        @SerializedName("bio")
        @Expose
        public String bio;

    }
    @SerializedName("imgPath")
    @Expose
    public String imgPath;
}
