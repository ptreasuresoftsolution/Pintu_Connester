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
import com.connester.job.RetrofitConnection.jsontogson.SignUpOtpResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserLoginResponse;
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

public class SignUpActivity extends AppCompatActivity {
    EditText email_input, password_input, verifyCode_input;
    MaterialButton signUp_btn;
    ImageView singUpGoogle_btn, singUpFB_btn, password_show;
    LinearLayout openSignIn_ll_btn;
    TextView resendCode_btn;
    LinearLayout verifyCode_ll;

    private void initView() {
        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        verifyCode_input = findViewById(R.id.verifyCode_input);
        resendCode_btn = findViewById(R.id.resendCode_btn);
        signUp_btn = findViewById(R.id.signUp_btn);
        singUpGoogle_btn = findViewById(R.id.singUpGoogle_btn);
        singUpFB_btn = findViewById(R.id.singUpFB_btn);
        password_show = findViewById(R.id.password_show);
        verifyCode_ll = findViewById(R.id.verifyCode_ll);
        openSignIn_ll_btn = findViewById(R.id.openSignIn_ll_btn);
    }

    private void setEvent() {
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signUp_btn.getText().toString().equalsIgnoreCase("Sign up")) {
                    //call for otp
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("email", email_input.getText().toString());
                    hashMap.put("password", password_input.getText().toString());
                    apiInterface.SIGNUP_SUBMIT_FOR_OTP(hashMap).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            CommonFunction.dismissDialog();
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    SignUpOtpResponse signUpOtpResponse = (SignUpOtpResponse) response.body();
                                    if (signUpOtpResponse.status) {
                                        signUp_btn.setText("Verify Code");
                                        codeTime = signUpOtpResponse.dt.codeTime;
                                        verifyCode_ll.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(context, signUpOtpResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {
                            CommonFunction.dismissDialog();
                            Log.d(LogTag.API_EXCEPTION, "CALL SIGNUP FOR OTP", t);
                        }
                    });
                } else {
                    //call of verify otp (Final call)
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("email", email_input.getText().toString());
                    hashMap.put("password", password_input.getText().toString());
                    hashMap.put("otp", verifyCode_input.getText().toString());
                    hashMap.put("codeTime_E", codeTime);
                    apiInterface.SIGNUP_SUBMIT_WITH_OTP(hashMap).enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            CommonFunction.dismissDialog();
                            if (response.isSuccessful()){
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
                                                        startActivity(new Intent(context, HomeActivity.class));
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
                            Log.d(LogTag.API_EXCEPTION, "CALL SINGUP SUBMIT WITH OTP", t);
                        }
                    });
                }
            }
        });
        resendCode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.put("email", email_input.getText().toString());
                apiInterface.SIGNUP_OTP_RESEND(hashMap).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        CommonFunction.dismissDialog();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                SignUpOtpResponse signUpOtpResponse = (SignUpOtpResponse) response.body();
                                if (signUpOtpResponse.status) {
                                    codeTime = signUpOtpResponse.dt.codeTime;
                                } else {
                                    Toast.makeText(context, signUpOtpResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        CommonFunction.dismissDialog();
                        Log.d(LogTag.API_EXCEPTION, "CALL RESEND", t);
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
                    password_input.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    password_show.setTag("show");
                    password_show.setImageResource(R.drawable.eye_open);
                    password_input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        singUpFB_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        singUpGoogle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        openSignIn_ll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    String codeTime;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = SignUpActivity.this;
        activity = SignUpActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        initView();
        setEvent();
    }
    private void openDisableProfileActivity() {
        startActivity(new Intent(context, AcDisableActivity.class));
        activity.finish();
    }
}