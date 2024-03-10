package com.connester.job.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.settingActivity.Blocking_PersonActivity;
import com.connester.job.activity.settingActivity.ChangePasswordActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class UserMenuActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    TextView top_userFullName_txt, userFullName_txt;
    ImageView back_iv, top_user_pic, user_pic;
    LinearLayout edit_profile_ll, save_item_ll, post_activity_ll, pages_ll, groups_ll, change_password_ll, blocking_person_ll, log_out_all_devices_ll, log_out_single_devices_ll, close_account_ll, privacy_ll, user_agreement_ll;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });

        top_userFullName_txt = findViewById(R.id.top_userFullName_txt);
        top_userFullName_txt.setText(sessionPref.getUserFullName());
        userFullName_txt = findViewById(R.id.userFullName_txt);
        userFullName_txt.setText(sessionPref.getUserFullName());
        top_user_pic = findViewById(R.id.top_user_pic);
        Glide.with(context).load(sessionPref.getUserProfilePic()).centerCrop().placeholder(R.drawable.default_user_pic).into(top_user_pic);
        user_pic = findViewById(R.id.user_pic);
        Glide.with(context).load(sessionPref.getUserProfilePic()).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);

        edit_profile_ll = findViewById(R.id.edit_profile_ll);
        edit_profile_ll.setOnClickListener(v -> {
            startActivity(new Intent(context, EditProfileActivity.class));
        });
        save_item_ll = findViewById(R.id.save_item_ll);
        save_item_ll.setOnClickListener(v -> {
            startActivity(new Intent(context, MySavedItemActivity.class));
        });
        post_activity_ll = findViewById(R.id.post_activity_ll);
        post_activity_ll.setOnClickListener(v -> {
            startActivity(new Intent(context, Activity_Activity.class));
        });
        pages_ll = findViewById(R.id.pages_ll);
        pages_ll.setOnClickListener(v -> {
            startActivity(new Intent(context, BusinessPageActivity.class));
        });
        groups_ll = findViewById(R.id.groups_ll);
        groups_ll.setOnClickListener(v -> {
            startActivity(new Intent(context, MyCommunityActivity.class));
        });
        change_password_ll = findViewById(R.id.change_password_ll);
        change_password_ll.setOnClickListener(v -> {
            startActivity(new Intent(context, ChangePasswordActivity.class));
        });
        blocking_person_ll = findViewById(R.id.blocking_person_ll);
        blocking_person_ll.setOnClickListener(v -> {
            startActivity(new Intent(context, Blocking_PersonActivity.class));
        });
        log_out_all_devices_ll = findViewById(R.id.log_out_all_devices_ll);
        log_out_all_devices_ll.setOnClickListener(v -> {
            //show confirmation dialog && call logout api and clear all data in sessionPref
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Log out all devices!");
            alertDialog.setMessage("So, You are confirm to the log out from all devices??\n" +
                    "And if click on YES then logout account.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //call logout api and clear all data in sessionPref
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    apiInterface.LOG_OUT_ALL_DEVICES_CALL(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        sessionPref.logOutPref();
                                        alertDialog.dismiss();
                                        ActivityCompat.finishAffinity(activity);
                                        startActivity(new Intent(context, SplashActivity.class));
                                    } else
                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        });

        log_out_single_devices_ll = findViewById(R.id.log_out_single_devices_ll);
        log_out_single_devices_ll.setOnClickListener(v -> {
            sessionPref.logOutPref();
            ActivityCompat.finishAffinity(activity);
            startActivity(new Intent(context, SplashActivity.class));
        });
        close_account_ll = findViewById(R.id.close_account_ll);
        close_account_ll.setOnClickListener(v -> {
            //show confirmation dialog and call api
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Close Account!");
            alertDialog.setMessage("You’ll also lose any recommendations and endorsements you’ve given or received.\n" +
                    "\n" +
                    "So, You are confirm to the close this account??\n" +
                    "And if click on continue then closed your account.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //call api close account
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    apiInterface.ACCOUNT_CLOSED_CALL(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        alertDialog.dismiss();
                                        ActivityCompat.finishAffinity(activity);
                                        startActivity(new Intent(context, SplashActivity.class));
                                    } else
                                        Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
            alertDialog.show();
        });

        Switch notification_onoff_switch = findViewById(R.id.notification_onoff_switch);
        notification_onoff_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //call code function for notification on / off
        });

        privacy_ll = findViewById(R.id.privacy_ll);
        privacy_ll.setOnClickListener(v -> {
            String urlPrivacy = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "Privacy-Policy";
            CommonFunction._OpenLink(context, urlPrivacy);
        });

        user_agreement_ll = findViewById(R.id.user_agreement_ll);
        user_agreement_ll.setOnClickListener(v -> {
            String urlUserAgreement = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "User-Agreement";
            CommonFunction._OpenLink(context, urlUserAgreement);
        });
    }
}