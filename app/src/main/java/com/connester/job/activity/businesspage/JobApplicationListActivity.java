package com.connester.job.activity.businesspage;

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
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.PageJobApplicationResponse;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class JobApplicationListActivity extends AppCompatActivity {
    String list;
    PageJobApplicationResponse.Json.JobList jobList;
    TextView title;
    ImageView back_iv;
    LinearLayout main_ll;
    Context context;
    SessionPref sessionPref;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_application_list);
        context = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if (getIntent() != null) {
            Intent intent = getIntent();
            if (intent.getStringExtra("list") != null) {
                list = intent.getStringExtra("list");
            } else return;
            if (intent.getStringExtra("json") != null) {
                PageJobApplicationResponse.Json json = new Gson().fromJson(intent.getStringExtra("json"), PageJobApplicationResponse.Json.class);
                if (list.equalsIgnoreCase("active")) {
                    jobList = json.activeList;
                } else if (list.equalsIgnoreCase("shortlisted")) {
                    jobList = json.shortlistedList;
                } else if (list.equalsIgnoreCase("rejected")) {
                    jobList = json.rejectedList;
                }
            } else return;
        } else return;

        title = findViewById(R.id.title);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        main_ll = findViewById(R.id.main_ll);

        setData();
    }

    private void setData() {
        title.setText(list + " Job Applications");
        if (jobList.status) {
            main_ll.removeAllViews();
            HashMap hashMapMain = new HashMap();
            hashMapMain.put("user_master_id", sessionPref.getUserMasterId());
            hashMapMain.put("apiKey", sessionPref.getApiKey());
            for (PageJobApplicationResponse.Json.JobList.Dt dt : jobList.dt) {
                View jobApplicationView = getLayoutInflater().inflate(R.layout.job_application_list_item, null);


                ImageView member_profile_pic = jobApplicationView.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(jobList.imgPath + dt.profilePic).centerCrop().placeholder(R.drawable.user_default_banner).into(member_profile_pic);

                TextView member_tv = jobApplicationView.findViewById(R.id.member_tv);
                member_tv.setText(dt.name);

                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", dt.userMasterId);
                    startActivity(intent);
                };
                member_profile_pic.setOnClickListener(openUserProfile);
                member_tv.setOnClickListener(openUserProfile);

                TextView first_tv = jobApplicationView.findViewById(R.id.first_tv);
                TextView second_tv = jobApplicationView.findViewById(R.id.second_tv);
                second_tv.setText("Reject");
                second_tv.setOnClickListener(v -> {
                    //call reject application api
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.putAll(hashMapMain);
                    hashMap.put("apply_status", "REJECTED");
                    hashMap.put("job_apply_id", dt.jobApplyId);
                    apiInterface.PAGES_JOB_APPLICATION_CHANGE_STATUS(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        if (!list.equalsIgnoreCase("rejected")) {
                                            main_ll.removeView(jobApplicationView);
                                        }
                                    }
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                });

                if (list.equalsIgnoreCase("shortlisted")) {
                    first_tv.setText("Add to active");
                    first_tv.setOnClickListener(v -> {
                        //call application add to active
                        CommonFunction.PleaseWaitShow(context);
                        HashMap hashMap = new HashMap();
                        hashMap.putAll(hashMapMain);
                        hashMap.put("apply_status", "APPLY");
                        hashMap.put("job_apply_id", dt.jobApplyId);
                        apiInterface.PAGES_JOB_APPLICATION_CHANGE_STATUS(hashMap).enqueue(new MyApiCallback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status) {
                                            main_ll.removeView(jobApplicationView);
                                        }
                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    });
                } else {//rejected OR active
                    first_tv.setText("Add to shortlist");
                    first_tv.setOnClickListener(v -> {
                        //call application add to shortlisted
                        CommonFunction.PleaseWaitShow(context);
                        HashMap hashMap = new HashMap();
                        hashMap.putAll(hashMapMain);
                        hashMap.put("apply_status", "SHORTLISTED");
                        hashMap.put("job_apply_id", dt.jobApplyId);
                        apiInterface.PAGES_JOB_APPLICATION_CHANGE_STATUS(hashMap).enqueue(new MyApiCallback() {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status) {
                                            main_ll.removeView(jobApplicationView);
                                        }
                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    });
                }

                TextView job_profile_tv = jobApplicationView.findViewById(R.id.job_profile_tv);
                job_profile_tv.setText(dt.titlePost);
                TextView address_tv = jobApplicationView.findViewById(R.id.address_tv);
                address_tv.setText(dt.jobLocation);
                main_ll.addView(jobApplicationView);
            }
        }
    }
}