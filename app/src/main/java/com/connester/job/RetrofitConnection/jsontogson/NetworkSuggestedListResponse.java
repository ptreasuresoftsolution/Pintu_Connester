package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NetworkSuggestedListResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("html")
    @Expose
    public String html;
    @SerializedName("jsonDt")
    @Expose
    public JsonDt jsonDt;
    public class JsonDt {

        //also use in seell & list option
        @SerializedName("ConnReq")
        @Expose
        public ConnReq connReq;
        public class ConnReq {

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
        @SerializedName("SugUserCity")
        @Expose
        public SugUserCity sugUserCity;
        public class SugUserCity {

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
                @SerializedName("send_connect_req")
                @Expose
                public String sendConnectReq;
                @SerializedName("rec_connect_req")
                @Expose
                public String recConnectReq;
                @SerializedName("city")
                @Expose
                public String city;
                @SerializedName("country_region")
                @Expose
                public String countryRegion;
                @SerializedName("blocked_user")
                @Expose
                public String blockedUser;

            }
            @SerializedName("imgPath")
            @Expose
            public String imgPath;

        }
        @SerializedName("SugUserIndustry")
        @Expose
        public SugUserIndustry sugUserIndustry;
        public class SugUserIndustry {

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
                @SerializedName("send_connect_req")
                @Expose
                public String sendConnectReq;
                @SerializedName("rec_connect_req")
                @Expose
                public String recConnectReq;
                @SerializedName("industry")
                @Expose
                public String industry;
                @SerializedName("blocked_user")
                @Expose
                public String blockedUser;

            }
            @SerializedName("imgPath")
            @Expose
            public String imgPath;

        }
        @SerializedName("SugGroup")
        @Expose
        public SugGroup sugGroup;
        public class SugGroup {

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
        @SerializedName("SugBusPages")
        @Expose
        public SugBusPages sugBusPages;
        public class SugBusPages {

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

    }
}
