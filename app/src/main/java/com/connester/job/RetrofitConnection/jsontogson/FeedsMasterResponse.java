package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedsMasterResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("totalRow")
    @Expose
    public String totalRow;
    @SerializedName("feedsRows")
    @Expose
    public List<FeedsRow> feedsRows;

    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    @SerializedName("feedImgPath")
    @Expose
    public String feedImgPath;

}
