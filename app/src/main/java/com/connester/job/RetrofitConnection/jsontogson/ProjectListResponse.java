package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectListResponse {
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

        @SerializedName("user_project_id")
        @Expose
        public String userProjectId;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("project_name")
        @Expose
        public String projectName;
        @SerializedName("company_name")
        @Expose
        public String companyName;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("project_desc")
        @Expose
        public String projectDesc;
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
        public String remoteIp;

    }
}
