package com.connester.job.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.FeedsCommentListResponse;
import com.connester.job.RetrofitConnection.jsontogson.FeedsMasterResponse;
import com.connester.job.RetrofitConnection.jsontogson.FeedsRow;
import com.connester.job.RetrofitConnection.jsontogson.JobsEventMasterResponse;
import com.connester.job.RetrofitConnection.jsontogson.MyPagesListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSeeAllCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.OurEventPostRow;
import com.connester.job.RetrofitConnection.jsontogson.OurJobPostRow;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.activity.FeedFullViewActivity;
import com.connester.job.activity.NetworkActivity;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.activity.business.BusinessActivity;
import com.connester.job.activity.community.CommunityActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.CustomPager;
import com.connester.job.function.DateUtils;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.MyListRowSet;
import com.connester.job.function.ScrollBottomListener;
import com.connester.job.function.SessionPref;
import com.connester.job.plugins.multipleselectedspinner.KeyPairBoolData;
import com.connester.job.plugins.multipleselectedspinner.MultiSpinnerListener;
import com.connester.job.plugins.multipleselectedspinner.MultiSpinnerSearch;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call
    String feedImgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/feeds/"; //overwrite on api call

    //master call define ----------- start -------//
    String feedsIds = "";//get some of feeds with ids(pass 1,2,3,4)
    String feedForId = "-1";//Feed for value send then use the id of feed_for
    String feedFor = "";//USER/COMMUNITY/BUSINESS(accept only one)
    String tblName = "";//MEDIA/EVENT/JOB/POST(accept Single and multiple with comma)
    boolean isChkClose = true;//check with user close feeds(not include in json) true/false
    String extraWhr = "";//pass special whr in sql syntax(passWith Base64Encode)
    int start = 0;//start list (0/10/20/30 Like pagenation);
    int limitGap = 5;//sepecific page limit

    public void setFeedFor(String feedFor) {
        this.feedFor = feedFor;
    }

    public void setFeedForId(String feedForId) {
        this.feedForId = feedForId;
    }

    public void setFeedsIds(String feedsIds) {
        this.feedsIds = feedsIds;
    }

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public void setChkClose(boolean chkClose) {
        isChkClose = chkClose;
    }

    public void setExtraWhr(String extraWhr) {
        this.extraWhr = extraWhr;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setLimitGap(int limitGap) {
        this.limitGap = limitGap;
    }

    public void loadFeedMaster(LinearLayout mainLinearLayout, ScrollView scrollView) {
        loadFeedMaster(mainLinearLayout, scrollView, 0);
    }

    public void loadFeedMaster(LinearLayout mainLinearLayout, ScrollView scrollView, int scrollViewExtraHeight) {
        this.mainLinearLayout = mainLinearLayout;
        this.scrollView = scrollView;
        //pagination
        this.scrollView = CommonFunction.OnScrollSetBottomListener(scrollView, new ScrollBottomListener() {
            @Override
            public void onScrollBottom() {
                if (!viewDisable)
                    if (mainLinearLayout.getChildCount() > 0) {
                        //set loading check
                        if (start < totalRow) {
                            callFeedMaster();
                        }
                    }
            }
        }, scrollViewExtraHeight);
        //video play/pause
        this.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                for (FeedStorage feedStorage : feedsViews) {
                    if (feedStorage.isVideoFeeds) {
                        if (feedStorage.styledPlayerView.getTag().equals("play")) {
                            if (CommonFunction.verticallyTopBottomShowInView(mainLinearLayout.getChildAt(feedStorage.viewIndex), scrollView.getScrollY(), context)) {
                                Log.e(LogTag.TMP_LOG, "PLAY " + feedStorage.feedsRow.feedMasterId);
                                if (!feedStorage.player.isPlaying() && !feedStorage.player.isLoading()) {
                                    feedStorage.player.play();
                                    feedStorage.styledPlayerView.hideController();
                                }
                            } else {
                                Log.e(LogTag.TMP_LOG, "PAUSE " + feedStorage.feedsRow.feedMasterId);
                                if (feedStorage.player != null && feedStorage.player.isPlaying()) {
                                    feedStorage.player.pause();
                                }
                            }
                        }
                    }
                    if (!isFeedsFullView && !feedStorage.impressionAdd) {
                        if (CommonFunction.verticallyTopBottomShowInView(mainLinearLayout.getChildAt(feedStorage.viewIndex), scrollView.getScrollY(), context)) {
                            if (feedStorage.feedsRow.feedMasterId != null) {
                                visitMaster.impressionFeeds(feedStorage.feedsRow.feedMasterId);
                                feedStorage.impressionAdd = true;
                            }
                        }
                    }
                }
            }
        });
        resetList();
        if (!viewDisable)
            callFeedMaster();
    }

    private void callFeedMaster() {
        if (isLoading) return;
        isLoading = true;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        else CommonFunction.PleaseWaitShow(context);

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("feedsIds", feedsIds);
        hashMap.put("feed_for_id", feedForId);
        hashMap.put("feed_for", feedFor);
        hashMap.put("tbl_name", tblName);
        hashMap.put("isChkClose", isChkClose);
        hashMap.put("extraWhr", extraWhr);
        hashMap.put("start", start);
        hashMap.put("limitGap", limitGap);
        apiInterface.FEED_MASTER_JSON_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                isLoading = false;
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        FeedsMasterResponse feedsMasterResponse = (FeedsMasterResponse) response.body();
                        if (feedsMasterResponse.status) {
                            imgPath = feedsMasterResponse.imgPath;
                            feedImgPath = feedsMasterResponse.feedImgPath;
                            totalRow = Long.parseLong(feedsMasterResponse.totalRow);
                            start += limitGap;
                            listToView(feedsMasterResponse.feedsRows);
                        } else
                            Toast.makeText(context, feedsMasterResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //master call define ----------- End -------//
    UserMaster userMaster;
    ComponentActivity componentActivity;

    public FeedsMaster(Context context, Activity activity, ComponentActivity componentActivity) {
        this.context = context;
        this.activity = activity;
        this.componentActivity = componentActivity;
        visitMaster = new VisitMaster(context, activity);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionPref = new SessionPref(context);
        loginUserRow = sessionPref.getUserMasterRowInObject();
        layoutInflater = LayoutInflater.from(context);
        if (componentActivity != null) {
            userMaster = new UserMaster(context, componentActivity);
            userMaster.initReportAttachmentLauncher();
        }
    }

    public void initUserMaster(ComponentActivity componentActivity) {
        userMaster = new UserMaster(context, componentActivity);
        userMaster.initReportAttachmentLauncher();
    }

    public void setUserMaster(UserMaster userMaster) {
        this.userMaster = userMaster;
    }

    VisitMaster visitMaster;
    ArrayList<FeedStorage> feedsViews = new ArrayList<>();
    HashMap<String, ExoPlayer> playerHashMap = new HashMap<>();
    HashMap<String, StyledPlayerView> styledPlayerViewHashMap = new HashMap<>();
    LinearLayout mainLinearLayout;
    ScrollView scrollView;
    public String feedListBy = "common";
    String feedForForward = "USER";
    String feedForIdForward = "0";

    public void setFeedForForward(String feedForForward) {
        this.feedForForward = feedForForward;
    }

    public void setFeedForIdForward(String feedForIdForward) {
        this.feedForIdForward = feedForIdForward;
    }

    long totalRow = 0;
    int viewIndex = 0;
    boolean isLoading = false, loadOnSavePage = false;
    View progressBar;

    public void setProgressBar(View progressBar) {
        this.progressBar = progressBar;
    }

    private void resetList() {
        this.mainLinearLayout.removeAllViews();
        if (mainLinearLayoutChange != null)
            mainLinearLayoutChange.itemAddEditChange(mainLinearLayout);
        feedsViews.clear();
        playerHashMap.clear();
        styledPlayerViewHashMap.clear();
        start = 0;
        viewIndex = 0;
        totalRow = 0;
    }

    public void loadHomeFeeds(LinearLayout mainLinearLayout, ScrollView scrollView) {
        this.mainLinearLayout = mainLinearLayout;
        this.scrollView = scrollView;
        feedListBy = "home";
        //pagination
        this.scrollView = CommonFunction.OnScrollSetBottomListener(scrollView, new ScrollBottomListener() {
            @Override
            public void onScrollBottom() {
                if (mainLinearLayout.getChildCount() > 0) {
                    //set loading check
                    if (start < totalRow) {
                        callHomeFeeds();
                    }
                }
            }
        });
        //video play/pause
        this.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                for (FeedStorage feedStorage : feedsViews) {
                    if (feedStorage.isVideoFeeds) {
                        if (feedStorage.styledPlayerView.getTag().equals("play")) {
                            if (CommonFunction.verticallyTopBottomShowInView(mainLinearLayout.getChildAt(feedStorage.viewIndex), scrollView.getScrollY(), context)) {
                                Log.e(LogTag.TMP_LOG, "PLAY " + feedStorage.feedsRow.feedMasterId);
                                if (!feedStorage.player.isPlaying() && !feedStorage.player.isLoading()) {
                                    feedStorage.player.play();
                                    feedStorage.styledPlayerView.hideController();
                                }
                            } else {
                                Log.e(LogTag.TMP_LOG, "PAUSE " + feedStorage.feedsRow.feedMasterId);
                                if (feedStorage.player != null && feedStorage.player.isPlaying()) {
                                    feedStorage.player.pause();
                                }
                            }
                        }
                    }
                    if (!isFeedsFullView && !feedStorage.impressionAdd) {
                        if (CommonFunction.verticallyTopBottomShowInView(mainLinearLayout.getChildAt(feedStorage.viewIndex), scrollView.getScrollY(), context)) {
                            if (feedStorage.feedsRow.feedMasterId != null) {
                                visitMaster.impressionFeeds(feedStorage.feedsRow.feedMasterId);
                                feedStorage.impressionAdd = true;
                            }
                        }
                    }
                }
            }
        });
        resetList();
        callHomeFeeds();
    }

    private void callHomeFeeds() {
        if (isLoading) return;
        isLoading = true;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        else CommonFunction.PleaseWaitShow(context);

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
//        hashMap.put("tbl_name", "MEDIA,POST,JOBS,EVENT");
        hashMap.put("isChkClose", isChkClose);
        hashMap.put("type", "home");
        hashMap.put("limitGap", limitGap);
        hashMap.put("start", start);
        apiInterface.HOME_FEEDS_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                isLoading = false;
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        FeedsMasterResponse feedsMasterResponse = (FeedsMasterResponse) response.body();
                        if (feedsMasterResponse.status) {
                            imgPath = feedsMasterResponse.imgPath;
                            feedImgPath = feedsMasterResponse.feedImgPath;
                            totalRow = Long.parseLong(feedsMasterResponse.totalRow);
                            start += limitGap;
                            listToView(feedsMasterResponse.feedsRows);
                        } else
                            Toast.makeText(context, feedsMasterResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void callSuggestedJobsEventsFeeds(LinearLayout mainLinearLayout, ScrollView scrollView) {
        this.mainLinearLayout = mainLinearLayout;
        this.scrollView = scrollView;
        feedListBy = "JobsEvent";
        resetList();
        callSuggestedJobsFeeds(false);
    }

    private void callSuggestedJobsFeeds(boolean seeAll) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("seeAll", seeAll);
        apiInterface.JOB_SUGGESTED_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        JobsEventMasterResponse jobsEventMasterResponse = (JobsEventMasterResponse) response.body();
                        if (jobsEventMasterResponse.status) {
                            imgPath = jobsEventMasterResponse.imgPath;
                            feedImgPath = jobsEventMasterResponse.feedsFilePath;
                            FrameLayout frameLayout = new FrameLayout(context);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                            frameLayout.setLayoutParams(layoutParams);

                            TextView nt_list_title = new TextView(context);
                            FrameLayout.LayoutParams layout_867 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            layout_867.gravity = Gravity.CENTER_VERTICAL;
                            nt_list_title.setLayoutParams(layout_867);
                            nt_list_title.setPadding((int) (10 / context.getResources().getDisplayMetrics().density), (int) (4 / context.getResources().getDisplayMetrics().density), 0, (int) (3 / context.getResources().getDisplayMetrics().density));
                            nt_list_title.setText("Jobs recommended for you");
                            nt_list_title.setTypeface(nt_list_title.getTypeface(), Typeface.BOLD);
                            frameLayout.addView(nt_list_title);

                            if (!seeAll) {
                                MaterialButton nt_list_seeall = new MaterialButton(context);
                                FrameLayout.LayoutParams layout_865 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 50);
                                layout_865.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                                nt_list_seeall.setLayoutParams(layout_865);
                                nt_list_seeall.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.transparent));
                                nt_list_seeall.setPadding(10, 0, 10, 0);
                                nt_list_seeall.setText("See All");
                                nt_list_seeall.setTextColor(context.getColor(R.color.secondary_3));
                                frameLayout.addView(nt_list_seeall);
                                nt_list_seeall.setOnClickListener(v -> {
                                    resetList();
                                    callSuggestedJobsFeeds(true);
                                });
                            }
                            FeedStorage feedStorage = new FeedStorage(frameLayout, viewIndex, null);
                            feedsViews.add(viewIndex, feedStorage);
                            mainLinearLayout.addView(frameLayout, viewIndex);
                            if (mainLinearLayoutChange != null)
                                mainLinearLayoutChange.itemAddEditChange(mainLinearLayout);
                            viewIndex++;
                            listToView(jobsEventMasterResponse.feedsRows);
                            if (!seeAll) {
                                callSuggestedEventsFeeds(false);
                            }
                        } else
                            Toast.makeText(context, jobsEventMasterResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void callSuggestedEventsFeeds(boolean seeAll) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("seeAll", seeAll);
        apiInterface.EVENT_SUGGESTED_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        JobsEventMasterResponse jobsEventMasterResponse = (JobsEventMasterResponse) response.body();
                        if (jobsEventMasterResponse.status) {
                            imgPath = jobsEventMasterResponse.imgPath;
                            feedImgPath = jobsEventMasterResponse.feedsFilePath;
                            FrameLayout frameLayout = new FrameLayout(context);
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.topMargin = 20;
                            frameLayout.setLayoutParams(layoutParams);

                            if (!seeAll) {
                                View view = new View(context);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
                                view.setLayoutParams(params);
                                view.setBackgroundColor(context.getColor(R.color.secondary));
                                frameLayout.addView(view);
                            }

                            TextView nt_list_title = new TextView(context);
                            FrameLayout.LayoutParams layout_867 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layout_867.gravity = Gravity.CENTER_VERTICAL;
                            layout_867.topMargin = 4;
                            nt_list_title.setLayoutParams(layout_867);
                            nt_list_title.setPadding((int) (10 / context.getResources().getDisplayMetrics().density), (int) (4 / context.getResources().getDisplayMetrics().density), 0, (int) (3 / context.getResources().getDisplayMetrics().density));
                            nt_list_title.setText("Events start soon");
                            nt_list_title.setTypeface(nt_list_title.getTypeface(), Typeface.BOLD);
                            frameLayout.addView(nt_list_title);

                            if (!seeAll) {
                                MaterialButton nt_list_seeall = new MaterialButton(context);
                                FrameLayout.LayoutParams layout_865 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                                layout_865.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                                layout_865.topMargin = 4;
                                nt_list_seeall.setLayoutParams(layout_865);
                                nt_list_seeall.setBackgroundTintList(context.getResources().getColorStateList(android.R.color.transparent));
                                nt_list_seeall.setPadding(10, 0, 10, 0);
                                nt_list_seeall.setText("See All");
                                nt_list_seeall.setTextColor(context.getColor(R.color.secondary_3));
                                frameLayout.addView(nt_list_seeall);
                                nt_list_seeall.setOnClickListener(v -> {
                                    resetList();
                                    callSuggestedEventsFeeds(true);
                                });
                            }
                            FeedStorage feedStorage = new FeedStorage(frameLayout, viewIndex, null);
                            feedsViews.add(viewIndex, feedStorage);
                            mainLinearLayout.addView(frameLayout, viewIndex);
                            if (mainLinearLayoutChange != null)
                                mainLinearLayoutChange.itemAddEditChange(mainLinearLayout);
                            viewIndex++;
                            listToView(jobsEventMasterResponse.feedsRows);
                        } else
                            Toast.makeText(context, jobsEventMasterResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void loadSingleFeeds(LinearLayout mainLinearLayout, ScrollView scrollView, String feed_master_id) {
        this.mainLinearLayout = mainLinearLayout;
        this.scrollView = scrollView;
        feedListBy = "single";
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("feed_master_id", feed_master_id);

        apiInterface.SINGLE_FEED_JSON_CALL(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        FeedsMasterResponse feedsMasterResponse = (FeedsMasterResponse) response.body();
                        if (feedsMasterResponse.status) {
                            imgPath = feedsMasterResponse.imgPath;
                            feedImgPath = feedsMasterResponse.feedImgPath;
                            listToView(feedsMasterResponse.feedsRows);
                        }
                    }
                }
            }
        });

        resetList();
    }

    private void listToView(List<FeedsRow> feedsRows) {
        for (FeedsRow feedsRow : feedsRows) {
            setSingleFeeds(feedsRow, viewIndex);
            viewIndex++;
        }
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void setSingleFeeds(FeedsRow feedsRow, int vIndex) {
        ExoPlayer player = null;
        StyledPlayerView styledPlayerView = null;
        View view;
        if (feedsRow.tblName.equalsIgnoreCase("MEDIA")) {
            if (title != null) {
                title.setText("Photos");
            }
            if (feedsRow.tblMediaPost.type.equalsIgnoreCase("VIDEO")) {
                view = getFeedsVideoView(feedsRow);
                player = playerHashMap.get(feedsRow.feedMasterId);
                styledPlayerView = styledPlayerViewHashMap.get(feedsRow.feedMasterId);
                if (title != null) {
                    title.setText("Video");
                }
            } else if (feedsRow.tblMediaPost.type.equalsIgnoreCase("M-IMAGE")) {
                view = getFeedsMultiplePhotosView(feedsRow);
            } else {//IMAGE
                view = getFeedsPhotoView(feedsRow);
            }
        } else if (feedsRow.tblName.equalsIgnoreCase("POST")) {
            if (title != null) {
                title.setText("Text/Content");
            }
            if (feedsRow.tblTextPost.type.equalsIgnoreCase("LINK")) {
                view = getFeedsLinkView(feedsRow);
            } else if (feedsRow.tblTextPost.type.equalsIgnoreCase("TEXT-LINK")) {
                view = getFeedsContentLinkView(feedsRow);
            } else {//TEXT
                view = getFeedsContentView(feedsRow);
            }
        } else if (feedsRow.tblName.equalsIgnoreCase("EVENT")) {
            if (title != null) {
                title.setText("Event");
            }
            if (isFeedsFullView) {
                view = getFeedsEventFullView(feedsRow);
            } else
                view = getFeedsEventView(feedsRow);
        } else {//JOB
            if (title != null) {
                title.setText("Job");
            }
            if (isFeedsFullView) {
                view = getFeedsJobFullView(feedsRow);
            } else
                view = getFeedsJobsView(feedsRow);
        }
        FeedStorage feedStorage = new FeedStorage(view, vIndex, feedsRow);
        if (player != null && styledPlayerView != null)
            feedStorage.setVideoResource(player, styledPlayerView);
        feedsViews.add(vIndex, feedStorage);
        mainLinearLayout.addView(view, vIndex);
        if (mainLinearLayoutChange != null)
            mainLinearLayoutChange.itemAddEditChange(mainLinearLayout);
    }

    public View getFeedsPhotoView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_photos_layout, null);
        view.setTag(feedsRow.feedMasterId);
        setFeedsTitleCommon(view, loginUserRow, feedsRow, isChkClose);
        setFeedsLikeCommentShareCommon(view, loginUserRow, feedsRow, isChkClose);
        TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
        feeds_content_txt.setText(feedsRow.tblMediaPost.ptTitle);

//        ImageView feeds_photo = view.findViewById(R.id.feeds_photo);
        SimpleDraweeView feeds_photo = view.findViewById(R.id.feeds_photo);
        Glide.with(context).load(feedImgPath + feedsRow.tblMediaPost.mediaFiles).placeholder(R.drawable.feeds_photos_default).into(feeds_photo);
//        Uri uri = Uri.parse(feedImgPath + feedsRow.tblMediaPost.mediaFiles);
//        feeds_photo.setImageURI(uri);
        return view;
    }

    public View getFeedsMultiplePhotosView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_multi_photos_layout, null);
        view.setTag(feedsRow.feedMasterId);
        setFeedsTitleCommon(view, loginUserRow, feedsRow, isChkClose);
        setFeedsLikeCommentShareCommon(view, loginUserRow, feedsRow, isChkClose);
        TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
        feeds_content_txt.setText(feedsRow.tblMediaPost.ptTitle);
        //set multiple images
        String imgs[] = feedsRow.tblMediaPost.mediaFiles.split(",");
        CustomPager feeds_view_pager = view.findViewById(R.id.feeds_view_pager);
        CircleIndicator feeds_circleIndicator_view = view.findViewById(R.id.feeds_circleIndicator_view);
        feeds_view_pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imgs.length;
            }

            int mCurrentPosition = -1;

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.setPrimaryItem(container, position, object);
                if (position != mCurrentPosition) {
                    FrameLayout fragment = (FrameLayout) object;
                    CustomPager pager = (CustomPager) container;
                    if (fragment != null) {
                        mCurrentPosition = position;
                        pager.measureCurrentView(fragment);
                    }
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = layoutInflater.inflate(R.layout.view_pager_img_item, container, false);
                SimpleDraweeView imageView = view.findViewById(R.id.img_view);
                Glide.with(context).load(feedImgPath + imgs[position]).into(imageView);
//                Uri uri = Uri.parse(feedImgPath + imgs[position]);
//                imageView.setImageURI(uri);
                container.addView(view);
                return view;
            }
        });
        feeds_view_pager.getParent().requestDisallowInterceptTouchEvent(true);
        feeds_circleIndicator_view.setViewPager(feeds_view_pager);
        return view;
    }

    private static Cache cache = null;

    public View getFeedsVideoView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_video_layout, null);
        view.setTag(feedsRow.feedMasterId);
        setFeedsTitleCommon(view, loginUserRow, feedsRow, isChkClose);
        setFeedsLikeCommentShareCommon(view, loginUserRow, feedsRow, isChkClose);
        TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
        feeds_content_txt.setText(feedsRow.tblMediaPost.ptTitle);
        //set player
        ImageView play_btn = view.findViewById(R.id.play_btn);
        StyledPlayerView feed_video = view.findViewById(R.id.feed_video);
        feed_video.setTag("stop");
        ExoPlayer player = new ExoPlayer.Builder(context).build();

        if (cache == null)
            cache = new SimpleCache(new File(context.getCacheDir(), "randomFeeds"), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 50));
//        Cache cache = new SimpleCache(new File(context.getCacheDir(), "random" + feedsRow.tblMediaPost.mediaFiles), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 50));
        DefaultHttpDataSource.Factory factory = new DefaultHttpDataSource.Factory();
        factory.setUserAgent(Util.getUserAgent(context, context.getPackageName()));

        CacheDataSource.Factory factoryCache = new CacheDataSource.Factory();
        factoryCache.setCache(cache);
        factoryCache.setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        factoryCache.setUpstreamDataSourceFactory(factory);

        MediaItem mediaItem = MediaItem.fromUri(feedImgPath + feedsRow.tblMediaPost.mediaFiles);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(factoryCache).createMediaSource(mediaItem);
        player.addMediaSource(mediaSource);
        player.prepare();
        feed_video.setPlayer(player);
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_btn.setVisibility(View.GONE);
                player.play();
                feed_video.setUseController(true);
                feed_video.setTag("play");
            }
        });

        playerHashMap.put(feedsRow.feedMasterId, player);
        styledPlayerViewHashMap.put(feedsRow.feedMasterId, feed_video);
        return view;
    }

    public View getFeedsContentView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_content_layout, null);
        view.setTag(feedsRow.feedMasterId);
        setFeedsTitleCommon(view, loginUserRow, feedsRow, isChkClose);
        setFeedsLikeCommentShareCommon(view, loginUserRow, feedsRow, isChkClose);
        TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
        feeds_content_txt.setText(feedsRow.tblTextPost.ptTitle);
        return view;
    }

    public View getFeedsContentLinkView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_content_link_layout, null);
        view.setTag(feedsRow.feedMasterId);
        setFeedsTitleCommon(view, loginUserRow, feedsRow, isChkClose);
        setFeedsLikeCommentShareCommon(view, loginUserRow, feedsRow, isChkClose);
        TextView feeds_content_txt = view.findViewById(R.id.feeds_content_txt);
        feeds_content_txt.setText(feedsRow.tblTextPost.ptTitle);

        String linkMetaArr = feedsRow.tblTextPost.ptHashDesc;
//        String linkMetaArr = "{\"link\":\"https://getbootstrap.com/\",
//        \"dataSrc\":\"https://getbootstrap.com/docs/5.3/assets/brand/bootstrap-social.png\",
//        \"linkTitle\":\"Bootstrap · The most popular HTML, CSS, and JS library in the world.\"}";
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Log.e(LogTag.EXCEPTION,"json link :"+linkMetaArr);
        HashMap<String, String> hashMap = new Gson().fromJson(linkMetaArr, type);

        MaterialCardView link_details_cv = view.findViewById(R.id.link_details_cv);
        link_details_cv.setVisibility(View.GONE);
        if (hashMap.get("link") != null && !hashMap.get("link").equalsIgnoreCase("")) {
            link_details_cv.setVisibility(View.VISIBLE);
            link_details_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction._OpenLink(context, hashMap.get("link"));
                }
            });
            SimpleDraweeView link_img = view.findViewById(R.id.link_img);
            link_img.setVisibility(View.GONE);
            if (hashMap.get("dataSrc") != null && !hashMap.get("dataSrc").equalsIgnoreCase("")) {
                link_img.setVisibility(View.VISIBLE);
                Glide.with(context).load(hashMap.get("dataSrc")).into(link_img);
//                Uri uri = Uri.parse(hashMap.get("dataSrc"));
//                link_img.setImageURI(uri);
            }

            TextView link_title = view.findViewById(R.id.link_title);
            link_title.setVisibility(View.GONE);
            if (hashMap.get("linkTitle") != null && !hashMap.get("linkTitle").equalsIgnoreCase("")) {
                link_title.setVisibility(View.VISIBLE);
                link_title.setText(hashMap.get("linkTitle"));
            }
            TextView link_url = view.findViewById(R.id.link_url);
            link_url.setText(hashMap.get("link"));
        }
        return view;
    }

    public View getFeedsLinkView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_link_layout, null);
        view.setTag(feedsRow.feedMasterId);
        setFeedsTitleCommon(view, loginUserRow, feedsRow, isChkClose);
        setFeedsLikeCommentShareCommon(view, loginUserRow, feedsRow, isChkClose);

        String linkMetaArr = feedsRow.tblTextPost.ptHashDesc;
//        String linkMetaArr = "{\"link\":\"https://getbootstrap.com/\",
//        \"dataSrc\":\"https://getbootstrap.com/docs/5.3/assets/brand/bootstrap-social.png\",
//        \"linkTitle\":\"Bootstrap · The most popular HTML, CSS, and JS library in the world.\"}";
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> hashMap = new Gson().fromJson(linkMetaArr, type);

        MaterialCardView link_details_cv = view.findViewById(R.id.link_details_cv);
        link_details_cv.setVisibility(View.GONE);
        if (hashMap.get("link") != null && !hashMap.get("link").equalsIgnoreCase("")) {
            link_details_cv.setVisibility(View.VISIBLE);
            link_details_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction._OpenLink(context, hashMap.get("link"));
                }
            });
            SimpleDraweeView link_img = view.findViewById(R.id.link_img);
            link_img.setVisibility(View.GONE);
            if (hashMap.get("dataSrc") != null && !hashMap.get("dataSrc").equalsIgnoreCase("")) {
                link_img.setVisibility(View.VISIBLE);
                Glide.with(context).load(hashMap.get("dataSrc")).into(link_img);
//                Uri uri = Uri.parse(hashMap.get("dataSrc"));
//                link_img.setImageURI(uri);
            }

            TextView link_title = view.findViewById(R.id.link_title);
            link_title.setVisibility(View.GONE);
            if (hashMap.get("linkTitle") != null && !hashMap.get("linkTitle").equalsIgnoreCase("")) {
                link_title.setVisibility(View.VISIBLE);
                link_title.setText(hashMap.get("linkTitle"));
            }
            TextView link_url = view.findViewById(R.id.link_url);
            link_url.setText(hashMap.get("link"));
        }
        return view;
    }

    public View getFeedsEventView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_event_layout, null);
        view.setTag(feedsRow.feedMasterId);

        SimpleDraweeView event_img = view.findViewById(R.id.event_img);
        Glide.with(context).load(feedImgPath + feedsRow.tblJobEvent.eventImg).into(event_img);
//        Uri uri = Uri.parse(feedImgPath + feedsRow.tblJobEvent.eventImg);
//        event_img.setImageURI(uri);
        TextView event_nm = view.findViewById(R.id.event_nm);
        event_nm.setText(feedsRow.tblJobEvent.title);

        View.OnClickListener fullViewFeeds = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedFullViewActivity.class);
                intent.putExtra("feed_master_id", feedsRow.feedMasterId);
                context.startActivity(intent);
            }
        };
        event_img.setOnClickListener(fullViewFeeds);
        event_nm.setOnClickListener(fullViewFeeds);
        setEventLabel(view, feedsRow.tblJobEvent.endDate, feedsRow.tblJobEvent.startDate);

        TextView event_time = view.findViewById(R.id.event_time);
        String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "EE, MMM dd, hh:mma", feedsRow.tblJobEvent.startDate);
        String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "EE, MMM dd, hh:mma", feedsRow.tblJobEvent.endDate);
        event_time.setText(startTime + " - " + endTime);
        ImageView event_online_offline_iv = view.findViewById(R.id.event_online_offline_iv);
        TextView event_online_offline_txt = view.findViewById(R.id.event_online_offline_txt);
        if (feedsRow.tblJobEvent.eventType.equalsIgnoreCase("ONLINE")) {
            event_online_offline_iv.setImageResource(R.drawable.camera_video);
            event_online_offline_txt.setText("Online");
        }
        TextView event_business_page_name_txt = view.findViewById(R.id.event_business_page_name_txt);
        event_business_page_name_txt.setText("By " + feedsRow.tblBusinessPage.busName);
        TextView event_desc = view.findViewById(R.id.event_desc);
        event_desc.setText(feedsRow.tblJobEvent.jobDescription);
        ImageView feeds_event_option_iv = view.findViewById(R.id.feeds_event_option_iv);
        feeds_event_option_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //feeds option open in bottom sheet dialog
                BottomSheetDialog feedsOptionDialog = new BottomSheetDialog(context);
                feedsOptionDialog.setContentView(R.layout.feeds_option_dialog_layout);
                LinearLayout feed_close = feedsOptionDialog.findViewById(R.id.feed_close);
                feed_close.setVisibility(View.GONE);
                LinearLayout feed_unfollow = feedsOptionDialog.findViewById(R.id.feed_unfollow);
                feed_unfollow.setVisibility(View.GONE);

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
                        //CommonFunction.PleaseWaitShow(context);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("feed_master_id", feedsRow.feedMasterId);
                        hashMap.put("isSave", isSave[0] ? 1 : 0);
                        apiInterface.FEEDS_OPTION_SAVE_UNSAVE(hashMap).enqueue(new MyApiCallback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status) {
                                            isSave[0] = normalCommonResponse.feedSave;
                                            if (isSave[0]) {
                                                feed_save_unsave_icon.setImageResource(R.drawable.feed_save_fill);
                                            } else {
                                                feed_save_unsave_icon.setImageResource(R.drawable.feed_save_blank);
                                            }
                                        } else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });

                LinearLayout feed_share = feedsOptionDialog.findViewById(R.id.feed_share);
                feed_share.setVisibility(View.VISIBLE);
                feed_share.setOnClickListener(v1 -> {
                    feedsOptionDialog.dismiss();
                    feedForwardDialog(feedsRow.feedMasterId);
                });

                LinearLayout feed_link_copy = feedsOptionDialog.findViewById(R.id.feed_link_copy);
                feed_link_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //link copy set (use link)
                        feedsOptionDialog.dismiss();
                        String link = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/feeds/" + feedsRow.feedLink;
                        CommonFunction.copyToClipBoard(context, link);
                    }
                });

                LinearLayout feed_report = feedsOptionDialog.findViewById(R.id.feed_report);
                feed_report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //send report data
                        userMaster.openReportDialog("Event", "feed_master", feedsRow.feedMasterId, context);
                        feedsOptionDialog.dismiss();
                    }
                });

                if (isJobEventEdit) {
                    feed_save_unsave.setVisibility(View.GONE);
                    feed_link_copy.setVisibility(View.GONE);
                    feed_report.setVisibility(View.GONE);

                    LinearLayout edit_event = feedsOptionDialog.findViewById(R.id.edit_event);
                    edit_event.setVisibility(View.VISIBLE);
                    edit_event.setOnClickListener(v1 -> {
                        openEditEventDialog(feedsRow.tblJobEvent.jobEventId, view);
                        feedsOptionDialog.dismiss();
                    });
                }

                feedsOptionDialog.show();
            }
        });
        return view;
    }

    public View getFeedsEventFullView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_event_full_layout, null);
        view.setTag(feedsRow.feedMasterId);

        SimpleDraweeView event_img = view.findViewById(R.id.event_img);
        Glide.with(context).load(feedImgPath + feedsRow.tblJobEvent.eventImg).into(event_img);
//        Uri uri = Uri.parse(feedImgPath + feedsRow.tblJobEvent.eventImg);
//        event_img.setImageURI(uri);
        TextView event_nm = view.findViewById(R.id.event_nm);
        event_nm.setText(feedsRow.tblJobEvent.title);

        setEventLabel(view, feedsRow.tblJobEvent.endDate, feedsRow.tblJobEvent.startDate);


        TextView event_time = view.findViewById(R.id.event_time);
        String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "EE, MMM dd, hh:mma", feedsRow.tblJobEvent.startDate);
        String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "EE, MMM dd, hh:mma", feedsRow.tblJobEvent.endDate);
        event_time.setText(startTime + " - " + endTime);

        ImageView event_online_offline_iv = view.findViewById(R.id.event_online_offline_iv);
        TextView event_online_offline_txt = view.findViewById(R.id.event_online_offline_txt);
        if (feedsRow.tblJobEvent.eventType.equalsIgnoreCase("ONLINE")) {
            event_online_offline_iv.setImageResource(R.drawable.camera_video);
            event_online_offline_txt.setText("Online");
        }
        TextView event_business_page_name_txt = view.findViewById(R.id.event_business_page_name_txt);
        event_business_page_name_txt.setText("By " + feedsRow.tblBusinessPage.busName);
        TextView event_desc = view.findViewById(R.id.event_desc);
        event_desc.setText(feedsRow.tblJobEvent.jobDescription);

        TextView location_or_broadcast_title_tv = view.findViewById(R.id.location_or_broadcast_title_tv);
        TextView location_or_broadcast_tv = view.findViewById(R.id.location_or_broadcast_tv);
        if (feedsRow.tblJobEvent.eventType.equalsIgnoreCase("ONLINE")) {
            location_or_broadcast_title_tv.setText("Broadcast Link");
            location_or_broadcast_tv.setText(feedsRow.tblJobEvent.contactNumber);
            location_or_broadcast_tv.setOnClickListener(v -> {
                CommonFunction._OpenLink(context, feedsRow.tblJobEvent.contactNumber);
            });
        } else {//Offline
            location_or_broadcast_title_tv.setText("Location");
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            HashMap<String, String> hashMap = new Gson().fromJson(feedsRow.tblJobEvent.addressJson, type);
            String location = "";
            if (hashMap.containsValue("address"))
                location = hashMap.get("address");
            if (hashMap.containsValue("city"))
                location += location.equalsIgnoreCase("") ? hashMap.get("city") : ", " + hashMap.get("city");
            if (hashMap.containsValue("region_country"))
                location += location.equalsIgnoreCase("") ? hashMap.get("region_country") : ", " + hashMap.get("region_country");
            location_or_broadcast_tv.setText(location);
        }
        //interest count
        LinearLayout interested_ll = view.findViewById(R.id.interested_ll);
        TextView count_interest_tv = view.findViewById(R.id.count_interest_tv);
        ImageView interest_iv = view.findViewById(R.id.interest_iv);
        final boolean isLike = Integer.parseInt(feedsRow.isLike) > 0;
        count_interest_tv.setText(feedsRow.likes);
        if (isLike) {
            interest_iv.setImageResource(R.drawable.account_multiple_check);
        }
        interested_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    int counter = Integer.parseInt(count_interest_tv.getText().toString());
                                    if (normalCommonResponse.msg.equalsIgnoreCase("Like!")) {
                                        interest_iv.setImageResource(R.drawable.account_multiple_check);
                                        count_interest_tv.setText(String.valueOf(counter + 1));
                                    } else {
                                        interest_iv.setImageResource(R.drawable.account_multiple);
                                        if (counter > 0)
                                            count_interest_tv.setText(String.valueOf(counter - 1));
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
        });
        return view;
    }

    private void setEventLabel(View view, String eventEndDate, String eventStartDate) {
        LinearLayout event_finish_ll = view.findViewById(R.id.event_finish_ll);
        ImageView event_ind_iv = view.findViewById(R.id.event_ind_iv);
        TextView event_ind_txt = view.findViewById(R.id.event_ind_txt);
        Date currDate = new Date();
        Date endDate = DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", eventEndDate);
        Date startDate = DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", eventStartDate);
        if (currDate.getTime() > endDate.getTime()) { //end
            event_ind_txt.setText("Event Finished");
            event_ind_txt.setTextColor(context.getColor(R.color.error));
            event_ind_iv.setImageResource(R.drawable.calendar_check);
        } else if (currDate.getTime() < endDate.getTime() && currDate.getTime() > startDate.getTime()) {//ongoing
            event_ind_txt.setText("Event Ongoing");
            event_ind_txt.setTextColor(context.getColor(R.color.success));
            event_ind_iv.setImageResource(R.drawable.calendar_ongoing);
        } else {//upcoming
            event_ind_txt.setText("Event Upcoming");
            event_ind_txt.setTextColor(context.getColor(R.color.info_dark));
            event_ind_iv.setImageResource(R.drawable.calendar_event_upcoming);
        }
        event_finish_ll.setVisibility(View.VISIBLE);
    }

    public View getFeedsJobsView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_jobs_layout, null);
        view.setTag(feedsRow.feedMasterId);
        View.OnClickListener fullViewFeeds = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedFullViewActivity.class);
                intent.putExtra("feed_master_id", feedsRow.feedMasterId);
                context.startActivity(intent);
            }
        };
        SimpleDraweeView job_business_iv = view.findViewById(R.id.job_business_iv);
        Glide.with(context).load(imgPath + feedsRow.tblBusinessPage.logo).into(job_business_iv);
//        Uri uri = Uri.parse(feedImgPath + feedsRow.tblBusinessPage.logo);
//        job_business_iv.setImageURI(uri);
        TextView job_title = view.findViewById(R.id.job_title);
        job_title.setText(feedsRow.tblJobPost.titlePost);
        job_title.setOnClickListener(fullViewFeeds);
        TextView job_business_page_nm_txt = view.findViewById(R.id.job_business_page_nm_txt);
        job_business_page_nm_txt.setText(feedsRow.tblBusinessPage.busName);
        TextView job_location = view.findViewById(R.id.job_location);
        job_location.setText(feedsRow.tblJobPost.jobLocation);
        ImageView feed_forward_icon = view.findViewById(R.id.feed_forward_icon);
        feed_forward_icon.setOnClickListener(v -> {
            feedForwardDialog(feedsRow.feedMasterId);
        });

        ImageView feed_save_unsave_icon = view.findViewById(R.id.feed_save_unsave_icon);
        final boolean[] isSave = {Integer.parseInt(feedsRow.isSave) > 0};
        if (isSave[0]) {
            feed_save_unsave_icon.setImageResource(R.drawable.feed_save_fill);
        }
        feed_save_unsave_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call feeds save unsave api and set related icon (use feedMasterId / isSave)
                //CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("user_master_id", sessionPref.getUserMasterId());
                hashMap.put("apiKey", sessionPref.getApiKey());
                hashMap.put("feed_master_id", feedsRow.feedMasterId);
                hashMap.put("isSave", isSave[0] ? 1 : 0);
                apiInterface.FEEDS_OPTION_SAVE_UNSAVE(hashMap).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    isSave[0] = normalCommonResponse.feedSave;
                                    if (isSave[0]) {
                                        feed_save_unsave_icon.setImageResource(R.drawable.feed_save_fill);
                                    } else {
                                        feed_save_unsave_icon.setImageResource(R.drawable.feed_save_blank);
                                    }
                                } else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        ImageView feed_close_icon = view.findViewById(R.id.feed_close_icon);
        if (!feedListBy.equalsIgnoreCase("JobsEvent") && !isChkClose) {
            feed_close_icon.setVisibility(View.GONE);
        }
        feed_close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call close api and remove feeds in list (use feedMasterId)
                //CommonFunction.PleaseWaitShow(context);
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
        TextView job_salary = view.findViewById(R.id.job_salary);
        job_salary.setText(feedsRow.tblJobPost.salaryCurrency + " " + CommonFunction.convertAmountUnitForm(Double.parseDouble(feedsRow.tblJobPost.salary)));
        TextView job_salary_payroll = view.findViewById(R.id.job_salary_payroll);
        job_salary_payroll.setText("/ " + feedsRow.tblJobPost.salaryPayroll);
        MaterialButton job_apply_btn = view.findViewById(R.id.job_apply_btn);
        Date currDate = new Date();
        Date endDate = DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", feedsRow.tblJobPost.postExpire);
        if (currDate.getTime() > endDate.getTime()) {//is expire
            job_apply_btn.setEnabled(false);
            job_apply_btn.setText("Job is expire");
        } else if (Integer.parseInt(feedsRow.tblJobPost.isApplide) > 0) {//is already applied
            job_apply_btn.setEnabled(false);
            job_apply_btn.setText("Applied");
        } else {
            job_apply_btn.setEnabled(true);
            job_apply_btn.setText("Apply");
            job_apply_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("job_post_id", feedsRow.tblJobPost.jobPostId);
                    apiInterface.JOB_APPLY(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        job_apply_btn.setEnabled(false);
                                        job_apply_btn.setText("Applied");
                                    } else
                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        }
        if (isJobEventEdit) {
            job_apply_btn.setEnabled(true);
            job_apply_btn.setText("Edit");
            job_apply_btn.setOnClickListener(v -> {
                //open edit popup
                openEditJobDialog(feedsRow.tblJobPost.jobPostId, view);
            });
        }
        FlexboxLayout job_skill_tag_fbl = view.findViewById(R.id.job_skill_tag_fbl);
        String skills[] = feedsRow.tblJobPost.skills.split(",");
        for (String skill : skills) {
            View skillLayout = layoutInflater.inflate(R.layout.skill_tag_item, null);
            TextView skill_item = skillLayout.findViewById(R.id.skill_item);
            skill_item.setText(skill);
            job_skill_tag_fbl.addView(skillLayout);
        }
        return view;
    }

    public View getFeedsJobFullView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_job_full_layout, null);
        view.setTag(feedsRow.feedMasterId);

        ImageView job_business_iv = view.findViewById(R.id.job_business_iv);
        Glide.with(context).load(imgPath + feedsRow.tblBusinessPage.logo).into(job_business_iv);
//        Uri uri = Uri.parse(feedImgPath + feedsRow.tblBusinessPage.logo);
//        job_business_iv.setImageURI(uri);
        TextView job_title = view.findViewById(R.id.job_title);
        job_title.setText(feedsRow.tblJobPost.titlePost);

        TextView job_business_page_nm_txt = view.findViewById(R.id.job_business_page_nm_txt);
        job_business_page_nm_txt.setText(feedsRow.tblBusinessPage.busName);

        TextView job_location = view.findViewById(R.id.job_location);
        job_location.setText(feedsRow.tblJobPost.jobLocation);

        TextView feed_time = view.findViewById(R.id.feed_time);
        feed_time.setText(feedTimeCount(feedsRow.createDate));

        MaterialButton open_page_mbtn = view.findViewById(R.id.open_page_mbtn);
        open_page_mbtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, BusinessActivity.class);
            intent.putExtra("business_page_id", feedsRow.tblJobPost.businessPageId);
            context.startActivity(intent);
        });

        TextView description_tv = view.findViewById(R.id.description_tv);
        description_tv.setText(feedsRow.tblJobPost.jobDescription);

        TextView location_tv = view.findViewById(R.id.location_tv);
        location_tv.setText(feedsRow.tblJobPost.jobType.equalsIgnoreCase("On-Site") ? feedsRow.tblJobPost.jobLocation : "Remote");

        TextView position_tv = view.findViewById(R.id.position_tv);
        position_tv.setText(feedsRow.tblJobPost.titlePost);

        TextView hour_time_tv = view.findViewById(R.id.hour_time_tv);
        hour_time_tv.setText(feedsRow.tblJobPost.jobTime);

        TextView experience_tv = view.findViewById(R.id.experience_tv);
        experience_tv.setText(feedsRow.tblJobPost.requirements);

        TextView no_of_vacancies_tv = view.findViewById(R.id.no_of_vacancies_tv);
        no_of_vacancies_tv.setText(feedsRow.tblJobPost.noOfVacancies);

        TextView job_salary = view.findViewById(R.id.job_salary);
        job_salary.setText(feedsRow.tblJobPost.salaryCurrency + " " + CommonFunction.convertAmountUnitForm(Double.parseDouble(feedsRow.tblJobPost.salary)) + "/ " + feedsRow.tblJobPost.salaryPayroll);

        FlexboxLayout job_skill_tag_fbl = view.findViewById(R.id.job_skill_tag_fbl);
        String skills[] = feedsRow.tblJobPost.skills.split(",");
        for (String skill : skills) {
            View skillLayout = layoutInflater.inflate(R.layout.skill_tag_item, null);
            TextView skill_item = skillLayout.findViewById(R.id.skill_item);
            skill_item.setText(skill);
            job_skill_tag_fbl.addView(skillLayout);
        }

        MaterialButton job_apply_btn = view.findViewById(R.id.job_apply_btn);
        Date currDate = new Date();
        Date endDate = DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", feedsRow.tblJobPost.postExpire);
        if (currDate.getTime() > endDate.getTime()) {//is expire
            job_apply_btn.setEnabled(false);
            job_apply_btn.setText("Job is expire");
        } else if (Integer.parseInt(feedsRow.tblJobPost.isApplide) > 0) {//is already applied
            job_apply_btn.setEnabled(false);
            job_apply_btn.setText("Applied");
        } else {
            job_apply_btn.setEnabled(true);
            job_apply_btn.setText("Apply");
            job_apply_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("job_post_id", feedsRow.tblJobPost.jobPostId);
                    apiInterface.JOB_APPLY(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        job_apply_btn.setEnabled(false);
                                        job_apply_btn.setText("Applied");
                                    } else
                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        }

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
                //CommonFunction.PleaseWaitShow(context);
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
                                    int counter = Integer.parseInt(like_counter_txt.getText().toString());
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
                loadCommentList(nestedScrollView_commentList, comment_ll, loginUserRow, feedsRow, comment_counter_txt);
                //comment place submit
                ImageView loginUser_pic = view.findViewById(R.id.loginUser_pic);
                Glide.with(context).load(imgPath + loginUserRow.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(loginUser_pic);
                EditText comment_input = view.findViewById(R.id.comment_input);
                ImageView comment_submit_iv = view.findViewById(R.id.comment_submit_iv);
                comment_submit_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //CommonFunction.PleaseWaitShow(context);
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
                                            loadCommentList(nestedScrollView_commentList, comment_ll, loginUserRow, feedsRow, comment_counter_txt);
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
                feedForwardDialog(feedsRow.feedMasterId);
            }
        });
    }

    private void loadCommentList(NestedScrollView nestedScrollView_commentList, LinearLayout commentLl, UserRowResponse.Dt loginUserRow, FeedsRow feedsRow, TextView comment_counter_txt) {
        commentLl.removeAllViews();
        //CommonFunction.PleaseWaitShow(context);
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
                            if (comment_counter_txt != null) {
                                comment_counter_txt.setText(String.valueOf(feedsCommentListResponse.totalComment));
                            }
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

                                    View.OnClickListener openUserProfile = v -> {
                                        Intent intent = new Intent(context, ProfileActivity.class);
                                        intent.putExtra("user_master_id", commentsRow.userMasterId);
                                        context.startActivity(intent);
                                    };
                                    user_pic.setOnClickListener(openUserProfile);
                                    userFullName_txt.setOnClickListener(openUserProfile);

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
        LinearLayout with_user_dt_ll = view.findViewById(R.id.with_user_dt_ll);
        TextView user_fullname_txt = view.findViewById(R.id.user_fullname_txt);
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
        if (feedsRow.userMasterId.equalsIgnoreCase(loginUserRow.userMasterId) && feedsRow.feedFor.equalsIgnoreCase("USER")) {
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
        with_user_dt_ll.setVisibility(View.GONE);
        if (feedsRow.feedFor.equalsIgnoreCase("COMMUNITY")) {
            title_gMember_view.setVisibility(View.VISIBLE);
            with_user_dt_ll.setVisibility(View.VISIBLE);
            user_fullname_txt.setText(feedsRow.name);
            Glide.with(context).load(imgPath + feedsRow.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(title_gMember_pic);
            View.OnClickListener openUserProfile = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("user_master_id", feedsRow.userMasterId);
                    context.startActivity(i);
                }
            };
            title_gMember_view.setOnClickListener(openUserProfile);
            user_fullname_txt.setOnClickListener(openUserProfile);
        }

        Glide.with(context).load(imgPath + feedsProPic).centerCrop().placeholder(defaultPic).into(feeds_title_img);
        fullname_txt.setText(feedsProName);
        time_ago_txt.setText(feedTimeCount(feedsRow.createDate));
        Intent finalClickPicNm = clickPicNm;
        View.OnClickListener openTitleProfile = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalClickPicNm != null) context.startActivity(finalClickPicNm);
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
                            //CommonFunction.PleaseWaitShow(context);
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
                                                feedsOptionDialog.dismiss();
                                            } else
                                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
                LinearLayout feed_delete = feedsOptionDialog.findViewById(R.id.feed_delete);
                feed_delete.setVisibility(View.GONE);
                if (sessionPref.getUserMasterId().equalsIgnoreCase(feedsRow.userMasterId)) {
                    feed_delete.setVisibility(View.VISIBLE);
                    feed_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //call close api and remove feeds in list (use feedMasterId)
                            //CommonFunction.PleaseWaitShow(context);
                            HashMap hashMap = new HashMap();
                            hashMap.put("user_master_id", sessionPref.getUserMasterId());
                            hashMap.put("apiKey", sessionPref.getApiKey());
                            hashMap.put("feed_master_id", feedsRow.feedMasterId);
                            apiInterface.FEEDS_OPTION_DELETE(hashMap).enqueue(new MyApiCallback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    super.onResponse(call, response);
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                            if (normalCommonResponse.status) {
                                                removeFeedsInList(view);
                                                feedsOptionDialog.dismiss();
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
                        //CommonFunction.PleaseWaitShow(context);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("feed_master_id", feedsRow.feedMasterId);
                        hashMap.put("isSave", isSave[0] ? 1 : 0);
                        apiInterface.FEEDS_OPTION_SAVE_UNSAVE(hashMap).enqueue(new MyApiCallback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status) {
                                            isSave[0] = normalCommonResponse.feedSave;
                                            if (isSave[0]) {
                                                feedsRow.isSave = "1";
                                                feed_save_unsave_icon.setImageResource(R.drawable.feed_save_fill);
                                            } else {
                                                feedsRow.isSave = "0";
                                                feed_save_unsave_icon.setImageResource(R.drawable.feed_save_blank);
                                                if (loadOnSavePage) {
                                                    removeFeedsInList(view);
                                                }
                                            }
                                            feedsOptionDialog.dismiss();
                                        } else
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
                        feedsOptionDialog.dismiss();
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
                            //CommonFunction.PleaseWaitShow(context);
                            HashMap hashMap = new HashMap();
                            hashMap.put("user_master_id", sessionPref.getUserMasterId());
                            hashMap.put("apiKey", sessionPref.getApiKey());
                            hashMap.put("profile_id", finalFeedsRowOptionalId);
                            hashMap.put("profile_type", feedsRow.feedFor);
                            apiInterface.FEEDS_OPTION_UNFOLLOW_PROFILE(hashMap).enqueue(new MyApiCallback() {
                                @Override
                                public void onResponse(Call call, Response response) {
                                    super.onResponse(call, response);
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                            if (normalCommonResponse.status) {
                                                removeFeedsInList(view);
                                                feedsOptionDialog.dismiss();
                                            } else
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
                        userMaster.openReportDialog("Post", "feed_master", feedsRow.feedMasterId, context);
                        feedsOptionDialog.dismiss();
                    }
                });
                feedsOptionDialog.show();
            }
        });
    }

    /**
     * job / event edit popup dialog
     **/

    public ActivityResultLauncher activityResultLauncherForEventBanner;
    public ActivityResultCallback activityResultCallbackForEventBanner = new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri photoUri) {
            if (photoUri != null && event_banner_iv != null) {
                eventBannerFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(eventBannerFile).centerCrop().placeholder(R.drawable.user_default_banner).into(event_banner_iv);
            }
        }
    };
    File eventBannerFile;
    String oldEventImage = null;
    ImageView event_banner_iv;
    private Calendar startTimeCalendar, endTimeCalendar;

    private void openEditEventDialog(String jobEventId, View feedView) {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.event_add_edit_manage_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);
        String[] eventType = context.getResources().getStringArray(R.array.event_type);
        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView title = dialog.findViewById(R.id.title);
        title.setText("Create a event");

        MaterialCardView event_banner_edit = dialog.findViewById(R.id.event_banner_edit);
        event_banner_edit.setOnClickListener(v -> {
            if (activityResultLauncherForEventBanner != null)
                activityResultLauncherForEventBanner.launch(("image/*"));
        });
        event_banner_iv = dialog.findViewById(R.id.event_banner_iv);


        EditText event_title_et = dialog.findViewById(R.id.event_title_et);

        EditText start_time_et = dialog.findViewById(R.id.start_time_et);
        startTimeCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", DateUtils.TODAYDATETIMEforDB()));
        start_time_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                    startTimeCalendar.set(Calendar.YEAR, year);
                    startTimeCalendar.set(Calendar.MONTH, month);
                    startTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTimeCalendar.set(Calendar.MINUTE, minute);

                    Date date = DateUtils.toDate(startTimeCalendar);
                    start_time_et.setText(DateUtils.getStringDate("dd-MMM-yyyy hh:mm a", date));
                }, startTimeCalendar.get(Calendar.HOUR_OF_DAY), startTimeCalendar.get(Calendar.MINUTE), false).show();

            }, startTimeCalendar.get(Calendar.YEAR), startTimeCalendar.get(Calendar.MONTH), startTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        EditText end_time_et = dialog.findViewById(R.id.end_time_et);
        endTimeCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", DateUtils.TODAYDATETIMEforDB()));
        end_time_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                    endTimeCalendar.set(Calendar.YEAR, year);
                    endTimeCalendar.set(Calendar.MONTH, month);
                    endTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endTimeCalendar.set(Calendar.MINUTE, minute);

                    Date date = DateUtils.toDate(endTimeCalendar);
                    end_time_et.setText(DateUtils.getStringDate("dd-MMM-yyyy hh:mm a", date));
                }, endTimeCalendar.get(Calendar.HOUR_OF_DAY), endTimeCalendar.get(Calendar.MINUTE), false).show();

            }, endTimeCalendar.get(Calendar.YEAR), endTimeCalendar.get(Calendar.MONTH), endTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText time_zone_et = dialog.findViewById(R.id.time_zone_et);

        LinearLayout broadcast_link_ll = dialog.findViewById(R.id.broadcast_link_ll);
        EditText broadcast_link_et = dialog.findViewById(R.id.broadcast_link_et);

        LinearLayout address_ll = dialog.findViewById(R.id.address_ll);
        address_ll.setVisibility(View.GONE);
        EditText address_et = dialog.findViewById(R.id.address_et);

        LinearLayout city_ll = dialog.findViewById(R.id.city_ll);
        city_ll.setVisibility(View.GONE);
        EditText city_et = dialog.findViewById(R.id.city_et);

        LinearLayout country_region_ll = dialog.findViewById(R.id.country_region_ll);
        country_region_ll.setVisibility(View.GONE);
        EditText country_region_et = dialog.findViewById(R.id.country_region_et);

        Spinner event_type_sp = dialog.findViewById(R.id.event_type_sp);
        event_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                broadcast_link_ll.setVisibility(View.VISIBLE);
                address_ll.setVisibility(View.GONE);
                city_ll.setVisibility(View.GONE);
                country_region_ll.setVisibility(View.GONE);
                if (position != 0) {
                    broadcast_link_ll.setVisibility(View.GONE);
                    address_ll.setVisibility(View.VISIBLE);
                    city_ll.setVisibility(View.VISIBLE);
                    country_region_ll.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        EditText description_et = dialog.findViewById(R.id.description_et);
        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setVisibility(View.GONE);

        if (jobEventId != null) {
            title.setText("Edit a job");
            remove_mbtn.setVisibility(View.VISIBLE);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("job_event_id", jobEventId);
            apiInterface.PAGES_OUR_EVENT_POST_ROW(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful())
                        if (response.body() != null) {
                            OurEventPostRow ourEventPostRow = (OurEventPostRow) response.body();
                            if (ourEventPostRow.status) {
                                if (ourEventPostRow.dt.eventImg != null) {
                                    Glide.with(context).load(ourEventPostRow.imgPath + ourEventPostRow.dt.eventImg).centerCrop().placeholder(R.drawable.user_default_banner).into(event_banner_iv);
                                    oldEventImage = ourEventPostRow.dt.eventImg;
                                }

                                if (ourEventPostRow.dt.title != null)
                                    event_title_et.setText(ourEventPostRow.dt.title);

                                if (ourEventPostRow.dt.eventType != null) {
                                    event_type_sp.setSelection(Arrays.asList(eventType).indexOf(ourEventPostRow.dt.eventType));
                                    broadcast_link_ll.setVisibility(View.VISIBLE);
                                    address_ll.setVisibility(View.GONE);
                                    city_ll.setVisibility(View.GONE);
                                    country_region_ll.setVisibility(View.GONE);
                                    if (!ourEventPostRow.dt.eventType.equalsIgnoreCase("ONLINE")) {
                                        broadcast_link_ll.setVisibility(View.GONE);
                                        address_ll.setVisibility(View.VISIBLE);
                                        city_ll.setVisibility(View.VISIBLE);
                                        country_region_ll.setVisibility(View.VISIBLE);
                                    }
                                }

                                if (ourEventPostRow.dt.startDate != null) {
                                    start_time_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy hh:mm a", ourEventPostRow.dt.startDate));
                                    startTimeCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", ourEventPostRow.dt.startDate));
                                }
                                if (ourEventPostRow.dt.endDate != null) {
                                    end_time_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy hh:mm a", ourEventPostRow.dt.endDate));
                                    endTimeCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", ourEventPostRow.dt.endDate));
                                }

                                if (ourEventPostRow.dt.timeZone != null)
                                    time_zone_et.setText(ourEventPostRow.dt.timeZone);

                                if (ourEventPostRow.dt.contactNumber != null)
                                    broadcast_link_et.setText(ourEventPostRow.dt.contactNumber);

                                if (ourEventPostRow.dt.addressJson != null) {
                                    OurEventPostRow.Dt.AddressJson addressJson = new Gson().fromJson(ourEventPostRow.dt.addressJson, OurEventPostRow.Dt.AddressJson.class);

                                    if (addressJson.address != null)
                                        address_et.setText(addressJson.address);
                                    if (addressJson.city != null)
                                        city_et.setText(addressJson.city);
                                    if (addressJson.regionCountry != null)
                                        country_region_et.setText(addressJson.regionCountry);
                                }

                                if (ourEventPostRow.dt.jobDescription != null)
                                    description_et.setText(ourEventPostRow.dt.jobDescription);
                            }
                        }
                }
            });

            remove_mbtn.setOnClickListener(v -> {
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap2 = new HashMap();
                hashMap2.put("user_master_id", sessionPref.getUserMasterId());
                hashMap2.put("apiKey", sessionPref.getApiKey());
                hashMap2.put("job_event_id", jobEventId);
                hashMap2.put("business_page_id", feedForId);//pass by setFeedForId
                apiInterface.PAGES_REMOVE_OUR_EVENT_POST(hashMap2).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    dialog.dismiss();
                                    if (feedView != null)
                                        removeFeedsInList(feedView);
                                }
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            });
        }

        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            //add or edit call
            CommonFunction.PleaseWaitShow(context);
            CommonFunction.PleaseWaitShowMessage("Files is compressing...");
            try {
                if (eventBannerFile == null) {
                    Toast.makeText(context, "Please select logo!", Toast.LENGTH_SHORT).show();
                    return;
                }
                File imgFileEvent = new Compressor(context)
                        .setMaxWidth(1080)
                        .setMaxWidth(800)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                        .compressToFile(eventBannerFile);

                CommonFunction.PleaseWaitShowMessage("Files is compressed completed");

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                        .addFormDataPart("apiKey", sessionPref.getApiKey())

                        .addFormDataPart("frm_business_page_id", feedForId)//pass by setFeedForId
                        .addFormDataPart("title", event_title_et.getText().toString())
                        .addFormDataPart("event_type", Arrays.asList(eventType).get(event_type_sp.getSelectedItemPosition()))
                        .addFormDataPart("start_date", DateUtils.getStringDate("dd-MMM-yyyy hh:mm a", "yyyy-MM-dd HH:mm:ss", start_time_et.getText().toString()))
                        .addFormDataPart("end_date", DateUtils.getStringDate("dd-MMM-yyyy hh:mm a", "yyyy-MM-dd HH:mm:ss", end_time_et.getText().toString()))
                        .addFormDataPart("time_zone", time_zone_et.getText().toString())
                        .addFormDataPart("contact_number", broadcast_link_et.getText().toString())
                        .addFormDataPart("address", address_et.getText().toString())
                        .addFormDataPart("city", city_et.getText().toString())
                        .addFormDataPart("region_country", country_region_et.getText().toString())
                        .addFormDataPart("job_description", description_et.getText().toString());


                if (jobEventId != null) {
                    builder.addFormDataPart("job_event_id", jobEventId);
                    if (oldEventImage != null)
                        builder.addFormDataPart("old_event_img", oldEventImage);
                }

                builder.addFormDataPart("event_img", imgFileEvent.getName(),
                        RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFileEvent)), imgFileEvent));

                RequestBody body = builder.build();
                CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                apiInterface.PAGES_MANAGE_OUR_EVENT_POST(body).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    imgFileEvent.delete();
                                    dialog.dismiss();
                                    reloadOrAddTopFeeds();
                                }
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                CommonFunction.dismissDialog();
                Toast.makeText(context, "request send Fail! Image issue", Toast.LENGTH_LONG).show();
                Log.e(LogTag.EXCEPTION, "Image Compress from Business page create Exception", e);
            }
        });

        dialog.show();
        if (jobEventId != null) {
            CommonFunction.PleaseWaitShow(context);
        }
    }

    public void openAddEventDialog() {
        openEditEventDialog(null, null);
    }

    private Calendar expireDateCalendar;
    private List<String> fullSkills = null;
    private String slSkill = null;

    private void openEditJobDialog(String jobPostId, View feedView) {
        slSkill = null;
        fullSkills = null;
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_add_edit_manage_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        String[] jobType = context.getResources().getStringArray(R.array.job_type),
                jobTime = context.getResources().getStringArray(R.array.job_time),
                payRoll = context.getResources().getStringArray(R.array.job_payroll),
                currencySort = context.getResources().getStringArray(R.array.currency_sort);

        TextView title = dialog.findViewById(R.id.title);
        title.setText("Create a job");
        EditText job_position_et = dialog.findViewById(R.id.job_position_et);
        Spinner job_type_sp = dialog.findViewById(R.id.job_type_sp);
        Spinner job_time_sp = dialog.findViewById(R.id.job_time_sp);
        EditText location_et = dialog.findViewById(R.id.location_et);
        Spinner currency_sp = dialog.findViewById(R.id.currency_sp);
        Spinner payroll_sp = dialog.findViewById(R.id.payroll_sp);
        EditText job_salary_et = dialog.findViewById(R.id.job_salary_et);
        EditText experience_et = dialog.findViewById(R.id.experience_et);
        EditText no_of_vacancies_et = dialog.findViewById(R.id.no_of_vacancies_et);

        MultiSpinnerSearch skills_selected = dialog.findViewById(R.id.skills_selected);
        HashMap hashMapDefault = new HashMap();
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        apiInterface.GET_SKILL_TBL(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            fullSkills = normalCommonResponse.dt;

                            // Pass true If you want searchView above the list. Otherwise false. default = true.
                            skills_selected.setSearchEnabled(true);

                            // A text that will display in search hint.
                            skills_selected.setSearchHint("Select your skill");

                            // Set text that will display when search result not found...
                            skills_selected.setEmptyTitle("Not Data Found!");


                            //A text that will display in clear text button
                            skills_selected.setClearText("Close & Clear");
                            if (jobPostId == null) {
                                //create arrayList of key with select or not
                                List<KeyPairBoolData> listArray = new ArrayList<>();
                                for (String skillItem : normalCommonResponse.dt) {
                                    listArray.add(new KeyPairBoolData(skillItem, false));
                                }

                                // Removed second parameter, position. Its not required now..
                                // If you want to pass preselected items, you can do it while making listArray,
                                // Pass true in setSelected of any item that you want to preselect
                                skills_selected.setItems(listArray, new MultiSpinnerListener() {
                                    @Override
                                    public void onItemsSelected(List<KeyPairBoolData> items) {
                                        for (int i = 0; i < items.size(); i++) {
                                            if (items.get(i).isSelected()) {
                                                Log.d(LogTag.CHECK_DEBUG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                            }
                                        }
                                    }
                                });
                            } else if (jobPostId != null && slSkill != null && fullSkills != null) {
                                ArrayList<String> selectedSkill = new ArrayList<>();
                                //default selected
                                if (slSkill != null) {
                                    selectedSkill.addAll(Arrays.asList(slSkill.split(",")));
                                }
                                //add other skill / out of list
                                if (slSkill != null && fullSkills != null) {
                                    for (String iSkill : selectedSkill) {
                                        if (fullSkills.indexOf(iSkill) < 0) { //not find in full list
                                            fullSkills.add(iSkill);
                                        }
                                    }
                                }
                                //create arrayList of key with select or not
                                List<KeyPairBoolData> listArray = new ArrayList<>();
                                for (String skillItem : normalCommonResponse.dt) {
                                    if (selectedSkill.indexOf(skillItem) >= 0)
                                        listArray.add(new KeyPairBoolData(skillItem, true));
                                    else
                                        listArray.add(new KeyPairBoolData(skillItem, false));
                                }
                                skills_selected.setItems(listArray, new MultiSpinnerListener() {
                                    @Override
                                    public void onItemsSelected(List<KeyPairBoolData> items) {
                                        for (int i = 0; i < items.size(); i++) {
                                            if (items.get(i).isSelected()) {
                                                Log.d(LogTag.CHECK_DEBUG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

        EditText expire_et = dialog.findViewById(R.id.expire_et);
        expireDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        expire_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                expireDateCalendar.set(Calendar.YEAR, year);
                expireDateCalendar.set(Calendar.MONTH, month);
                expireDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(expireDateCalendar);
                expire_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, expireDateCalendar.get(Calendar.YEAR), expireDateCalendar.get(Calendar.MONTH), expireDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        EditText description_et = dialog.findViewById(R.id.description_et);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setVisibility(View.GONE);
        if (jobPostId != null) {
            //edit job
            title.setText("Edit a job");
            remove_mbtn.setVisibility(View.VISIBLE);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("job_post_id", jobPostId);
            apiInterface.PAGES_OUR_JOB_POST_ROW(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful())
                        if (response.body() != null) {
                            OurJobPostRow ourJobPostRow = (OurJobPostRow) response.body();
                            if (ourJobPostRow.status) {
                                if (ourJobPostRow.dt.titlePost != null)
                                    job_position_et.setText(ourJobPostRow.dt.titlePost);

                                if (ourJobPostRow.dt.jobType != null)
                                    job_type_sp.setSelection(Arrays.asList(jobType).indexOf(ourJobPostRow.dt.jobType));

                                if (ourJobPostRow.dt.jobTime != null)
                                    job_time_sp.setSelection(Arrays.asList(jobTime).indexOf(ourJobPostRow.dt.jobTime));

                                if (ourJobPostRow.dt.jobLocation != null)
                                    location_et.setText(ourJobPostRow.dt.jobLocation);

                                if (ourJobPostRow.dt.salaryCurrency != null)
                                    currency_sp.setSelection(Arrays.asList(currencySort).indexOf(ourJobPostRow.dt.salaryCurrency));

                                if (ourJobPostRow.dt.salaryPayroll != null)
                                    payroll_sp.setSelection(Arrays.asList(payRoll).indexOf(ourJobPostRow.dt.salaryPayroll));

                                if (ourJobPostRow.dt.salary != null)
                                    job_salary_et.setText(ourJobPostRow.dt.salary);

                                if (ourJobPostRow.dt.requirements != null)
                                    experience_et.setText(ourJobPostRow.dt.requirements);

                                if (ourJobPostRow.dt.noOfVacancies != null)
                                    no_of_vacancies_et.setText(ourJobPostRow.dt.noOfVacancies);

                                if (ourJobPostRow.dt.skills != null) {
                                    slSkill = ourJobPostRow.dt.skills;
                                    if (jobPostId != null && slSkill != null && fullSkills != null) {
                                        ArrayList<String> selectedSkill = new ArrayList<>();
                                        //default selected
                                        if (slSkill != null) {
                                            selectedSkill.addAll(Arrays.asList(slSkill.split(",")));
                                        }

                                        //add other skill / out of list
                                        if (slSkill != null && fullSkills != null) {
                                            for (String iSkill : selectedSkill) {
                                                if (fullSkills.indexOf(iSkill) < 0) { //not find in full list
                                                    fullSkills.add(iSkill);
                                                }
                                            }
                                        }

                                        //create arrayList of key with select or not
                                        List<KeyPairBoolData> listArray = new ArrayList<>();
                                        for (String skillItem : fullSkills) {
                                            if (selectedSkill.indexOf(skillItem) >= 0)
                                                listArray.add(new KeyPairBoolData(skillItem, true));
                                            else
                                                listArray.add(new KeyPairBoolData(skillItem, false));
                                        }
                                        skills_selected.setItems(listArray, new MultiSpinnerListener() {
                                            @Override
                                            public void onItemsSelected(List<KeyPairBoolData> items) {
                                                for (int i = 0; i < items.size(); i++) {
                                                    if (items.get(i).isSelected()) {
                                                        Log.d(LogTag.CHECK_DEBUG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                                if (ourJobPostRow.dt.postExpire != null) {
                                    expire_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy", ourJobPostRow.dt.postExpire));
                                    expireDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", ourJobPostRow.dt.postExpire));
                                }

                                if (ourJobPostRow.dt.salaryCurrency != null)
                                    description_et.setText(ourJobPostRow.dt.jobDescription);
                            }
                        }
                }
            });
        }
        save_mbtn.setOnClickListener(v -> {
            //add or edit call
            CommonFunction.PleaseWaitShow(context);
            ArrayList<String> selectedSkills = new ArrayList<>();
            for (KeyPairBoolData keyPairBoolData : skills_selected.getSelectedItems()) {
                selectedSkills.add(keyPairBoolData.getName());
            }

            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());

            if (jobPostId != null)
                hashMap.put("job_post_id", jobPostId);

            hashMap.put("frm_business_page_id", feedForId);//pass by setFeedForId
            hashMap.put("title_post", job_position_et.getText().toString());
            hashMap.put("job_type", Arrays.asList(jobType).get(job_type_sp.getSelectedItemPosition()));
            hashMap.put("job_time", Arrays.asList(jobTime).get(job_time_sp.getSelectedItemPosition()));
            hashMap.put("job_location", location_et.getText().toString());
            hashMap.put("salary_currency", Arrays.asList(currencySort).get(currency_sp.getSelectedItemPosition()));
            hashMap.put("salary_payroll", Arrays.asList(payRoll).get(payroll_sp.getSelectedItemPosition()));
            hashMap.put("salary", job_salary_et.getText().toString());
            hashMap.put("requirements", experience_et.getText().toString());//experience
            hashMap.put("no_of_vacancies", no_of_vacancies_et.getText().toString());//experience
            hashMap.put("skills", TextUtils.join(",", selectedSkills.toArray(new String[selectedSkills.size()])));
            hashMap.put("post_expire", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", expire_et.getText().toString()));
            hashMap.put("job_description", description_et.getText().toString());

            apiInterface.PAGES_MANAGE_OUR_JOB_POST(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                dialog.dismiss();
                                reloadOrAddTopFeeds();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
        if (jobPostId != null)
            remove_mbtn.setOnClickListener(v -> {
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("user_master_id", sessionPref.getUserMasterId());
                hashMap.put("apiKey", sessionPref.getApiKey());
                hashMap.put("job_post_id", jobPostId);
                hashMap.put("business_page_id", feedForId);//pass by setFeedForId
                apiInterface.PAGES_REMOVE_OUR_JOB_POST(hashMap).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    dialog.dismiss();
                                    if (feedView != null)
                                        removeFeedsInList(feedView);
                                }
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            });

        dialog.show();

        CommonFunction.PleaseWaitShow(context);
    }


    public void openAddJobDialog() {
        openEditJobDialog(null, null);
    }

    /**
     * common function for feeds
     **/

    public void feedForwardDialog(String feedMasterId) {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.feed_frwd_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);
        LayoutInflater inflater = LayoutInflater.from(context);

        dialog.show();

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        LinearLayout own_profile_ll = dialog.findViewById(R.id.own_profile_ll);
        UserRowResponse.Dt userRowResponse = sessionPref.getUserMasterRowInObject();
        View ownProfile = inflater.inflate(R.layout.user_pic_two_btn_list_item, null);

        ImageView member_profile_pic = ownProfile.findViewById(R.id.member_profile_pic);
        Glide.with(context).load(imgPath + userRowResponse.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);

        TextView member_tv = ownProfile.findViewById(R.id.member_tv);
        member_tv.setText(userRowResponse.name);

        MaterialButton first_mbtn = ownProfile.findViewById(R.id.first_mbtn);
        first_mbtn.setVisibility(View.GONE);

        MaterialButton second_mbtn = ownProfile.findViewById(R.id.second_mbtn);
        second_mbtn.setText("Forward");
        second_mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("user_master_id", sessionPref.getUserMasterId());
                hashMap.put("apiKey", sessionPref.getApiKey());
                hashMap.put("feed_master_id", feedMasterId);
                hashMap.put("feed_for", "USER");
                hashMap.put("feed_for_id", userRowResponse.userMasterId);
                apiInterface.FEEDS_SHARE_FORWARD(hashMap).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    //reload activity page OR add forwarded feed on top of the list
                                    dialog.dismiss();
                                    reloadOrAddTopFeeds();
                                } else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        own_profile_ll.addView(ownProfile);

        CommonFunction.PleaseWaitShow(context);
        LinearLayout business_main_ll = dialog.findViewById(R.id.business_main_ll);
        LinearLayout business_ll = dialog.findViewById(R.id.business_ll);

        LinearLayout groups_main_ll = dialog.findViewById(R.id.groups_main_ll);
        LinearLayout groups_ll = dialog.findViewById(R.id.groups_main_ll);

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");

        apiInterface.GET_MY_PAGES_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                //set group
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("user_master_id", sessionPref.getUserMasterId());
                hashMap.put("apiKey", sessionPref.getApiKey());
                hashMap.put("device", "ANDROID");
                //connectReqUsersMaster / connectUsers / followReqUsers / followerUsers / followingUsers / userCommunitys / userBusinessPages / userEvents /
                //suggestedCityUser / suggestedIndustryUser / suggestedGroup / suggestedBusPages
                hashMap.put("fnName", NetworkActivity.SeeAllFnName.userCommunitys.getVal());
                apiInterface.NETWORK_SEE_ALL_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        CommonFunction.dismissDialog();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NetworkSeeAllCommonResponse.CommunitysListResponse communitysListResponse = new Gson().fromJson(new Gson().toJson(response.body()), NetworkSeeAllCommonResponse.CommunitysListResponse.class);
                                if (communitysListResponse.status) {
                                    if (communitysListResponse.dt.size() > 0) {
                                        groups_main_ll.setVisibility(View.VISIBLE);
                                        for (NetworkSeeAllCommonResponse.CommunitysListResponse.Dt dt : communitysListResponse.dt) {
                                            View groupList = inflater.inflate(R.layout.user_pic_two_btn_list_item, null);
                                            ImageView member_profile_pic = groupList.findViewById(R.id.member_profile_pic);
                                            Glide.with(context).load(imgPath + dt.logo).placeholder(R.drawable.default_groups_pic).into(member_profile_pic);

                                            TextView member_tv = groupList.findViewById(R.id.member_tv);
                                            member_tv.setText(dt.name);

                                            MaterialButton first_mbtn = groupList.findViewById(R.id.first_mbtn);
                                            first_mbtn.setVisibility(View.GONE);

                                            MaterialButton second_mbtn = groupList.findViewById(R.id.second_mbtn);
                                            second_mbtn.setText("Forward");
                                            second_mbtn.setOnClickListener(v -> {
                                                CommonFunction.PleaseWaitShow(context);
                                                HashMap hashMap2 = new HashMap();
                                                hashMap2.put("user_master_id", sessionPref.getUserMasterId());
                                                hashMap2.put("apiKey", sessionPref.getApiKey());
                                                hashMap2.put("feed_master_id", feedMasterId);
                                                hashMap2.put("feed_for", "COMMUNITY");
                                                hashMap2.put("feed_for_id", dt.communityMasterId);
                                                apiInterface.FEEDS_SHARE_FORWARD(hashMap2).enqueue(new MyApiCallback() {
                                                    @Override
                                                    public void onResponse(Call call, Response response) {
                                                        super.onResponse(call, response);
                                                        if (response.isSuccessful()) {
                                                            if (response.body() != null) {
                                                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                                                if (normalCommonResponse.status) {
                                                                    //reload activity page OR add forwarded feed on top of the list
                                                                    dialog.dismiss();
                                                                    reloadOrAddTopFeeds();
                                                                } else
                                                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                            });

                                            groups_ll.addView(groupList);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });


                //set business
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MyPagesListResponse myPagesListResponse = (MyPagesListResponse) response.body();
                        if (myPagesListResponse.status) {
                            if (myPagesListResponse.dt.size() > 0) {
                                business_main_ll.setVisibility(View.VISIBLE);
                                for (MyPagesListResponse.Dt dt : myPagesListResponse.dt) {
                                    View ownBusinessPages = inflater.inflate(R.layout.user_pic_two_btn_list_item, null);

                                    ImageView member_profile_pic = ownBusinessPages.findViewById(R.id.member_profile_pic);
                                    Glide.with(context).load(imgPath + dt.logo).placeholder(R.drawable.default_business_pic).into(member_profile_pic);

                                    TextView member_tv = ownBusinessPages.findViewById(R.id.member_tv);
                                    member_tv.setText(dt.busName);

                                    MaterialButton first_mbtn = ownBusinessPages.findViewById(R.id.first_mbtn);
                                    first_mbtn.setVisibility(View.GONE);

                                    MaterialButton second_mbtn = ownBusinessPages.findViewById(R.id.second_mbtn);
                                    second_mbtn.setText("Forward");
                                    second_mbtn.setOnClickListener(v -> {
                                        CommonFunction.PleaseWaitShow(context);
                                        HashMap hashMap2 = new HashMap();
                                        hashMap2.put("user_master_id", sessionPref.getUserMasterId());
                                        hashMap2.put("apiKey", sessionPref.getApiKey());
                                        hashMap2.put("feed_master_id", feedMasterId);
                                        hashMap2.put("feed_for", "BUSINESS");
                                        hashMap2.put("feed_for_id", dt.businessPageId);
                                        apiInterface.FEEDS_SHARE_FORWARD(hashMap2).enqueue(new MyApiCallback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                super.onResponse(call, response);
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null) {
                                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                                        if (normalCommonResponse.status) {
                                                            //reload activity page OR add forwarded feed on top of the list
                                                            dialog.dismiss();
                                                            reloadOrAddTopFeeds();
                                                        } else
                                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    });

                                    business_ll.addView(ownBusinessPages);
                                }
                            }
                        }
                    }
                }
            }
        });

    }

    private void reloadOrAddTopFeeds() {
        reloadOrAddTopFeeds(null);
    }

    private void reloadOrAddTopFeeds(FeedsRow feedsRow) {
        if (feedsRow == null) {
            //compulsory reload
            resetList();
            if (feedListBy.equalsIgnoreCase("home")) {
                callHomeFeeds();
            } else if (feedListBy.equalsIgnoreCase("common")) {
                callFeedMaster();
            } else if (feedListBy.equalsIgnoreCase("single")) {
                loadSingleFeeds(mainLinearLayout, scrollView, feedsRow.feedMasterId);
            }
        } else {
            //optional add feeds on top
            setSingleFeeds(feedsRow, 0);
            updateViewIndexInFeedStorage();
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private void updateViewIndexInFeedStorage() {
        for (FeedStorage feedStorage : feedsViews) {
            if (feedsViews.contains(feedStorage)) {
                int vIndex = feedsViews.indexOf(feedStorage);
                feedStorage.viewIndex = vIndex;
            }
        }
        viewIndex = feedsViews.size();
    }


    private void removeFeedsInList(FeedsRow feedsRow) {
        String feedMasterId = feedsRow.feedMasterId;
        removeIdInList(feedMasterId);
    }

    private void removeFeedsInList(View mainLayoutView) {
        String feedMasterId = mainLayoutView.getTag().toString();
        removeIdInList(feedMasterId);
    }

    private void removeIdInList(String feedMasterId) {
        for (FeedStorage feedStorage : feedsViews) {
            if (feedStorage.feedsRow != null)
                if (feedStorage.feedsRow.feedMasterId.equalsIgnoreCase(feedMasterId)) {
                    if (mainLinearLayout.getChildAt(feedStorage.viewIndex) != null) {
                        mainLinearLayout.removeViewAt(feedStorage.viewIndex);
                        if (mainLinearLayoutChange != null)
                            mainLinearLayoutChange.itemAddEditChange(mainLinearLayout);
                        feedsViews.remove(feedStorage.viewIndex);
                        updateViewIndexInFeedStorage();
                    }
                    break;
                }
        }
    }

    public static String feedTimeCount(String createDate) {
        return feedTimeCount(createDate, DateUtils.TODAYDATETIMEforDB(), null);
    }

    public static String feedTimeCount(String createDate, String nowDate, String type) {
        long year = DateUtils.dateDiff("Y", createDate, nowDate);
        long month = DateUtils.dateDiff("m", createDate, nowDate);
        long days = DateUtils.dateDiff("d", createDate, nowDate);
        long hour = DateUtils.dateDiff("h", createDate, nowDate);
        long minute = DateUtils.dateDiff("n", createDate, nowDate);
        if (year > 0) {
            if (type != null && type.equalsIgnoreCase("M-Y")) {
                return year + " yr " + Math.round(year % 12) + " mo ago";
            }
            return year + " Years ago";
        } else if (month > 0 && days > 28) {
            if (type != null && type.equalsIgnoreCase("M-Y")) {
                if ((days % 30.417) > 5) {
//                    return month + " mo " + Math.round(days % 30.417) + " dy ago";
                }
            }
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

    public ArrayList<FeedStorage> getFeedsViews() {
        return feedsViews;
    }

    public class FeedStorage {
        public View view; // getTag to given is feedMasterId
        public int viewIndex;
        public FeedsRow feedsRow;
        public ExoPlayer player;
        public StyledPlayerView styledPlayerView;

        public boolean isVideoFeeds = false;

        public boolean impressionAdd = false;

        public FeedStorage(View view, int viewIndex, FeedsRow feedsRow) {
            this.view = view;
            this.viewIndex = viewIndex;
            this.feedsRow = feedsRow;
        }

        public void setVideoResource(ExoPlayer player, StyledPlayerView styledPlayerView) {
            this.player = player;
            this.styledPlayerView = styledPlayerView;
            isVideoFeeds = true;
        }
    }

    public interface MainLinearLayoutChange {
        void itemAddEditChange(LinearLayout linearLayout);
    }

    MainLinearLayoutChange mainLinearLayoutChange;

    public void setMainLinearLayoutChange(MainLinearLayoutChange mainLinearLayoutChange) {
        this.mainLinearLayoutChange = mainLinearLayoutChange;
    }

    boolean viewDisable = false;

    public void setViewDisable(boolean viewDisable) {
        this.viewDisable = viewDisable;
    }

    boolean isFeedsFullView = false;

    public void setFeedsFullView(boolean feedsFullView) {
        isFeedsFullView = feedsFullView;
    }

    TextView title = null;

    public void setTitleView(TextView title) {
        this.title = title;
    }

    boolean isJobEventEdit = false;

    public void setJobEventEdit(boolean jobEventEdit) {
        isJobEventEdit = jobEventEdit;
    }

}
