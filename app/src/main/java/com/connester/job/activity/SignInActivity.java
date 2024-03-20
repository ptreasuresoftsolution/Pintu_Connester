package com.connester.job.activity;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.Googlelogin.GoogleLogin;
import com.connester.job.Googlelogin.LoginResponse;
import com.connester.job.Googlelogin.SocialLoginData;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.UserLoginResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.activity.nonslug.ForgotPasswordActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
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
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                UserLoginResponse userLoginResponse = (UserLoginResponse) response.body();
                                if (userLoginResponse.status) {
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
                                } else {
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
                facebookLogin.getLoginManager().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
            }
        });
        facebookLogin.setFacebookLoginCallback(new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                Toast.makeText(context, "facebook login done", Toast.LENGTH_SHORT).show();
            }
        });

        singInGoogle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleLogin.loginByActivity(SignInActivity.this, new LoginResponse() {
                    @Override
                    public void result(SocialLoginData loginData) {
//                        loginData.fname, loginData.email, loginData.acId
                        Log.e(LogTag.TMP_LOG, "Google id : " + loginData.acId + " nm : " + loginData.fname);
                        Toast.makeText(context, "Google id : " + loginData.acId, Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, "Google login done", Toast.LENGTH_SHORT).show();
                    }
                });
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
    FacebookLogin facebookLogin;
    GoogleLogin googleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        context = SignInActivity.this;
        activity = SignInActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        googleLogin = new GoogleLogin(context, this);

        facebookLogin = new FacebookLogin();
        facebookLogin.initialize(context);
        initView();
        setEvent();
    }

    private void openDisableProfileActivity() {
        startActivity(new Intent(context, AcDisableActivity.class));
        activity.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleLogin.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        facebookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        googleLogin.getOnResultSet().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    class FacebookLogin {

        private CallbackManager callbackManager;
        private LoginManager loginManager;

        public CallbackManager getCallbackManager() {
            return callbackManager;
        }

        public LoginManager getLoginManager() {
            return loginManager;
        }

        GraphRequest.GraphJSONObjectCallback graphJSONObjectCallback;

        public void setFacebookLoginCallback(GraphRequest.GraphJSONObjectCallback graphJSONObjectCallback) {
            this.graphJSONObjectCallback = graphJSONObjectCallback;
        }

        void initialize(Context context) {
            FacebookSdk.sdkInitialize(context);
            callbackManager = CallbackManager.Factory.create();
            Log.e(LogTag.TMP_LOG, "Facebook login initialize");
            //login
            loginManager = LoginManager.getInstance();
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                            if (object != null) {
                                try {
                                    String name = object.getString("name") != null ? object.getString("name") : "";
//                                    String email = object.getString("email") != null ? object.getString("email") : null;
                                    String fbUserID = object.getString("id");
                                    Log.e(LogTag.TMP_LOG, "FB id : " + fbUserID + " nm : " + name);
                                    Toast.makeText(context, "fb id : " + fbUserID, Toast.LENGTH_SHORT).show();

                                    if (graphJSONObjectCallback != null)
                                        graphJSONObjectCallback.onCompleted(object, response);
                                    disconnectFromFacebook();
                                    // do action after Facebook login success
                                    // or call your API
                                } catch (JSONException | NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putString(
                            "fields",
                            "id, name, email, gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Log.v("LoginScreen", "---onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    // here write code when get error
                    Log.v("LoginScreen", "----onError: "
                            + error.getMessage());
                }
            });
        }

        private void disconnectFromFacebook() {
            if (AccessToken.getCurrentAccessToken() == null) {
                return; // already logged out
            }
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    LoginManager.getInstance().logOut();
                }
            }).executeAsync();
        }
    }
}