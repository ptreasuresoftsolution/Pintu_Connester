package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkExperienceListResponse {
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

        @SerializedName("user_experience_id")
        @Expose
        public String userExperienceId;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("job_title")
        @Expose
        public String jobTitle;
        @SerializedName("job_company")
        @Expose
        public String jobCompany;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("is_current_company")
        @Expose
        public String isCurrentCompany;
        @SerializedName("job_desc")
        @Expose
        public String jobDesc;
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
