package com.connester.job.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.UserLoginResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.ApiAuth;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    EditText email_input, password_input;
    TextView openForgot_txt;
    MaterialButton login_btn;
    ImageView singInGoogle_btn, singInFB_btn, password_show;
    LinearLayout openSignUp_ll_btn;

    private void initView() {
        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        openForgot_txt = findViewById(R.id.openForgot_txt);
        login_btn = findViewById(R.id.login_btn);
        singInGoogle_btn = findViewById(R.id.singInGoogle_btn);
        singInFB_btn = findViewById(R.id.singInFB_btn);
        password_show = findViewById(R.id.password_show);
        openSignUp_ll_btn = findViewById(R.id.openSignUp_ll_btn);
    }

    private void setEvent() {
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("email", email_input.getText().toString());
                hashMap.put("password", password_input.getText().toString());
                apiInterface.CALL_LOGIN(hashMap).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        CommonFunction.dismissDialog();
                        if(response.isSuccessful()){
                            if (response.body() != null){
                                UserLoginResponse userLoginResponse = (UserLoginResponse) response.body();
                                if (userLoginResponse.status){
                                    sessionPref.setIsLogin(true);
                                    sessionPref.setUserMasterId(userLoginResponse.userMasterId);
                                    sessionPref.setUserEmail(userLoginResponse.email);
                                    //get user full details
                                    new UserMaster(context).getLoginUserData(new UserMaster.CallBack() {
                                        @Override
                                        public void DataCallBack(Response response) {
                                            UserRowResponse userRowResponse = (UserRowResponse) response.body();
                                            if (userRowResponse.status) {
                                                if (userRowResponse.dt.profileStatus.equals("ON")) {
                                                    startActivity(new Intent(context, MainActivity.class));
                                                    activity.finish();
                                                } else {
                                                    openDisableProfileActivity();
                                                }
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(context, userLoginResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        CommonFunction.dismissDialog();
                        Log.d(LogTag.API_EXCEPTION, "CALL_LOGIN", t);
                    }
                });
            }
        });
        password_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password_show.getTag().equals("show")) {
                    password_show.setTag("hide");
                    password_show.setImageResource(R.drawable.eye_close);
                    password_input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                } else {
                    password_show.setTag("show");
                    password_show.setImageResource(R.drawable.eye_open);
                    password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        singInFB_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        singInGoogle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        openSignUp_ll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SignUpActivity.class));
            }
        });
        openForgot_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ForgotPasswordActivity.class));
            }
        });
    }


    SessionPref sessionPref;
    ApiInterface apiInterface;
    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = SignInActivity.this;
        activity = SignInActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        initView();
        setEvent();
    }

    private void openDisableProfileActivity() {
        startActivity(new Intent(context, UserDisableAcActivity.class));
        activity.finish();
    }
}