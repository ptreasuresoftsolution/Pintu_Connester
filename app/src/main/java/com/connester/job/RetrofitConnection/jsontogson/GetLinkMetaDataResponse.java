package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetLinkMetaDataResponse {
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

        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("img")
        @Expose
        public String img;
        @SerializedName("info")
        @Expose
        public String info;
        @SerializedName("url")
        @Expose
        public String url;

    }

    @SerializedName("error")
    @Expose
    public String error;
    @SerializedName("errorMsg")
    @Expose
    public String errorMsg;
}
