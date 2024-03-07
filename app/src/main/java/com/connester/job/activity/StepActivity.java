package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepActivity extends AppCompatActivity {
    EditText fullname_input, username_input, password_input;
    TextView userNameMsg_txt;
    MaterialButton confirm_btn;
    LinearLayout isPasswordRequire_ll;

    private void initView() {
        fullname_input = findViewById(R.id.fullname_input);
        username_input = findViewById(R.id.username_input);
        password_input = findViewById(R.id.password_input);
        userNameMsg_txt = findViewById(R.id.userNameMsg_txt);
        confirm_btn = findViewById(R.id.confirm_btn);
        isPasswordRequire_ll = findViewById(R.id.isPasswordRequire_ll);

        //some initialization
        // password view HIDE/SHOW
        if (!(sessionPref.getUserPassword() != null && !sessionPref.getUserPassword().equals("") && !sessionPref.getUserPassword().isEmpty())) {
            isPasswordRequire_ll.setVisibility(View.VISIBLE);
        }
        //store unique username
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("email", sessionPref.getUserEmail());
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        apiInterface.GET_UNIQUE_USERNAME(hashMap).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                CommonFunction.dismissDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status)
                            username_input.setText(normalCommonResponse.userName);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                CommonFunction.dismissDialog();
                Log.d(LogTag.API_EXCEPTION, "CALL GET_UNIQUE_USERNAME", t);
            }
        });
    }

    private void setEvent() {
        //on input bindOn username_input
        username_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkUserName(username_input.getText().toString());
            }
        });
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("user_master_id", sessionPref.getUserMasterId());
                hashMap.put("apiKey", sessionPref.getApiKey());
                hashMap.put("name", fullname_input.getText().toString());
                hashMap.put("username", username_input.getText().toString());
                if (isPasswordRequire_ll.getVisibility() == View.VISIBLE) {
                    hashMap.put("password", password_input.getText().toString());
                }
                apiInterface.STEP_1_SUBMIT(hashMap).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        CommonFunction.dismissDialog();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    new UserMaster(context).getLoginUserData(new UserMaster.CallBack() {
                                        @Override
                                        public void DataCallBack(Response response) {
                                            UserRowResponse userRowResponse = (UserRowResponse) response.body();
                                            if (userRowResponse.status) {
                                                if (userRowResponse.dt.profileStatus.equals("ON")) {
                                                    startActivity(new Intent(context, HomeActivity.class));
                                                    activity.finish();
                                                } else {
                                                    openDisableProfileActivity();
                                                }
                                            }
                                        }
                                    });
                                } else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        CommonFunction.dismissDialog();
                        Log.d(LogTag.API_EXCEPTION, "CALL STEP_1_SUBMIT", t);
                    }
                });
            }
        });
    }

    SessionPref sessionPref;
    Context context;
    Activity activity;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        context = StepActivity.this;
        activity = StepActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        initView();
        setEvent();
    }

    private void openDisableProfileActivity() {
        startActivity(new Intent(context, AcDisableActivity.class));
        activity.finish();
    }

    public void checkUserName(String userName) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_name", username_input.getText().toString());
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