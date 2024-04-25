package com.connester.job.activity.settingActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeUsernameActivity extends AppCompatActivity {
    EditText username_et;
    MaterialButton save_username_mbtn;
    TextView userNameMsg_txt;
    SessionPref sessionPref;
    Context context;
    Activity activity;
    ImageView back_iv;
    UserRowResponse.Dt dt;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        username_et = findViewById(R.id.username_et);
        save_username_mbtn = findViewById(R.id.save_username_mbtn);
        userNameMsg_txt = findViewById(R.id.userNameMsg_txt);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        dt = sessionPref.getUserMasterRowInObject();
        username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkUserName(username_et.getText().toString());
            }
        });
        username_et.setText(dt.userName);

        save_username_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("username", username_et.getText().toString());
            ApiClient.getClient().create(ApiInterface.class).CHANGE_USERNAME_CALL(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                new UserMaster(context).getLoginUserData(new UserMaster.CallBack() {
                                    @Override
                                    public void DataCallBack(Response response) {
                                        UserRowResponse userRowResponse = (UserRowResponse) response.body();
                                        if (userRowResponse.status) {
                                            dt = userRowResponse.dt;
                                        }
                                    }
                                });
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
    }

    public void checkUserName(String userName) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_name", userName);
        if (apiInterface.CHECK_USERNAME(hashMap).isExecuted()) {
            if (!apiInterface.CHECK_USERNAME(hashMap).isCanceled())
                apiInterface.CHECK_USERNAME(hashMap).cancel();
        }
        apiInterface.CHECK_USERNAME(hashMap).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        userNameMsg_txt.setText(normalCommonResponse.msg);
                        if (normalCommonResponse.status)
                            userNameMsg_txt.setTextColor(getColor(R.color.success));
                        else
                            userNameMsg_txt.setTextColor(getColor(R.color.error));
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(LogTag.API_EXCEPTION, "CALL CHECK_USERNAME", t);
            }
        });
    }
}