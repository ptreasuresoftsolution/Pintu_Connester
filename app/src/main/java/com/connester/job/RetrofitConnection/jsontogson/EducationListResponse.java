package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EducationListResponse {
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

        @SerializedName("user_education_id")
        @Expose
        public String userEducationId;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("degree")
        @Expose
        public String degree;
        @SerializedName("school_institute_uni")
        @Expose
        public String schoolInstituteUni;
        @SerializedName("study_field")
        @Expose
        public String studyField;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("edu_desc")
        @Expose
        public String eduDesc;
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
