package com.connester.job.module;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.FeedsRow;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.Constant;
import com.connester.job.function.SessionPref;

import java.util.ArrayList;

public class FeedsMaster {
    Context context;
    Activity activity;
    LayoutInflater layoutInflater;
    SessionPref sessionPref;
    UserRowResponse.Dt userRow;
    boolean isNeedCloseBtn = true;

    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call
    String feedImgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/feeds/"; //overwrite on api call


    public void setNeedCloseBtn(boolean needCloseBtn) {
        isNeedCloseBtn = needCloseBtn;
    }

    public FeedsMaster(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
        userRow = sessionPref.getUserMasterRowInObject();
        layoutInflater = LayoutInflater.from(context);
    }

    ArrayList<View> feedsViews = new ArrayList<>();

    public View getFeedsPhotoView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_photos_layout, null);
        setFeedsTitleCommon(view, userRow, feedsRow, isNeedCloseBtn);
        setFeedsLikeCommentShareCommon(view, userRow, feedsRow, isNeedCloseBtn);
        TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
        feeds_content_txt.setText(feedsRow.tblMediaPost.ptTitle);
        ImageView feeds_photo = view.findViewById(R.id.feeds_photo);
        Glide.with(context)
                .load(feedImgPath + feedsRow.tblMediaPost.mediaFiles)
                .centerCrop()
                .placeholder(R.drawable.feeds_photos_default)
                .into(feeds_photo);
        return view;
    }

    private void setFeedsLikeCommentShareCommon(View view, UserRowResponse.Dt userRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {
        ImageView like_iv = view.findViewById(R.id.like_iv);
        TextView like_counter_txt = view.findViewById(R.id.like_counter_txt);
        ImageView comment_iv = view.findViewById(R.id.comment_iv);
        TextView comment_counter_txt = view.findViewById(R.id.comment_counter_txt);
        ImageView feed_fwd_iv = view.findViewById(R.id.feed_fwd_iv);
    }

    private void setFeedsTitleCommon(View view, UserRowResponse.Dt userRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {

    }
}
