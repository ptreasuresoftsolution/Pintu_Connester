package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JobsEventMasterResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("dt")
    @Expose
    public List<FeedsRow> feedsRows;
    @SerializedName("html")
    @Expose
    public String html;
    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    @SerializedName("feedsFilePath")
    @Expose
    public String feedsFilePath;

}
