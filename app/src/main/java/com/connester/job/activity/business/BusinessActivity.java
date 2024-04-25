package com.connester.job.activity.business;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.BusinessPageRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.business.fragment.BusinessAboutFragment;
import com.connester.job.activity.business.fragment.BusinessEventFragment;
import com.connester.job.activity.business.fragment.BusinessJobsFragment;
import com.connester.job.activity.business.fragment.BusinessPeopleFragment;
import com.connester.job.activity.business.fragment.BusinessPostsFragment;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.connester.job.module.VisitMaster;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BusinessActivity extends AppCompatActivity {
    String business_page_id;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    MaterialCardView back_cv;
    TabLayout tab_layout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();
    ApiInterface apiInterface;
    ImageView page_banner_iv, page_logo_iv;
    MaterialButton follow_unfollow_mbtn, more_option_mbtn;
    TextView page_title_txt, tagline_bio_tv, industry_tv, address_tv, founded_yr_tv, followers_tv, no_employee_tv;
    ScrollView scrollView;
    FrameLayout progressBar;
    HashMap hashMapMain = new HashMap();
   public static UserMaster userMaster;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (getIntent() != null) {
            if (getIntent().getStringExtra("business_page_id") != null) {
                business_page_id = getIntent().getStringExtra("business_page_id");
            } else {
                Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            return;
        }
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        userMaster = new UserMaster(context, BusinessActivity.this);
        userMaster.initReportAttachmentLauncher();

        hashMapMain.put("user_master_id", sessionPref.getUserMasterId());
        hashMapMain.put("apiKey", sessionPref.getApiKey());
        hashMapMain.put("device", "ANDROID");

        scrollView = findViewById(R.id.scrollView);
        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });

        page_banner_iv = findViewById(R.id.page_banner_iv);
        page_logo_iv = findViewById(R.id.page_logo_iv);

        page_title_txt = findViewById(R.id.page_title_txt);
        tagline_bio_tv = findViewById(R.id.tagline_bio_tv);
        industry_tv = findViewById(R.id.industry_tv);
        address_tv = findViewById(R.id.address_tv);
        founded_yr_tv = findViewById(R.id.founded_yr_tv);
        followers_tv = findViewById(R.id.followers_tv);
        no_employee_tv = findViewById(R.id.no_employee_tv);

        follow_unfollow_mbtn = findViewById(R.id.follow_unfollow_mbtn);

        more_option_mbtn = findViewById(R.id.more_option_mbtn);
        more_option_mbtn.setOnClickListener(v -> {
            //open More option
            openMoreOptionDialog();
        });

        setData();

        tab_layout = findViewById(R.id.tab_layout);

        fragments.add(new BusinessAboutFragment(business_page_id, progressBar));
        fragmentsTitle.add("About");
        fragments.add(new BusinessPostsFragment(scrollView, business_page_id, progressBar));
        fragmentsTitle.add("Posts");
        fragments.add(new BusinessJobsFragment(scrollView, business_page_id, progressBar));
        fragmentsTitle.add("Jobs");
        fragments.add(new BusinessEventFragment(scrollView, business_page_id, progressBar));
        fragmentsTitle.add("Events");
        fragments.add(new BusinessPeopleFragment(scrollView, business_page_id, progressBar));
        fragmentsTitle.add("People");


        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = fragments.get(tab.getPosition());
                CommonFunction._LoadFirstFragment(BusinessActivity.this, R.id.container, fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        CommonFunction._LoadFirstFragment(BusinessActivity.this, R.id.container, fragments.get(0));
        new VisitMaster(context, activity).visitedBusinessPage(business_page_id);

        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                setData();
                tab_layout.selectTab(tab_layout.getTabAt(tab_layout.getSelectedTabPosition()));
            }
        });

    }


    private void openMoreOptionDialog() {
        BottomSheetDialog optionDialog = new BottomSheetDialog(context);
        optionDialog.setContentView(R.layout.common_option_dialog_layout);
        LinearLayout link_copy_LL = optionDialog.findViewById(R.id.link_copy_LL);
        link_copy_LL.setVisibility(View.VISIBLE);
        link_copy_LL.setOnClickListener(v -> {
            if (businessPageRow != null) {
                String urlLink = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/business/" + businessPageRow.cusLink;
                CommonFunction.copyToClipBoard(context, urlLink);
                optionDialog.dismiss();
            }
        });

        LinearLayout report_LL = optionDialog.findViewById(R.id.report_LL);
        report_LL.setVisibility(View.VISIBLE);
        report_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMaster.openReportDialog("Business", "business_page", business_page_id, context);
                optionDialog.dismiss();
            }
        });
        optionDialog.show();
    }

    BusinessPageRowResponse.BusinessPageRow businessPageRow;
    BusinessPageRowResponse businessPageRowResponse;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call

    private void setData() {
        progressBar.setVisibility(View.VISIBLE);
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("business_page_id", business_page_id);

        apiInterface.BUSINESS_PAGE_ROW(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                        businessPageRowResponse = (BusinessPageRowResponse) response.body();
                        if (businessPageRowResponse.status) {
                            businessPageRow = businessPageRowResponse.businessPageRow;
                            //handling redirect for page is not active
                            if (!businessPageRow.pageStatus.equalsIgnoreCase("ACTIVE")) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(context, "Page not found! This page is deactivated! Pleas go back.", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                imgPath = businessPageRowResponse.imgPath;
                                Glide.with(context).load(imgPath + businessPageRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(page_banner_iv);
                                Glide.with(context).load(imgPath + businessPageRow.logo).centerCrop().placeholder(R.drawable.default_business_pic).into(page_logo_iv);

                                page_title_txt.setText(businessPageRow.busName);
                                tagline_bio_tv.setText(businessPageRow.bio);
                                industry_tv.setText(businessPageRow.industry);
                                address_tv.setText(businessPageRow.address);
                                founded_yr_tv.setText(businessPageRow.foundedYear);
                                followers_tv.setText(businessPageRow.members + " followers");
                                no_employee_tv.setText(businessPageRow.orgSize);

                                if (businessPageRowResponse.isMember) {//call unfollow
                                    follow_unfollow_mbtn.setText("Unfollow");
                                    follow_unfollow_mbtn.setIcon(getDrawable(R.drawable.log_out));
                                    follow_unfollow_mbtn.setOnClickListener(v -> {
                                        //setup unfollow call
                                        CommonFunction.PleaseWaitShow(context);
                                        HashMap hashMap = new HashMap();
                                        hashMap.putAll(hashMapMain);
                                        hashMap.put("business_page_id", business_page_id);
                                        apiInterface.PAGES_MEMBERS_REMOVE(hashMap).enqueue(new MyApiCallback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                super.onResponse(call, response);
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null) {
                                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                                        if (normalCommonResponse.status) {
                                                            setData();
                                                        }
                                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    });
                                } else {//call follow
                                    follow_unfollow_mbtn.setText("Follow");
                                    follow_unfollow_mbtn.setIcon(getDrawable(R.drawable.account_multiple_plus_follow));
                                    follow_unfollow_mbtn.setOnClickListener(v -> {
                                        //setup follow call
                                        CommonFunction.PleaseWaitShow(context);
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                                        hashMap.put("apiKey", sessionPref.getApiKey());
                                        hashMap.put("business_page_id", business_page_id);
                                        apiInterface.PAGE_FOLLOW_REQUEST(hashMap).enqueue(new MyApiCallback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                super.onResponse(call, response);
                                                progressBar.setVisibility(View.GONE);
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null) {
                                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                                        if (normalCommonResponse.status)
                                                            setData();
                                                        else
                                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    });
                                }

                                tab_layout.selectTab(tab_layout.getTabAt(0));
                            }
                        } else {
                            businessPageRow = null;
                            Toast.makeText(context, businessPageRowResponse.msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });
    }
}