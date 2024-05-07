package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OurJobPostRow {
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

        @SerializedName("job_post_id")
        @Expose
        public String jobPostId;
        @SerializedName("business_page_id")
        @Expose
        public String businessPageId;
        @SerializedName("title_post")
        @Expose
        public String titlePost;
        @SerializedName("company_name")
        @Expose
        public Object companyName;
        @SerializedName("inquiry_email")
        @Expose
        public Object inquiryEmail;
        @SerializedName("job_description")
        @Expose
        public String jobDescription;
        @SerializedName("requirements")
        @Expose
        public String requirements;
        @SerializedName("no_of_vacancies")
        @Expose
        public String noOfVacancies;
        @SerializedName("skills")
        @Expose
        public String skills;
        @SerializedName("job_time")
        @Expose
        public String jobTime;
        @SerializedName("job_type")
        @Expose
        public String jobType;
        @SerializedName("street_address")
        @Expose
        public String streetAddress;
        @SerializedName("locality_city")
        @Expose
        public String localityCity;
        @SerializedName("postal_code")
        @Expose
        public String postalCode;
        @SerializedName("region")
        @Expose
        public String region;
        @SerializedName("job_location")
        @Expose
        public String jobLocation;
        @SerializedName("salary_currency")
        @Expose
        public String salaryCurrency;
        @SerializedName("salary_payroll")
        @Expose
        public String salaryPayroll;
        @SerializedName("salary")
        @Expose
        public String salary;
        @SerializedName("post_expire")
        @Expose
        public String postExpire;
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
