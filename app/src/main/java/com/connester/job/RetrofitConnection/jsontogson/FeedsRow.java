package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedsRow {
    @SerializedName("feed_master_id")
    @Expose
    public String feedMasterId;
    @SerializedName("user_master_id")
    @Expose
    public String userMasterId;
    @SerializedName("feed_link")
    @Expose
    public String feedLink;
    @SerializedName("tbl_name")
    @Expose
    public String tblName;
    @SerializedName("tbl_id")
    @Expose
    public String tblId;
    @SerializedName("feed_for")
    @Expose
    public String feedFor;
    @SerializedName("optional_id")
    @Expose
    public String optionalId;
    @SerializedName("share_frwd_post")
    @Expose
    public String shareFrwdPost;
    @SerializedName("feed_status")
    @Expose
    public String feedStatus;
    @SerializedName("create_date")
    @Expose
    public String createDate;
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
    @SerializedName("profile_pic")
    @Expose
    public String profilePic;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("latitude_location")
    @Expose
    public Object latitudeLocation;
    @SerializedName("longitude_location")
    @Expose
    public Object longitudeLocation;
    @SerializedName("isLike")
    @Expose
    public String isLike;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("comments")
    @Expose
    public String comments;
    @SerializedName("isSave")
    @Expose
    public String isSave;
    @SerializedName("tbl_media_post")
    @Expose
    public TblMediaPost tblMediaPost;

    public class TblMediaPost {

        @SerializedName("media_post_id")
        @Expose
        public String mediaPostId;
        @SerializedName("pt_title")
        @Expose
        public String ptTitle;
        @SerializedName("media_files")
        @Expose
        public String mediaFiles;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("hash")
        @Expose
        public String hash;
        @SerializedName("link")
        @Expose
        public String link;

    }


    @SerializedName("tbl_text_post")
    @Expose
    public TblTextPost tblTextPost;

    public class TblTextPost {

        @SerializedName("text_post_id")
        @Expose
        public String textPostId;
        @SerializedName("pt_title")
        @Expose
        public String ptTitle;
        @SerializedName("pt_hash_desc")
        @Expose
        public String ptHashDesc;
        @SerializedName("pt_image")
        @Expose
        public Object ptImage;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("hash")
        @Expose
        public String hash;
        @SerializedName("link")
        @Expose
        public String link;

    }


    @SerializedName("tbl_job_event")
    @Expose
    public TblJobEvent tblJobEvent;

    public class TblJobEvent {
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
        @SerializedName("inquiry_email")
        @Expose
        public Object inquiryEmail;
        @SerializedName("create_date")
        @Expose
        public String createDate;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("time_zone")
        @Expose
        public String timeZone;
    }


    @SerializedName("tbl_job_post")
    @Expose
    public TblJobPost tblJobPost;

    public class TblJobPost {
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
        public String companyName;
        @SerializedName("inquiry_email")
        @Expose
        public String inquiryEmail;
        @SerializedName("job_description")
        @Expose
        public String jobDescription;
        @SerializedName("requirements")
        @Expose
        public String requirements;
        @SerializedName("skills")
        @Expose
        public String skills;
        @SerializedName("job_time")
        @Expose
        public String jobTime;
        @SerializedName("job_type")
        @Expose
        public String jobType;
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

    }


    @SerializedName("FeedsEndUser")
    @Expose
    public FeedsEndUser feedsEndUser;

    public class FeedsEndUser {
        @SerializedName("feedProPic")
        @Expose
        public String feedProPic;
        @SerializedName("feedProName")
        @Expose
        public String feedProName;
        @SerializedName("feedProLink")
        @Expose
        public String feedProLink;
        @SerializedName("exDt")
        @Expose
        public ExDt exDt;

        public class ExDt {
            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("profile_pic")
            @Expose
            public String profilePic;
            @SerializedName("profile_link")
            @Expose
            public String profileLink;

        }
    }

    @SerializedName("tbl_community_master")
    @Expose
    public TblCommunityMaster tblCommunityMaster;

    public class TblCommunityMaster {

        @SerializedName("community_master_id")
        @Expose
        public String communityMasterId;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("community_link")
        @Expose
        public String communityLink;
        @SerializedName("logo")
        @Expose
        public String logo;
        @SerializedName("banner")
        @Expose
        public String banner;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("bio")
        @Expose
        public String bio;
        @SerializedName("group_status")
        @Expose
        public String groupStatus;

    }

    @SerializedName("tbl_business_page")
    @Expose
    public TblBusinessPage tblBusinessPage;

    public class TblBusinessPage {

        @SerializedName("business_page_id")
        @Expose
        public String businessPageId;
        @SerializedName("bus_name")
        @Expose
        public String busName;
        @SerializedName("cus_link")
        @Expose
        public String cusLink;
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
        @SerializedName("skills")
        @Expose
        public String skills;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("page_status")
        @Expose
        public String pageStatus;

    }
}
