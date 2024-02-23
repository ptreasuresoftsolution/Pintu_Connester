package com.connester.job.RetrofitConnection.jsontogson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedsCommentListResponse {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("commentsRows")
    @Expose
    public List<CommentsRow> commentsRows;
    @SerializedName("totalComment")
    @Expose
    public Integer totalComment;
    @SerializedName("imgPath")
    @Expose
    public String imgPath;
    @SerializedName("feedImgPath")
    @Expose
    public String feedImgPath;

    public class CommentsRow {
        @SerializedName("feed_master_id")
        @Expose
        public String feedMasterId;
        @SerializedName("user_master_id")
        @Expose
        public String userMasterId;
        @SerializedName("comment_text")
        @Expose
        public String commentText;
        @SerializedName("create_date")
        @Expose
        public String createDate;
        @SerializedName("profile_link")
        @Expose
        public String profileLink;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("profile_pic")
        @Expose
        public String profilePic;
    }
}
