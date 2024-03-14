package com.connester.job.activity.businesspage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.BusinessPageRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class PageDisableActivity extends AppCompatActivity {
    String business_page_id, imgPath;
    TextView error_text;
    BusinessPageRowResponse businessPageRowResponse;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    MaterialCardView back_cv;
    ImageView page_banner_iv, page_logo_iv;
    TextView page_title_txt, tagline_bio_tv, industry_tv, address_tv, founded_yr_tv, followers_tv, no_employee_tv;
    TextView close_type_tv, page_id_tv, close_reason_tv, close_reason_db, mail_req_tv;
    WebView privacy_policy_txt;
    MaterialButton re_activate_page_mbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_disable);

        error_text = findViewById(R.id.error_text);
        error_text.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            business_page_id = getIntent().getStringExtra("business_page_id");
            String businessPageRowResponseJson = getIntent().getStringExtra("BusinessPageRowResponse");
            if (business_page_id != null && businessPageRowResponseJson != null) {
                businessPageRowResponse = new Gson().fromJson(businessPageRowResponseJson, BusinessPageRowResponse.class);
            } else return;
        } else return;

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        error_text.setVisibility(View.GONE);

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

        imgPath = businessPageRowResponse.imgPath;
        Glide.with(context).load(imgPath + businessPageRowResponse.businessPageRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(page_banner_iv);
        Glide.with(context).load(imgPath + businessPageRowResponse.businessPageRow.logo).centerCrop().placeholder(R.drawable.default_business_pic).into(page_logo_iv);

        page_title_txt.setText(businessPageRowResponse.businessPageRow.busName);
        tagline_bio_tv.setText(businessPageRowResponse.businessPageRow.bio);
        industry_tv.setText(businessPageRowResponse.businessPageRow.industry);
        address_tv.setText(businessPageRowResponse.businessPageRow.address);
        founded_yr_tv.setText(businessPageRowResponse.businessPageRow.foundedYear);
        followers_tv.setText(businessPageRowResponse.businessPageRow.members + " followers");
        no_employee_tv.setText(businessPageRowResponse.businessPageRow.orgSize);

        //disable data set

        HashMap typeHashMap = new HashMap();
        typeHashMap.put("SUSPENDED", "suspended!");
        typeHashMap.put("TEMPORARY_OFF", "temporary disabled/closed!");

        HashMap<String, Integer> typeIconHashMap = new HashMap();
        typeIconHashMap.put("SUSPENDED", R.drawable.ban_solid);
        typeIconHashMap.put("TEMPORARY_OFF", R.drawable.lock_solid);

        HashMap<String, Integer> typeColorHashMap = new HashMap();
        typeColorHashMap.put("SUSPENDED", R.color.error);
        typeColorHashMap.put("TEMPORARY_OFF", R.color.warning);

        String pisPageOffReason[] = businessPageRowResponse.businessPageRow.pageOffReason.split("-");
        HashMap msgOffHashMap = new HashMap();
        msgOffHashMap.put("BY USER", "Business page closed action by You.");
        msgOffHashMap.put("BY ADMIN", "Disabled by admin.");
        msgOffHashMap.put("BY SYSTEM", "Disabled by automated system security.");
        msgOffHashMap.put("BY REPORT", "Suspended because other user report your profile.");

        close_type_tv = findViewById(R.id.close_type_tv);
        close_type_tv.setText(typeHashMap.get(businessPageRowResponse.businessPageRow.pageStatus.toUpperCase()).toString());
        close_type_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, typeIconHashMap.get(businessPageRowResponse.businessPageRow.pageStatus.toUpperCase()), 0);
        close_type_tv.setCompoundDrawablePadding(4);
        close_type_tv.setTextColor(context.getColor(typeColorHashMap.get(businessPageRowResponse.businessPageRow.pageStatus.toUpperCase())));

        page_id_tv = findViewById(R.id.page_id_tv);
        page_id_tv.setText("Business page Id - " + String.format("%04d", Integer.parseInt(business_page_id)));

        close_reason_tv = findViewById(R.id.close_reason_tv);
        close_reason_tv.setText("You no longer access your business page! further reason of " + msgOffHashMap.get(pisPageOffReason[pisPageOffReason.length - 1]));

        close_reason_db = findViewById(R.id.close_reason_db);
        close_reason_db.setText(businessPageRowResponse.businessPageRow.pageOffReason);

        privacy_policy_txt = findViewById(R.id.privacy_policy_txt);
        privacy_policy_txt.setBackgroundColor(Color.TRANSPARENT);
        privacy_policy_txt.loadData("<p style='background:transparent'>Check the <a style='color:darkred;text-decoration:none' href='" + Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "User-Agreement'>User Agreement</a>, and <a style='color:darkred;text-decoration:none' href='" + Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "Privacy-Policy'>Privacy Policy</a> you agree to Connester. Check of your violation or closed/Disabled Profile.</p>", "text/html; charset=UTF-8", null);

        mail_req_tv = findViewById(R.id.mail_req_tv);
        mail_req_tv.setVisibility(View.GONE);
        re_activate_page_mbtn = findViewById(R.id.re_activate_page_mbtn);
        if (pisPageOffReason[pisPageOffReason.length - 1].equalsIgnoreCase("BY USER")) {
            re_activate_page_mbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("business_page_id", business_page_id);
                    apiInterface.BUSINESS_PAGE_REACTIVATE_CALL(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        Intent intent = new Intent(context, ManageMyPageActivity.class);
                                        intent.putExtra("business_page_id", business_page_id);
                                        startActivity(intent);
                                        finish();
                                    } else
                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        } else { // request approve in  mail
            mail_req_tv.setVisibility(View.VISIBLE);
            re_activate_page_mbtn.setText("Mail - " + Constant.userEmail);
            re_activate_page_mbtn.setOnClickListener(v -> {
                CommonFunction.mailInApp(context, Constant.userEmail);
            });
        }
    }
}