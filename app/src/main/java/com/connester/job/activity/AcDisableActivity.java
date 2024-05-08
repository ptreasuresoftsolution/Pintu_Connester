package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class AcDisableActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    TextView close_type_tv, account_id_tv, close_reason_tv, close_reason_db, mail_req_tv;
    WebView privacy_policy_txt;
    MaterialButton re_activate_ac_mbtn;
    UserRowResponse.Dt userDt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_disable_ac);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        userDt = sessionPref.getUserMasterRowInObject();


        HashMap typeHashMap = new HashMap();
        typeHashMap.put("SUSPENDED", "suspended!");
        typeHashMap.put("TEMPORARY_OFF", "temporary disabled/closed!");

        HashMap<String, Integer> typeIconHashMap = new HashMap();
        typeIconHashMap.put("SUSPENDED", R.drawable.ban_solid);
        typeIconHashMap.put("TEMPORARY_OFF", R.drawable.lock_solid);

        HashMap<String, Integer> typeColorHashMap = new HashMap();
        typeColorHashMap.put("SUSPENDED", R.color.error);
        typeColorHashMap.put("TEMPORARY_OFF", R.color.warning);

        String pisProfileOffReason[] = userDt.profileOffReason.split("-");
        HashMap msgOffHashMap = new HashMap();
        msgOffHashMap.put("BY USER", "Account closed action by You.");
        msgOffHashMap.put("BY ADMIN", "Disabled by admin.");
        msgOffHashMap.put("BY SYSTEM", "Disabled by automated system security.");
        msgOffHashMap.put("BY REPORT", "Suspended because other user report your profile.");


        close_type_tv = findViewById(R.id.close_type_tv);
        close_type_tv.setText(typeHashMap.get(userDt.profileStatus.toUpperCase()).toString());
        close_type_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, typeIconHashMap.get(userDt.profileStatus.toUpperCase()), 0);
        close_type_tv.setCompoundDrawablePadding(4);
        close_type_tv.setTextColor(context.getColor(typeColorHashMap.get(userDt.profileStatus.toUpperCase())));

        account_id_tv = findViewById(R.id.account_id_tv);
        account_id_tv.setText("Account Id - " + String.format("%04d", Integer.parseInt(userDt.userMasterId)));

        close_reason_tv = findViewById(R.id.close_reason_tv);
        close_reason_tv.setText("You no longer access your account! further reason of " + msgOffHashMap.get(pisProfileOffReason[pisProfileOffReason.length - 1]));

        close_reason_db = findViewById(R.id.close_reason_db);
        close_reason_db.setText(userDt.profileOffReason);

        privacy_policy_txt = findViewById(R.id.privacy_policy_txt);
        privacy_policy_txt.setBackgroundColor(Color.TRANSPARENT);
        privacy_policy_txt.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                CommonFunction._OpenLinkInMyWebview(context, "", url);
                return true;
            }
        });
        privacy_policy_txt.loadData("<p style='background:transparent'>Check the <a style='color:darkred;text-decoration:none' href='" + Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "User-Agreement'>User Agreement</a>, and <a style='color:darkred;text-decoration:none' href='" + Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/" + "Privacy-Policy'>Privacy Policy</a> you agree to Connester. Check of your violation or closed/Disabled Profile.</p>", "text/html; charset=UTF-8", null);

        mail_req_tv = findViewById(R.id.mail_req_tv);
        mail_req_tv.setVisibility(View.GONE);
        re_activate_ac_mbtn = findViewById(R.id.re_activate_ac_mbtn);
        if (pisProfileOffReason[pisProfileOffReason.length - 1].equalsIgnoreCase("BY USER")) {
            re_activate_ac_mbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    apiInterface.ACCOUNT_REACTIVATE_CALL(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
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
        } else { // request approve in  mail
            mail_req_tv.setVisibility(View.VISIBLE);
            re_activate_ac_mbtn.setText("Mail - " + Constant.userEmail);
            re_activate_ac_mbtn.setOnClickListener(v -> {
                CommonFunction.mailInApp(context, Constant.userEmail);
            });
        }
    }
}