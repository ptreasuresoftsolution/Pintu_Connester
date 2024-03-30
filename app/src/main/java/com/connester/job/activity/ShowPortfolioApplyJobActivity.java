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

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.EducationListResponse;
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
import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class ShowPortfolioApplyJobActivity extends AppCompatActivity {
    String user_master_id;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    UserRowResponse.Dt userDt;
    UserMaster userMaster;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call
    FlexboxLayout user_skill_tag_fbl, user_language_tag_fbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_portfolio_apply_job);

        if (getIntent() != null) {
            user_master_id = getIntent().getStringExtra("user_master_id");
        }
        if (user_master_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        userMaster = new UserMaster(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        initView();
        setData();
    }

    TextView userFullName_txt, birth_date_tv, phone_tv, email_tv, gender_tv, location_tv, about_me_tv, share_web_portfolio;
    ImageView back_iv, user_pic;
    LinearLayout work_experience_ll, education_ll, project_ll;


    private void initView() {
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        hashMapDefault.put("device", "ANDROID");

        share_web_portfolio = findViewById(R.id.share_web_portfolio);

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        user_pic = findViewById(R.id.user_pic);

        userFullName_txt = findViewById(R.id.userFullName_txt);
        birth_date_tv = findViewById(R.id.birth_date_tv);
        phone_tv = findViewById(R.id.phone_tv);
        email_tv = findViewById(R.id.email_tv);
        gender_tv = findViewById(R.id.gender_tv);
        location_tv = findViewById(R.id.location_tv);

        about_me_tv = findViewById(R.id.about_me_tv);
        work_experience_ll = findViewById(R.id.work_experience_ll);
        education_ll = findViewById(R.id.education_ll);
        project_ll = findViewById(R.id.project_ll);
        user_skill_tag_fbl = findViewById(R.id.user_skill_tag_fbl);
        user_language_tag_fbl = findViewById(R.id.user_language_tag_fbl);
    }


    private void setData() {
        //view profile User
        userMaster.getUserClmData(new UserMaster.CallBack() {
            @Override
            public void DataCallBack(Response response) {
                UserRowResponse userRowResponse = (UserRowResponse) response.body();
                if (userRowResponse.status) {
                    userDt = userRowResponse.dt;
                    imgPath = userRowResponse.imgPath;

                    userFullName_txt.setText(userDt.name);
                    Glide.with(context).load(imgPath + userDt.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);
                    if (userDt.birthDate != null)
                        birth_date_tv.setText(DateUtils.getStringDate("yyyy-MM-dd", "dd MMM, yyyy", userDt.birthDate));
                    if (userDt.mainPhone != null)
                        phone_tv.setText(userDt.mainPhone);
                    if (userDt.email != null)
                        email_tv.setText(userDt.email);
                    if (userDt.gender != null)
                        gender_tv.setText(userDt.gender);
                    if (userDt.city != null && userDt.countryRegion != null)
                        location_tv.setText(userDt.city + ", " + userDt.countryRegion);
                    if (userDt.bio != null)
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

                    //share portfolio button
                    share_web_portfolio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String portFolioLink = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/show-portfolio-applyjob?open=web-portfolio&userId=" + CommonFunction.base64Encode(userDt.userMasterId);
                            Intent intent = new Intent(context, ChatHistoryUsersActivity.class);
                            intent.putExtra("action", "pick");
                            intent.putExtra("isEncryMsg", true);
                            intent.putExtra("message", CommonFunction.base64Encode(portFolioLink));
                            startActivity(intent);
                        }
                    });
                }
            }
        }, "*", true, user_master_id);
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
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate).replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "-" + gap);
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
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate).replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "-" + gap);
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
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate).replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "-" + gap);
                                work_experience_ll.addView(profile_project_ed_work_item);
                            }
                        }
                    }
                }
            }
        });
    }
}