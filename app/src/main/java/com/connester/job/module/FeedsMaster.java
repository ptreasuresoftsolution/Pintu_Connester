package com.connester.job.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.connester.job.activity.BusinessActivity;
import com.connester.job.activity.CommunityActivity;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.DateUtils;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.MyListRowSet;
import com.connester.job.function.SessionPref;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

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

    ArrayList<FeedStorage> feedsViews = new ArrayList<>();

    public View getFeedsPhotoView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_photos_layout, null);
        view.setTag(feedsRow.feedMasterId);
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
                        apiInterface.FEEDS_SHARE_FORWARD(hashMap).enqueue(new MyApiCallback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status) {
                                            //reload activity page OR add forwarded feed on top of the list
                                            reloadOrAddTopFeeds();
                                        } else
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
//        share by user elements
        MaterialCardView feeds_fwd_shareBy_user_view = view.findViewById(R.id.feeds_fwd_shareBy_user_view);
        ImageView feeds_shareBy_User_pic = view.findViewById(R.id.feeds_shareBy_User_pic);
        TextView feeds_shareBy_User_name = view.findViewById(R.id.feeds_shareBy_User_name);
//        group icon userpic elements
        MaterialCardView title_gMember_view = view.findViewById(R.id.title_gMember_view);
        ImageView title_gMember_pic = view.findViewById(R.id.title_gMember_pic);
//        main elements
        ImageView feeds_title_img = view.findViewById(R.id.feeds_title_img);
        TextView fullname_txt = view.findViewById(R.id.fullname_txt);
        TextView time_ago_txt = view.findViewById(R.id.time_ago_txt);
        ImageView feeds_option_iv = view.findViewById(R.id.feeds_option_iv);

        //set data on elements

        Intent clickPicNm = null;
        String feedsRowOptionalId = feedsRow.optionalId;
        if (!(feedsRow.optionalId != null && !feedsRow.optionalId.equalsIgnoreCase("0"))) {
            feedsRowOptionalId = feedsRow.userMasterId;
        }

        int defaultPic = R.drawable.default_user_pic;
        String feedsProPic = feedsRow.profilePic;
        String feedsProName = feedsRow.name;
        clickPicNm = new Intent(context, ProfileActivity.class);
        clickPicNm.putExtra("user_master_id", feedsRow.userMasterId);

        if (feedsRow.feedFor.equalsIgnoreCase("COMMUNITY")) {
            defaultPic = R.drawable.default_groups_pic;
            feedsProPic = feedsRow.tblCommunityMaster.logo;
            feedsProName = feedsRow.tblCommunityMaster.name;
            clickPicNm = new Intent(context, CommunityActivity.class);
            clickPicNm.putExtra("community_master_id", feedsRow.tblCommunityMaster.communityMasterId);
        } else if (feedsRow.feedFor.equalsIgnoreCase("BUSINESS")) {
            defaultPic = R.drawable.default_business_pic;
            feedsProPic = feedsRow.tblBusinessPage.logo;
            feedsProName = feedsRow.tblBusinessPage.busName;
            clickPicNm = new Intent(context, BusinessActivity.class);
            clickPicNm.putExtra("business_page_id", feedsRow.tblBusinessPage.businessPageId);
        }

        boolean unFollowShow = true;
        if (feedsRow.userMasterId == loginUserRow.userMasterId && feedsRow.feedFor.equalsIgnoreCase("USER")) {
            unFollowShow = false;
        }
        String unFollowName = feedsProName;

        feeds_fwd_shareBy_user_view.setVisibility(View.GONE);
        if (feedsRow.shareFrwdPost != null && !feedsRow.shareFrwdPost.equalsIgnoreCase("0")) {
            //share by elements set if(is share feeds)
            feeds_fwd_shareBy_user_view.setVisibility(View.VISIBLE);

            Glide.with(context).load(imgPath + feedsProPic).centerCrop().placeholder(defaultPic).into(feeds_shareBy_User_pic);
            feeds_shareBy_User_name.setText(feedsProName);
            Intent finalIntent = clickPicNm;
            feeds_fwd_shareBy_user_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalIntent != null) {
                        context.startActivity(finalIntent);
                    }
                }
            });

            //overwrite main feeds user details with end user
            defaultPic = R.drawable.default_user_pic;
            feedsProPic = feedsRow.feedsEndUser.feedProPic;
            feedsProName = feedsRow.feedsEndUser.feedProName;
            clickPicNm = new Intent(context, ProfileActivity.class);
            clickPicNm.putExtra("user_master_id", feedsRow.feedsEndUser.exDt.userMasterId);
            if (feedsRow.feedsEndUser.feedFor.equalsIgnoreCase("COMMUNITY")) {
                defaultPic = R.drawable.default_groups_pic;
                clickPicNm = new Intent(context, CommunityActivity.class);
                clickPicNm.putExtra("community_master_id", feedsRow.feedsEndUser.exDt.communityMasterId);
            } else if (feedsRow.feedsEndUser.feedFor.equalsIgnoreCase("BUSINESS")) {
                defaultPic = R.drawable.default_business_pic;
                clickPicNm = new Intent(context, BusinessActivity.class);
                clickPicNm.putExtra("business_page_id", feedsRow.feedsEndUser.exDt.businessPageId);
            }
        }

        //group feeds set group extra elements
        title_gMember_view.setVisibility(View.GONE);
        if (feedsRow.feedFor.equalsIgnoreCase("COMMUNITY")) {
            title_gMember_view.setVisibility(View.VISIBLE);
            Glide.with(context).load(imgPath + feedsRow.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(title_gMember_pic);
            title_gMember_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("user_master_id", feedsRow.userMasterId);
                    context.startActivity(i);
                }
            });
        }

        Glide.with(context).load(imgPath + feedsProPic).centerCrop().placeholder(defaultPic).into(feeds_title_img);
        fullname_txt.setText(feedsProName);
        time_ago_txt.setText(feedTimeCount(feedsRow.createDate));
        Intent finalClickPicNm = clickPicNm;
        View.OnClickListener openTitleProfile = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalClickPicNm != null)
                    context.startActivity(finalClickPicNm);
            }
        };
        feeds_title_img.setOnClickListener(openTitleProfile);
        fullname_txt.setOnClickListener(openTitleProfile);
        boolean finalUnFollowShow = unFollowShow;
        String finalFeedsRowOptionalId = feedsRowOptionalId;
        feeds_option_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //feeds option open in bottom sheet dialog
                BottomSheetDialog feedsOptionDialog = new BottomSheetDialog(context);
                feedsOptionDialog.setContentView(R.layout.feeds_option_dialog_layout);
                LinearLayout feed_close = feedsOptionDialog.findViewById(R.id.feed_close);
                feed_close.setVisibility(View.GONE);
                if (isNeedCloseBtn) {
                    feed_close.setVisibility(View.VISIBLE);
                    feed_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //call close api and remove feeds in list (use feedMasterId)
                            CommonFunction.PleaseWaitShow(context);
                            HashMap hashMap = new HashMap();
                            hashMap.put("user_master_id", sessionPref.getUserMasterId());
                            hashMap.put("apiKey", sessionPref.getApiKey());
                            hashMap.put("feed_master_id", feedsRow.feedMasterId);
                            apiInterface.FEEDS_OPTION_CLOSE(hashMap).enqueue(new MyApiCallback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    super.onResponse(call, response);
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                            if (normalCommonResponse.status) {
                                                removeFeedsInList(view);
                                            } else
                                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }

                LinearLayout feed_save_unsave = feedsOptionDialog.findViewById(R.id.feed_save_unsave);
                ImageView feed_save_unsave_icon = feedsOptionDialog.findViewById(R.id.feed_save_unsave_icon);
                final boolean[] isSave = {Integer.parseInt(feedsRow.isSave) > 0};
                if (isSave[0]) {
                    feed_save_unsave_icon.setImageResource(R.drawable.feed_save_fill);
                }
                feed_save_unsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call feeds save unsave api and set related icon (use feedMasterId / isSave)
                        CommonFunction.PleaseWaitShow(context);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("feed_master_id", feedsRow.feedMasterId);
                        hashMap.put("isSave", isSave[0] ? 1 : 0);
                        apiInterface.FEEDS_OPTION_SAVE_UNSAVE(hashMap).enqueue(new MyApiCallback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()){
                                    if (response.body() != null){
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status){
                                            isSave[0] = normalCommonResponse.feedSave;
                                            if (isSave[0]) {
                                                feed_save_unsave_icon.setImageResource(R.drawable.feed_save_fill);
                                            }else{
                                                feed_save_unsave_icon.setImageResource(R.drawable.feed_save_blank);
                                            }
                                        }else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });

                LinearLayout feed_link_copy = feedsOptionDialog.findViewById(R.id.feed_link_copy);
                feed_link_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //link copy set (use link)
                        String link = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/feeds/" + feedsRow.feedLink;
                        CommonFunction.copyToClipBoard(context, link);
                    }
                });

                LinearLayout feed_unfollow = feedsOptionDialog.findViewById(R.id.feed_unfollow);
                TextView feed_unfollow_name = feedsOptionDialog.findViewById(R.id.feed_unfollow_name);
                feed_unfollow.setVisibility(View.GONE);
                if (finalUnFollowShow) {
                    feed_unfollow.setVisibility(View.VISIBLE);
                    feed_unfollow_name.setText("Unfollow " + unFollowName);
                    feed_unfollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //call unfollow profile and remove item in feed list(use feedsRowOptionalId/feedsRow.feedFor)
                            CommonFunction.PleaseWaitShow(context);
                            HashMap hashMap = new HashMap();
                            hashMap.put("user_master_id", sessionPref.getUserMasterId());
                            hashMap.put("apiKey", sessionPref.getApiKey());
                            hashMap.put("profile_id", finalFeedsRowOptionalId);
                            hashMap.put("profile_type", feedsRow.feedFor);
                            apiInterface.FEEDS_OPTION_UNFOLLOW_PROFILE(hashMap).enqueue(new MyApiCallback(){
                                @Override
                                public void onResponse(Call call, Response response) {
                                    super.onResponse(call, response);
                                    if (response.isSuccessful()){
                                        if (response.body() != null){
                                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                            if (normalCommonResponse.status){
                                                removeFeedsInList(view);
                                            }else
                                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }

                LinearLayout feed_report = feedsOptionDialog.findViewById(R.id.feed_report);
                feed_report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //send report data
                    }
                });
            }
        });
    }

    /**
     * common function for feeds
     **/
    private void reloadOrAddTopFeeds() {
        reloadOrAddTopFeeds(null);
    }

    /*** programing remain **/
    private void reloadOrAddTopFeeds(FeedsRow feedsRow) {
        if (feedsRow == null) {
            //compulsory reload
        } else {
            //optional add feeds on top
        }
    }

    private void removeFeedsInList(FeedsRow feedsRow) {

    }

    private void removeFeedsInList(View mainLayoutView) {

    }

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

    class FeedStorage {
        View view; // getTag to given is feedMasterId
        int viewIndex;
        FeedsRow feedsRow;

        public FeedStorage(View view, int viewIndex, FeedsRow feedsRow) {
            this.view = view;
            this.viewIndex = viewIndex;
            this.feedsRow = feedsRow;
        }
    }
}
