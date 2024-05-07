package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OurEventPostRow {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    @SerializedName("feedImgPath")
    @Expose
    public String feedImgPath;
    @SerializedName("dt")
    @Expose
    public Dt dt;

    public class Dt {

        @SerializedName("job_event_id")
        @Expose
        public String jobEventId;
        @SerializedName("business_page_id")
        @Expose
        public String businessPageId;
        @SerializedName("event_img")
        @Expose
        public String eventImg;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("event_type")
        @Expose
        public String eventType;
        @SerializedName("company_name")
        @Expose
        public Object companyName;
        @SerializedName("address_json")
        @Expose
        public String addressJson;

        public class AddressJson {
            @SerializedName("address")
            @Expose
            public String address;
            @SerializedName("city")
            @Expose
            public String city;
            @SerializedName("region_country")
            @Expose
            public String regionCountry;
        }

        @SerializedName("contact_number")
        @Expose
        public String contactNumber;
        @SerializedName("job_description")
        @Expose
        public String jobDescription;
        @SerializedName("job_requirements")
        @Expose
        public Object jobRequirements;
        @SerializedName("job_skills")
        @Expose
        public Object jobSkills;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("time_zone")
        @Expose
        public String timeZone;
        @SerializedName("inquiry_email")
        @Expose
        public Object inquiryEmail;
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
