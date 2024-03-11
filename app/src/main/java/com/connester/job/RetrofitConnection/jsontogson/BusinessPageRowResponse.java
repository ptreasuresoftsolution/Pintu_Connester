package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessPageRowResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;

    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    @SerializedName("dt")
    @Expose
    public BusinessPageRow businessPageRow;
    public class BusinessPageRow {

        @SerializedName("business_page_id")
        @Expose
        public String businessPageId;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("bus_name")
        @Expose
        public String busName;
        @SerializedName("cus_link")
        @Expose
        public String cusLink;
        @SerializedName("website")
        @Expose
        public String website;
        @SerializedName("industry")
        @Expose
        public String industry;
        @SerializedName("org_size")
        @Expose
        public String orgSize;
        @SerializedName("org_type")
        @Expose
        public String orgType;
        @SerializedName("logo")
        @Expose
        public String logo;
        @SerializedName("bio")
        @Expose
        public String bio;
        @SerializedName("banner")
        @Expose
        public String banner;
        @SerializedName("founded_year")
        @Expose
        public String foundedYear;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("address_json")
        @Expose
        public String addressJson;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("skills")
        @Expose
        public String skills;
        @SerializedName("page_status")
        @Expose
        public String pageStatus;
        @SerializedName("page_off_reason")
        @Expose
        public String pageOffReason;
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
        @SerializedName("members")
        @Expose
        public String members;

    }
}
