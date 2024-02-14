package com.connester.job.module;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.FeedsRow;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedsMaster {
    Context context;
    Activity activity;
    ApiInterface apiInterface;
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
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
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
        Glide.with(context).load(feedImgPath + feedsRow.tblMediaPost.mediaFiles).centerCrop().placeholder(R.drawable.feeds_photos_default).into(feeds_photo);
        return view;
    }

    private void setFeedsLikeCommentShareCommon(View view, UserRowResponse.Dt userRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {
        ImageView like_iv = view.findViewById(R.id.like_iv);
        TextView like_counter_txt = view.findViewById(R.id.like_counter_txt);
        ImageView comment_iv = view.findViewById(R.id.comment_iv);
        TextView comment_counter_txt = view.findViewById(R.id.comment_counter_txt);
        ImageView feed_fwd_iv = view.findViewById(R.id.feed_fwd_iv);

        //like
        final boolean isLike = Integer.parseInt(feedsRow.isLike) > 0;
        like_counter_txt.setText(feedsRow.likes);
        if (isLike) {
            like_iv.setImageResource(R.drawable.like_fill);
        }
        View.OnClickListener likeApiCall = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("user_master_id", sessionPref.getUserMasterId());
                hashMap.put("apiKey", sessionPref.getApiKey());
                hashMap.put("feed_master_id", feedsRow.feedMasterId);
                apiInterface.CALL_LIKE_UNLIKE(hashMap).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        CommonFunction.dismissDialog();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    int counter = Integer.parseInt(feedsRow.likes);
                                    if (normalCommonResponse.msg.equalsIgnoreCase("Like!")) {
                                        like_iv.setImageResource(R.drawable.like_fill);
                                        like_counter_txt.setText(String.valueOf(counter + 1));
                                    } else {
                                        like_iv.setImageResource(R.drawable.like);
                                        if (counter > 0)
                                            like_counter_txt.setText(String.valueOf(counter - 1));
                                    }
                                } else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        CommonFunction.dismissDialog();
                        Log.d(LogTag.API_EXCEPTION, "CALL_LIKE_UNLIKE", t);
                    }
                });
            }
        };
        like_iv.setOnClickListener(likeApiCall);

        //comment (remain bottom dialog for list and add)
        comment_counter_txt.setText(feedsRow.comments);
        comment_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open comment list With comment insert input EV
            }
        });

        //feed fwd share
        feed_fwd_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open confirm box and forward and push top of list
            }
        });
    }

    private void setFeedsTitleCommon(View view, UserRowResponse.Dt userRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {

    }
}
