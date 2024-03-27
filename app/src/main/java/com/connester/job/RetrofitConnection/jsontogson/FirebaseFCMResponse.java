package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FirebaseFCMResponse {
    @SerializedName("multicast_id")
    @Expose
    public Long multicastId;
    @SerializedName("success")
    @Expose
    public Integer success;
    @SerializedName("failure")
    @Expose
    public Integer failure;
    @SerializedName("canonical_ids")
    @Expose
    public Integer canonicalIds;
    @SerializedName("results")
    @Expose
    public List<Result> results = null;

    public class Result {
        @SerializedName("error")
        @Expose
        public String error;
        @SerializedName("message_id")
        @Expose
        public String messageId;

    }
}
