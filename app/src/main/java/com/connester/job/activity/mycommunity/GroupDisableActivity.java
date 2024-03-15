package com.connester.job.activity.mycommunity;

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
import com.connester.job.RetrofitConnection.jsontogson.GroupRowResponse;
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

public class GroupDisableActivity extends AppCompatActivity {
    String community_master_id, imgPath;
    TextView error_text;
    GroupRowResponse groupRowResponse;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    MaterialCardView back_cv;
    ImageView group_banner_iv, group_logo_iv, privacy_img;
    TextView close_type_tv, group_id_tv, close_reason_tv, close_reason_db, mail_req_tv, group_title_txt, privacy_tv;
    WebView privacy_policy_txt;
    MaterialButton re_activate_group_mbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_disable);

        error_text = findViewById(R.id.error_text);
        error_text.setVisibility(View.VISIBLE);
        if (getIntent() != null) {
            community_master_id = getIntent().getStringExtra("community_master_id");
            String GroupRowResponseJson = getIntent().getStringExtra("GroupRowResponse");
            if (community_master_id != null && GroupRowResponseJson != null) {
                groupRowResponse = new Gson().fromJson(GroupRowResponseJson, GroupRowResponse.class);
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

        group_banner_iv = findViewById(R.id.group_banner_iv);
        group_logo_iv = findViewById(R.id.group_logo_iv);

        group_title_txt = findViewById(R.id.group_title_txt);
        privacy_img = findViewById(R.id.privacy_img);
        privacy_tv = findViewById(R.id.privacy_tv);

        imgPath = groupRowResponse.imgPath;
        Glide.with(context).load(imgPath + groupRowResponse.groupRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(group_banner_iv);
        Glide.with(context).load(imgPath + groupRowResponse.groupRow.logo).centerCrop().placeholder(R.drawable.default_business_pic).into(group_logo_iv);


        group_title_txt.setText(groupRowResponse.groupRow.name);
        privacy_tv.setText(CommonFunction.capitalize(groupRowResponse.groupRow.type));
        //set icon
        if (groupRowResponse.groupRow.type.equalsIgnoreCase("PUBLIC")) {
            privacy_img.setImageResource(R.drawable.people_fill_public);
        } else {
            privacy_img.setImageResource(R.drawable.person_fill_lock_private);
        }

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

        String pisGroupOffReason[] = groupRowResponse.groupRow.groupOffReason.split("-");
        HashMap msgOffHashMap = new HashMap();
        msgOffHashMap.put("BY USER", "Group closed action by You.");
        msgOffHashMap.put("BY ADMIN", "Disabled by admin.");
        msgOffHashMap.put("BY SYSTEM", "Disabled by automated system security.");
        msgOffHashMap.put("BY REPORT", "Suspended because other user report your profile.");

        close_type_tv = findViewById(R.id.close_type_tv);
        close_type_tv.setText(typeHashMap.get(groupRowResponse.groupRow.groupStatus.toUpperCase()).toString());
        close_type_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, typeIconHashMap.get(groupRowResponse.groupRow.groupStatus.toUpperCase()), 0);
        close_type_tv.setCompoundDrawablePadding(4);
        close_type_tv.setTextColor(context.getColor(typeColorHashMap.get(groupRowResponse.groupRow.groupStatus.toUpperCase())));

        group_id_tv = findViewById(R.id.group_id_tv);
        group_id_tv.setText("Group Id - " + String.format("%04d", Integer.parseInt(community_master_id)));

        close_reason_tv = findViewById(R.id.close_reason_tv);
        close_reason_tv.setText("You no longer access your group! further reason of " + msgOffHashMap.get(pisGroupOffReason[pisGroupOffReason.length - 1]));

        close_reason_db = findViewById(R.id.close_reason_db);
        close_reason_db.setText(groupRowResponse.groupRow.groupOffReason);

        privacy_policy_txt = findViewById(R.id.privacy_policy_txt);
        privacy_policy_txt.setBackgroundColor(Color.TRANSPARENT);
        privacy_policy_txt.loadData("<p style='background:transparent'>Check the <a style='color:darkred;text-decoration:none' href='" + Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "User-Agreement'>User Agreement</a>, and <a style='color:darkred;text-decoration:none' href='" + Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "Privacy-Policy'>Privacy Policy</a> you agree to Connester. Check of your violation or closed/Disabled Profile.</p>", "text/html; charset=UTF-8", null);

        mail_req_tv = findViewById(R.id.mail_req_tv);
        mail_req_tv.setVisibility(View.GONE);
        re_activate_group_mbtn = findViewById(R.id.re_activate_group_mbtn);
        if (pisGroupOffReason[pisGroupOffReason.length - 1].equalsIgnoreCase("BY USER")) {
            re_activate_group_mbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("community_master_id", community_master_id);
                    apiInterface.GROUP_RE_ACTIVE(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        Intent intent = new Intent(context, ManageMyCommunityActivity.class);
                                        intent.putExtra("community_master_id", community_master_id);
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
            re_activate_group_mbtn.setText("Mail - " + Constant.userEmail);
            re_activate_group_mbtn.setOnClickListener(v -> {
                CommonFunction.mailInApp(context, Constant.userEmail);
            });
        }
    }
}