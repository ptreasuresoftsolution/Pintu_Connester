package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.EducationListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.ProjectListResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.WorkExperienceListResponse;
import com.connester.job.activity.message.ChatHistoryUsersActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.DateUtils;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.connester.job.module.UserMaster;
import com.connester.job.module.VisitMaster;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    String user_master_id;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    UserRowResponse.Dt userDt, loginUserDt;
    UserMaster userMaster;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call
    FlexboxLayout user_skill_tag_fbl, user_language_tag_fbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        userMaster = new UserMaster(context, ProfileActivity.this);
        userMaster.initReportAttachmentLauncher();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if (getIntent() != null) {
            user_master_id = getIntent().getStringExtra("user_master_id");
        }
        if (user_master_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }

        initView();
        setData();
        new VisitMaster(context, activity).visitedUserProfile(user_master_id);

        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                setData();
            }
        });
    }

    SwipeRefreshLayout swipe_refresh;
    TextView userFullName_txt, user_position_tv, user_bio_tv, followers_tv, following_tv, about_me_tv;
    ImageView user_banner_iv, user_pic;
    MaterialCardView back_cv;
    LinearLayout work_experience_ll, education_ll, project_ll;
    MaterialButton one_mbtn, two_mbtn, more_option_mbtn;


    private void initView() {
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        hashMapDefault.put("device", "ANDROID");
        hashMapDefault.put("for_user_master_id", user_master_id);

        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });
        user_banner_iv = findViewById(R.id.user_banner_iv);

        user_pic = findViewById(R.id.user_pic);

        userFullName_txt = findViewById(R.id.userFullName_txt);
        user_position_tv = findViewById(R.id.user_position_tv);
        user_bio_tv = findViewById(R.id.user_bio_tv);
        followers_tv = findViewById(R.id.followers_tv);
        following_tv = findViewById(R.id.following_tv);

        one_mbtn = findViewById(R.id.one_mbtn);
        two_mbtn = findViewById(R.id.two_mbtn);
        if (user_master_id.equalsIgnoreCase(sessionPref.getUserMasterId())) {
            one_mbtn.setVisibility(View.GONE);
            two_mbtn.setVisibility(View.GONE);
        }

        more_option_mbtn = findViewById(R.id.more_option_mbtn);
        more_option_mbtn.setOnClickListener(v -> {
            BottomSheetDialog profileOptionDialog = new BottomSheetDialog(context);
            profileOptionDialog.setContentView(R.layout.common_option_dialog_layout);

            LinearLayout share_profile_option_LL = profileOptionDialog.findViewById(R.id.share_profile_option_LL);
            share_profile_option_LL.setVisibility(View.VISIBLE);
            share_profile_option_LL.setOnClickListener(v1 -> {
                profileOptionDialog.dismiss();
                Intent intent = new Intent(context, ChatHistoryUsersActivity.class);
                intent.putExtra("action", "pick");
                intent.putExtra("message", Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/profile/" + userDt.profileLink);
                startActivity(intent);
            });
            LinearLayout web_portfolio_option_LL = profileOptionDialog.findViewById(R.id.web_portfolio_option_LL);
            web_portfolio_option_LL.setVisibility(View.VISIBLE);
            web_portfolio_option_LL.setOnClickListener(v1 -> {
                profileOptionDialog.dismiss();
                Intent intent = new Intent(context, ShowPortfolioApplyJobActivity.class);
                intent.putExtra("user_master_id", user_master_id);
                startActivity(intent);
            });
            LinearLayout post_LL = profileOptionDialog.findViewById(R.id.post_LL);
            post_LL.setVisibility(View.VISIBLE);
            post_LL.setOnClickListener(v1 -> {
                profileOptionDialog.dismiss();
                Intent intent = new Intent(context, Activity_Activity.class);
                intent.putExtra("user_master_id", user_master_id);
                intent.putExtra("title", (userDt.name != null ? userDt.name + " Post" : "Post"));
                startActivity(intent);
            });
            LinearLayout block_user_LL = profileOptionDialog.findViewById(R.id.block_user_LL);
            if (!user_master_id.equalsIgnoreCase(sessionPref.getUserMasterId())) {
                block_user_LL.setVisibility(View.VISIBLE);
                block_user_LL.setOnClickListener(v1 -> {
                    //call block member api
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("id", user_master_id);
                    apiInterface.BLOCKED_USER(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status)
                                        profileOptionDialog.dismiss();
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                });
            }
            LinearLayout report_LL = profileOptionDialog.findViewById(R.id.report_LL);
            if (!user_master_id.equalsIgnoreCase(sessionPref.getUserMasterId())) {
                report_LL.setVisibility(View.VISIBLE);
                report_LL.setOnClickListener(v1 -> {
                    //call report this profile
                    userMaster.openReportDialog("Profile", "user_master", user_master_id, context);
                });
            }
            profileOptionDialog.show();
        });

        about_me_tv = findViewById(R.id.about_me_tv);
        work_experience_ll = findViewById(R.id.work_experience_ll);
        education_ll = findViewById(R.id.education_ll);
        project_ll = findViewById(R.id.project_ll);
        user_skill_tag_fbl = findViewById(R.id.user_skill_tag_fbl);
        user_language_tag_fbl = findViewById(R.id.user_language_tag_fbl);
    }


    private void setData() {
        //login User
        userMaster.getUserClmData(new UserMaster.CallBack() {
            @Override
            public void DataCallBack(Response response) {
                if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                    swipe_refresh.setRefreshing(false);
                }
                UserRowResponse loginUserRowResponse = (UserRowResponse) response.body();
                if (loginUserRowResponse.status) {
                    loginUserDt = loginUserRowResponse.dt;
                }
                //view profile User
                userMaster.getUserClmData(new UserMaster.CallBack() {
                    @Override
                    public void DataCallBack(Response response) {
                        UserRowResponse userRowResponse = (UserRowResponse) response.body();
                        if (userRowResponse.status) {
                            userDt = userRowResponse.dt;
                            imgPath = userRowResponse.imgPath;

                            Glide.with(context).load(imgPath + userDt.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);
                            Glide.with(context).load(imgPath + userDt.profileBanner).centerCrop().placeholder(R.drawable.user_default_banner).into(user_banner_iv);
                            userFullName_txt.setText(userDt.name);
                            user_position_tv.setText(userDt.position);
                            user_bio_tv.setText(userDt.bio);

                            int followers = 0;
                            if (userDt.followerIds != null)
                                followers = userDt.followerIds.split(",").length;
                            followers_tv.setText(followers + " followers");

                            int following = 0;
                            if (userDt.followingIds != null)
                                following = userDt.followingIds.split(",").length;
                            following_tv.setText(following + " following");

                            about_me_tv.setText(userDt.bio);

                            loadWorkExperience();
                            loadEducation();
                            loadProjects();

                            //skills set
                            user_skill_tag_fbl.removeAllViews();
                            for (String skill : userDt.skill.split(",")) {
                                View skillItem = getLayoutInflater().inflate(R.layout.skill_tag_item, null);
                                TextView skill_item = skillItem.findViewById(R.id.skill_item);
                                skill_item.setText(skill);
                                user_skill_tag_fbl.addView(skillItem);
                            }

                            //language set
                            user_language_tag_fbl.removeAllViews();
                            for (String language : userDt.language.split(",")) {
                                View languageItem = getLayoutInflater().inflate(R.layout.skill_tag_item, null);
                                TextView language_item = languageItem.findViewById(R.id.skill_item);
                                language_item.setText(language);
                                user_language_tag_fbl.addView(languageItem);
                            }

                            //set all button
                            //one-first button
                            if (loginUserDt.followingIds != null && UserMaster.findIdInIds(user_master_id, loginUserDt.followingIds)) {//is follower -> Unfollow
                                one_mbtn.setText("UnFollow");
                                one_mbtn.setIcon(getDrawable(R.drawable.person_dash_unfollow));
                                one_mbtn.setEnabled(true);
                                one_mbtn.setOnClickListener(v -> {
                                    networkActionMange(new NetworkActivity.NetworkActionCallback() {
                                        @Override
                                        public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                            if (normalCommonResponse.status) setData();
                                            else
                                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }, NetworkActivity.ActionName.UnFollowFollowing, user_master_id, context);
                                });
                            } else if (loginUserDt.sendFollowingReq != null && UserMaster.findIdInIds(user_master_id, loginUserDt.sendFollowingReq)) {//is following -> Pending
                                one_mbtn.setText("Pending");
                                one_mbtn.setIcon(getDrawable(R.drawable.feeds_time));
                                one_mbtn.setEnabled(false);
                            } else {//default button -> Follow
                                one_mbtn.setText("Follow");
                                one_mbtn.setIcon(getDrawable(R.drawable.person_add_follow));
                                one_mbtn.setEnabled(true);
                                one_mbtn.setOnClickListener(v -> {
                                    networkActionMange(new NetworkActivity.NetworkActionCallback() {
                                        @Override
                                        public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                            if (normalCommonResponse.status) setData();
                                            else
                                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }, NetworkActivity.ActionName.ReqFollow, user_master_id, context);
                                });
                            }

                            //two-second button
                            if (loginUserDt.connectUser != null && UserMaster.findIdInIds(user_master_id, loginUserDt.connectUser)) {//is connected -> Message
                                two_mbtn.setText("Message");
                                two_mbtn.setIcon(getDrawable(R.drawable.inbox_message_chat_send));
                                two_mbtn.setEnabled(true);
                                two_mbtn.setOnClickListener(v -> {
                                    Intent intent = new Intent(context, ChatHistoryUsersActivity.class);
                                    intent.putExtra("action", "startChat");
                                    intent.putExtra("userId", user_master_id);
                                    startActivity(intent);
                                });
                            } else if (UserMaster.findIdInIds(user_master_id, loginUserDt.recConnectReq) || UserMaster.findIdInIds(user_master_id, loginUserDt.sendConnectReq)) {//connect requested -> Pending
                                two_mbtn.setText("Pending");
                                two_mbtn.setIcon(getDrawable(R.drawable.feeds_time));
                                two_mbtn.setEnabled(false);
                            } else {//default button -> Connect
                                two_mbtn.setText("Connect");
                                two_mbtn.setIcon(getDrawable(R.drawable.person_plus_fill_connect));
                                two_mbtn.setEnabled(true);
                                two_mbtn.setOnClickListener(v -> {
                                    networkActionMange(new NetworkActivity.NetworkActionCallback() {
                                        @Override
                                        public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                            if (normalCommonResponse.status) setData();
                                            else
                                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }, NetworkActivity.ActionName.SendInvReq, user_master_id, context);
                                });
                            }
                        }
                    }
                }, "*", true, user_master_id);
            }
        }, "*", true);
    }

    HashMap hashMapDefault = new HashMap<>();

    private void loadProjects() {
        CommonFunction.PleaseWaitShow(context);
        apiInterface.PROJECT_LIST(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ProjectListResponse projectListResponse = (ProjectListResponse) response.body();
                        if (projectListResponse.status) {
                            project_ll.removeAllViews();
                            for (ProjectListResponse.Dt dt : projectListResponse.dt) {
                                View profile_project_ed_work_item = getLayoutInflater().inflate(R.layout.profile_project_ed_work_item, null);

                                ImageView item_edit = profile_project_ed_work_item.findViewById(R.id.item_edit);
                                item_edit.setVisibility(View.GONE);

                                TextView item_title = profile_project_ed_work_item.findViewById(R.id.item_title);
                                item_title.setText(dt.projectName);
                                TextView item_subtitle = profile_project_ed_work_item.findViewById(R.id.item_subtitle);
                                item_subtitle.setText(dt.companyName);
                                TextView item_duration = profile_project_ed_work_item.findViewById(R.id.item_duration);
                                String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.startDate);
                                String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.endDate);
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate, "M-Y").replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "(" + gap + ")");
                                project_ll.addView(profile_project_ed_work_item);
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadEducation() {
        CommonFunction.PleaseWaitShow(context);
        apiInterface.EDUCATION_LIST(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        EducationListResponse educationListResponse = (EducationListResponse) response.body();
                        if (educationListResponse.status) {
                            education_ll.removeAllViews();
                            for (EducationListResponse.Dt dt : educationListResponse.dt) {
                                View profile_project_ed_work_item = getLayoutInflater().inflate(R.layout.profile_project_ed_work_item, null);
                                ImageView item_edit = profile_project_ed_work_item.findViewById(R.id.item_edit);
                                item_edit.setVisibility(View.GONE);

                                TextView item_title = profile_project_ed_work_item.findViewById(R.id.item_title);
                                item_title.setText(dt.degree);
                                TextView item_subtitle = profile_project_ed_work_item.findViewById(R.id.item_subtitle);
                                item_subtitle.setText(dt.schoolInstituteUni);
                                TextView item_duration = profile_project_ed_work_item.findViewById(R.id.item_duration);
                                String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.startDate);
                                String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.endDate);
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate, "M-Y").replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "(" + gap + ")");
                                education_ll.addView(profile_project_ed_work_item);
                            }
                        }
                    }
                }
            }
        });

    }

    private void loadWorkExperience() {
        CommonFunction.PleaseWaitShow(context);
        apiInterface.WORK_EXPERIENCE_LIST(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WorkExperienceListResponse workExperienceListResponse = (WorkExperienceListResponse) response.body();
                        if (workExperienceListResponse.status) {
                            work_experience_ll.removeAllViews();
                            for (WorkExperienceListResponse.Dt dt : workExperienceListResponse.dt) {
                                View profile_project_ed_work_item = getLayoutInflater().inflate(R.layout.profile_project_ed_work_item, null);
                                ImageView item_edit = profile_project_ed_work_item.findViewById(R.id.item_edit);
                                item_edit.setVisibility(View.GONE);

                                TextView item_title = profile_project_ed_work_item.findViewById(R.id.item_title);
                                item_title.setText(dt.jobTitle);
                                TextView item_subtitle = profile_project_ed_work_item.findViewById(R.id.item_subtitle);
                                item_subtitle.setText(dt.jobCompany);
                                TextView item_duration = profile_project_ed_work_item.findViewById(R.id.item_duration);
                                String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.startDate);
                                String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.endDate);
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate, "M-Y").replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "(" + gap + ")");
                                work_experience_ll.addView(profile_project_ed_work_item);
                            }
                        }
                    }
                }
            }
        });
    }

    static void networkActionMange(NetworkActivity.NetworkActionCallback networkActionCallback, NetworkActivity.ActionName action, String userOpponentsId, Context context) {
        CommonFunction.PleaseWaitShow(context);
        SessionPref sessionPref = new SessionPref(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
//      InvReqAccept / InvReqDecline / SendInvReq / RemoveConnection / RemoveFollower / UnFollowFollowing / ReqFollow / FollowReqAccept / FollowReqReject
        hashMap.put("action", action.getVal());
        hashMap.put("opponentsId", userOpponentsId);
        ApiClient.getClient().create(ApiInterface.class).NETWORK_ACTION_MANGE(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        networkActionCallback.apiCallBack(normalCommonResponse);
                    }
                }
            }
        });
    }
}