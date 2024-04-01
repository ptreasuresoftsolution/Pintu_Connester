package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllInOneSearchResponse {
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
    @SerializedName("feedImgPath")
    @Expose
    public String feedImgPath;

    public class Dt {

        @SerializedName("tbl_name")
        @Expose
        public String tblName;
        @SerializedName("tbl_id")
        @Expose
        public String tblId;
        @SerializedName("searchText")
        @Expose
        public String searchText;
        @SerializedName("pic")
        @Expose
        public String pic;
        @SerializedName("link")
        @Expose
        public String link;

    }
}
