package com.connester.job.activity.settingActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.SplashActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText old_password_et, new_password_et, confirm_password_et;
    MaterialButton save_pass_mbtn;
    SessionPref sessionPref;
    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        old_password_et = findViewById(R.id.old_password_et);
        new_password_et = findViewById(R.id.new_password_et);
        confirm_password_et = findViewById(R.id.confirm_password_et);
        save_pass_mbtn = findViewById(R.id.save_pass_mbtn);

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);

        save_pass_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("old_pass", old_password_et.getText().toString());
            hashMap.put("new_pass", new_password_et.getText().toString());
            hashMap.put("confirm_pass", confirm_password_et.getText().toString());
            ApiClient.getClient().create(ApiInterface.class).CHANGE_PASSWORD_CALL(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                sessionPref.logOutPref();
                                ActivityCompat.finishAffinity(activity);
                                startActivity(new Intent(context, SplashActivity.class));
                            } else
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
    }
}