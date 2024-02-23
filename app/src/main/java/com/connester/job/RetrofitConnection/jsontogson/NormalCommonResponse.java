package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
}
