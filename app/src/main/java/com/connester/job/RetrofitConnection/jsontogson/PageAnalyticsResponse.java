package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PageAnalyticsResponse {
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

        @SerializedName("pageSearchAppreanace")
        @Expose
        public String pageSearchAppreanace;
        @SerializedName("pageViews")
        @Expose
        public String pageViews;
        @SerializedName("pageMember")
        @Expose
        public String pageMember;
        @SerializedName("pageFeedsImpression")
        @Expose
        public String pageFeedsImpression;

    }
}
