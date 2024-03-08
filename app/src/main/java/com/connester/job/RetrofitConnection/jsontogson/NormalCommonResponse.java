package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NormalCommonResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;

    //store unique username
    @SerializedName("user_name")
    @Expose
    public String userName;

    //feed save unSave
    @SerializedName("feedSave")
    @Expose
    public Boolean feedSave;

    //feed add / post on submit
    @SerializedName("feed_master_id")
    @Expose
    public Integer feedMasterId;

    //job Work experiences
    @SerializedName("position")
    @Expose
    public String position;

    //update profile pic and profile banner
    @SerializedName("fileName")
    @Expose
    public String fileName;

    //db table single clm array
    @SerializedName("dt")
    @Expose
    public List<String> dt = new ArrayList<>();
}
