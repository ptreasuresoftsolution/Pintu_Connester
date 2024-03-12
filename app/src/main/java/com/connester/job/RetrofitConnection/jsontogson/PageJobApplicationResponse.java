package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageJobApplicationResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("json")
    @Expose
    public Json json;
    public class Json {

        @SerializedName("activeList")
        @Expose
        public JobList activeList;
        @SerializedName("shortlistedList")
        @Expose
        public JobList shortlistedList;
        @SerializedName("rejectedList")
        @Expose
        public JobList rejectedList;

        public class JobList {

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
                @SerializedName("job_apply_id")
                @Expose
                public String jobApplyId;
                @SerializedName("job_post_id")
                @Expose
                public String jobPostId;
                @SerializedName("user_master_id")
                @Expose
                public String userMasterId;
                @SerializedName("apply_status")
                @Expose
                public String applyStatus;
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
                @SerializedName("requirements")
                @Expose
                public String requirements;
                @SerializedName("skills")
                @Expose
                public String skills;
                @SerializedName("job_type")
                @Expose
                public String jobType;
                @SerializedName("job_location")
                @Expose
                public String jobLocation;
                @SerializedName("name")
                @Expose
                public String name;
                @SerializedName("user_name")
                @Expose
                public String userName;
                @SerializedName("profile_link")
                @Expose
                public String profileLink;
                @SerializedName("email")
                @Expose
                public String email;
                @SerializedName("main_phone")
                @Expose
                public String mainPhone;
                @SerializedName("profile_pic")
                @Expose
                public String profilePic;
                @SerializedName("profile_banner")
                @Expose
                public String profileBanner;
                @SerializedName("position")
                @Expose
                public String position;
                @SerializedName("country_region")
                @Expose
                public String countryRegion;
                @SerializedName("city")
                @Expose
                public String city;
            }
        }
    }
}
