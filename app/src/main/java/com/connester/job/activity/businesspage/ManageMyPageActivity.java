package com.connester.job.activity.businesspage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.BusinessPageRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ManageMyPageActivity extends AppCompatActivity {
    String business_page_id = null;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    MaterialCardView back_cv;
    ViewPager view_pager;
    TabLayout tab_layout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();
    ApiInterface apiInterface;
    ImageView page_banner_iv, page_logo_iv;
    MaterialCardView page_info_edit;
    TextView page_title_txt, tagline_bio_tv, industry_tv, address_tv, founded_yr_tv, followers_tv, no_employee_tv;
    MaterialButton jobs_mbtn, events_mbtn, setting_open_mbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_page);

        if (getIntent() != null) {
            business_page_id = getIntent().getStringExtra("business_page_id");
        } else {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
        if (business_page_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }

        context = ManageMyPageActivity.this;
        activity = ManageMyPageActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });

        page_banner_iv = findViewById(R.id.page_banner_iv);
        page_logo_iv = findViewById(R.id.page_logo_iv);
        page_info_edit = findViewById(R.id.page_info_edit);
        page_info_edit.setOnClickListener(v -> {
            openPageEditDialog();
        });

        page_title_txt = findViewById(R.id.page_title_txt);
        tagline_bio_tv = findViewById(R.id.tagline_bio_tv);
        industry_tv = findViewById(R.id.industry_tv);
        address_tv = findViewById(R.id.address_tv);
        founded_yr_tv = findViewById(R.id.founded_yr_tv);
        followers_tv = findViewById(R.id.followers_tv);
        no_employee_tv = findViewById(R.id.no_employee_tv);

        jobs_mbtn = findViewById(R.id.jobs_mbtn);
        jobs_mbtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PageJobMangeActivity.class);
            intent.putExtra("business_page_id", business_page_id);
            startActivity(intent);
        });
        events_mbtn = findViewById(R.id.events_mbtn);
        events_mbtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PageEventMangeActivity.class);
            intent.putExtra("business_page_id", business_page_id);
            startActivity(intent);
        });
        setting_open_mbtn = findViewById(R.id.setting_open_mbtn);
        setting_open_mbtn.setOnClickListener(v -> {
           openPageSettingDialog();
        });
        setData();

        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        fragments.add(new PagePostFragment());
        fragmentsTitle.add("Posts");
        fragments.add(new PageAnalyticsFragment());
        fragmentsTitle.add("Analytics");
        fragments.add(new PageJobApplicationFragment());
        fragmentsTitle.add("Job Application");
        fragments.add(new PagePeopleFragment());
        fragmentsTitle.add("People");
        view_pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return fragmentsTitle.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        });
    }

    private void openPageSettingDialog() {
    }

    private void openPageEditDialog() {
    }

    BusinessPageRowResponse.BusinessPageRow businessPageRow;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call

    private void setData() {
        businessPageRow = null;
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("business_page_id", business_page_id);

        apiInterface.BUSINESS_PAGE_ROW(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        BusinessPageRowResponse businessPageRowResponse = (BusinessPageRowResponse) response.body();
                        if (businessPageRowResponse.status) {
                            businessPageRow = businessPageRowResponse.businessPageRow;
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
                        } else
                            Toast.makeText(context, businessPageRowResponse.msg, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


    }
}