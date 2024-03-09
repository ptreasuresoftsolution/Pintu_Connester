package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EducationItemResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("dt")
    @Expose
    public EducationListResponse.Dt dt;
}
