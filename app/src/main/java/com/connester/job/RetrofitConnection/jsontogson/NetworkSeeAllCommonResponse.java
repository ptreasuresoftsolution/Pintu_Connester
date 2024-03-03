package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NetworkSeeAllCommonResponse {
    public class ConnectionListResponse {

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

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("profile_link")
            @Expose
            public String profileLink;
            @SerializedName("profile_pic")
            @Expose
            public String profilePic;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;
            @SerializedName("bio")
            @Expose
            public String bio;
            @SerializedName("position")
            @Expose
            public String position;

        }

        @SerializedName("myDt")
        @Expose
        public MyDt myDt;

        public class MyDt {

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;

        }

        @SerializedName("imgPath")
        @Expose
        public String imgPath;

    }

    public class FollowReqListResponse {

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

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("profile_link")
            @Expose
            public String profileLink;
            @SerializedName("profile_pic")
            @Expose
            public String profilePic;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;
            @SerializedName("bio")
            @Expose
            public String bio;
            @SerializedName("position")
            @Expose
            public String position;

        }

        @SerializedName("myDt")
        @Expose
        public MyDt myDt;

        public class MyDt {

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;

        }

        @SerializedName("imgPath")
        @Expose
        public String imgPath;

    }

    public class FollowerListResponse {

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

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("profile_link")
            @Expose
            public String profileLink;
            @SerializedName("profile_pic")
            @Expose
            public String profilePic;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;
            @SerializedName("bio")
            @Expose
            public String bio;
            @SerializedName("position")
            @Expose
            public String position;

        }

        @SerializedName("myDt")
        @Expose
        public MyDt myDt;

        public class MyDt {

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;

        }

        @SerializedName("imgPath")
        @Expose
        public String imgPath;

    }

    public class FollowingsListResponse {

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

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("name")
            @Expose
            public String name;
            @SerializedName("profile_link")
            @Expose
            public String profileLink;
            @SerializedName("profile_pic")
            @Expose
            public String profilePic;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;
            @SerializedName("bio")
            @Expose
            public String bio;
            @SerializedName("position")
            @Expose
            public String position;

        }

        @SerializedName("myDt")
        @Expose
        public MyDt myDt;

        public class MyDt {

            @SerializedName("user_master_id")
            @Expose
            public String userMasterId;
            @SerializedName("connect_user")
            @Expose
            public String connectUser;

        }

        @SerializedName("imgPath")
        @Expose
        public String imgPath;

    }

    public class CommunitysListResponse {

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

            @SerializedName("community_master_id")
            @Expose
            public String communityMasterId;
            @SerializedName("members")
            @Expose
            public String members;
            @SerializedName("user_role")
            @Expose
            public String userRole;
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

        }

        @SerializedName("imgPath")
        @Expose
        public String imgPath;

    }

    public class BusinessPagesListResponse {

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

            @SerializedName("business_page_id")
            @Expose
            public String businessPageId;
            @SerializedName("members")
            @Expose
            public String members;
            @SerializedName("user_role")
            @Expose
            public String userRole;
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
}
