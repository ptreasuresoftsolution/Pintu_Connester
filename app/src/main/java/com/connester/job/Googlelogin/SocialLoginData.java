package com.connester.job.Googlelogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialLoginData {
    @SerializedName("fname")
    @Expose
    public String fname;
    @SerializedName("lname")
    @Expose
    public String lname;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("ac_id")
    @Expose
    public String acId;
    @SerializedName("photo_url")
    @Expose
    public String photoUrl;
}
