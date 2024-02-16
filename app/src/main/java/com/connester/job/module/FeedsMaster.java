package com.connester.job.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.FeedsCommentListResponse;
import com.connester.job.RetrofitConnection.jsontogson.FeedsRow;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.DateUtils;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.MyListRowSet;
import com.connester.job.function.SessionPref;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
    UserRowResponse.Dt loginUserRow;
    boolean isNeedCloseBtn = true;

    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call
    String feedImgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/feeds/"; //overwrite on api call

    String feedFor = "USER";//USER/COMMUNITY/BUSINESS
    String feedForId = "0";

    public void setNeedCloseBtn(boolean needCloseBtn) {
        isNeedCloseBtn = needCloseBtn;
    }

    public FeedsMaster(Context context, Activity activity) {
        this(context, activity, "USER", "0");
    }

    public FeedsMaster(Context context, Activity activity, String feedFor, String feedForId) {
        this.context = context;
        this.activity = activity;
        this.feedFor = feedFor;
        this.feedForId = feedForId;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionPref = new SessionPref(context);
        loginUserRow = sessionPref.getUserMasterRowInObject();
        layoutInflater = LayoutInflater.from(context);
    }

    ArrayList<View> feedsViews = new ArrayList<>();

    public View getFeedsPhotoView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_photos_layout, null);
        setFeedsTitleCommon(view, loginUserRow, feedsRow, isNeedCloseBtn);
        setFeedsLikeCommentShareCommon(view, loginUserRow, feedsRow, isNeedCloseBtn);
        TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
        feeds_content_txt.setText(feedsRow.tblMediaPost.ptTitle);
        ImageView feeds_photo = view.findViewById(R.id.feeds_photo);
        Glide.with(context).load(feedImgPath + feedsRow.tblMediaPost.mediaFiles).centerCrop().placeholder(R.drawable.feeds_photos_default).into(feeds_photo);
        return view;
    }

    private void setFeedsLikeCommentShareCommon(View view, UserRowResponse.Dt loginUserRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {
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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                View view = LayoutInflater.from(context).inflate(R.layout.feeds_comment_list_dialog_layout, null);
                LinearLayout comment_ll = view.findViewById(R.id.comment_ll);
                NestedScrollView nestedScrollView_commentList = view.findViewById(R.id.nestedScrollView_commentList);
                loadCommentList(nestedScrollView_commentList, comment_ll, loginUserRow, feedsRow);
                //comment place submit
                ImageView loginUser_pic = view.findViewById(R.id.loginUser_pic);
                Glide.with(context).load(imgPath + loginUserRow.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(loginUser_pic);
                EditText comment_input = view.findViewById(R.id.comment_input);
                ImageView comment_submit_iv = view.findViewById(R.id.comment_submit_iv);
                comment_submit_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonFunction.PleaseWaitShow(context);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("feed_master_id", feedsRow.feedMasterId);
                        hashMap.put("commentText", comment_input.getText().toString());
                        apiInterface.SUBMIT_COMMENT_ON_FEEDS(hashMap).enqueue(new MyApiCallback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status) {
                                            loadCommentList(nestedScrollView_commentList, comment_ll, loginUserRow, feedsRow);
                                        } else {
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
            }
        });

        //feed fwd share
        feed_fwd_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open confirm box and forward and push top of list
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setMessage("Are you sure share this post on your post timeline?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CommonFunction.PleaseWaitShow(context);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("feed_master_id", feedsRow.feedMasterId);
                        hashMap.put("feed_for", feedFor);
                        hashMap.put("feed_for_id", feedForId);
                        apiInterface.FEEDS_SHARE_FORWARD(hashMap).enqueue(new MyApiCallback(){
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()){
                                    if (response.body() != null){
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status){
                                            //reload activity page OR add forwarded feed on top of the list
                                        }else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void loadCommentList(NestedScrollView nestedScrollView_commentList, LinearLayout commentLl, UserRowResponse.Dt loginUserRow, FeedsRow feedsRow) {
        commentLl.removeAllViews();
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("feed_master_id", feedsRow.feedMasterId);
        apiInterface.GET_FEED_COMMENT(hashMap).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                CommonFunction.dismissDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        FeedsCommentListResponse feedsCommentListResponse = (FeedsCommentListResponse) response.body();
                        if (feedsCommentListResponse.status) {
                            String imgPath = feedsCommentListResponse.imgPath;

                            new MyListRowSet(commentLl, feedsCommentListResponse.commentsRows, context) {
                                @SuppressLint("MissingInflatedId")
                                @Override
                                public View setView(int position) {
                                    FeedsCommentListResponse.CommentsRow commentsRow = feedsCommentListResponse.commentsRows.get(position);
                                    View view = layoutInflater.inflate(R.layout.feeds_comment_list_item, null);
                                    ImageView user_pic = view.findViewById(R.id.user_pic);
                                    Glide.with(context).load(imgPath + commentsRow.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);

                                    TextView userFullName_txt = view.findViewById(R.id.userFullName_txt);
                                    userFullName_txt.setText(commentsRow.name);

                                    TextView time_comment_txt = view.findViewById(R.id.time_comment_txt);
                                    time_comment_txt.setText(feedTimeCount(commentsRow.createDate));

                                    TextView comment_txt = view.findViewById(R.id.comment_txt);
                                    comment_txt.setText(commentsRow.commentText);
                                    return view;
                                }
                            }.createView();

                            nestedScrollView_commentList.post(new Runnable() {
                                @Override
                                public void run() {
                                    nestedScrollView_commentList.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        } else
                            Toast.makeText(context, feedsCommentListResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                CommonFunction.dismissDialog();
                Log.d(LogTag.API_EXCEPTION, "GET_FEED_COMMENT", t);
            }
        });
    }

    private void setFeedsTitleCommon(View view, UserRowResponse.Dt loginUserRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {

    }

    /**
     * common function for feeds
     **/
    String feedTimeCount(String createDate) {
        return feedTimeCount(createDate, DateUtils.TODAYDATETIMEforDB());
    }

    String feedTimeCount(String createDate, String nowDate) {
        long year = DateUtils.dateDiff("Y", createDate, nowDate);
        long month = DateUtils.dateDiff("m", createDate, nowDate);
        long days = DateUtils.dateDiff("d", createDate, nowDate);
        long hour = DateUtils.dateDiff("h", createDate, nowDate);
        long minute = DateUtils.dateDiff("n", createDate, nowDate);
        if (year > 0) {
            return year + " Years ago";
        } else if (month > 0 && days > 28) {
            return month + " Months ago";
        } else if (days > 0) {
            return days + " Days ago";
        } else if (hour > 0) {
            return hour + " Hours ago";
        } else if (minute > 0) {
            return minute + " Minute ago";
        } else {
            return " Now";
        }
    }
}
