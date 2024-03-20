package com.connester.job.Googlelogin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.gson.Gson;

public class GoogleLogin {
    private static final int GOOGLE_SING_IN = 1001;
    Context context;
    AppCompatActivity compatActivity;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;
    public boolean isAlreadyLogin = false;
    private LoginResponse loginResponse;
    private OnResultSet onResultSet = new OnResultSet() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            try {
                account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                loginResponse.result(getLoginData());
            } catch (ApiException e) {
                Log.e("Exception", "Google login ", e);
            }
        }
    };

    public GoogleLogin(Context context, AppCompatActivity compatActivity) {
        this.context = context;
        this.compatActivity = compatActivity;
        config();
    }

    public void config() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void start() {
        account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null)
            isAlreadyLogin = true;
    }

    public void loginByFragment(Fragment fragment, LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
        if (isAlreadyLogin)
            loginResponse.result(getLoginData());
        else
            fragment.startActivityForResult(googleSignInClient.getSignInIntent(), GOOGLE_SING_IN);
    }

    public void loginByActivity(AppCompatActivity appCompatActivity, LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
        if (isAlreadyLogin)
            loginResponse.result(getLoginData());
        else
            appCompatActivity.startActivityForResult(googleSignInClient.getSignInIntent(), GOOGLE_SING_IN);
    }

    public SocialLoginData getLoginData() {
        if (account != null) {
            SocialLoginData loginSocialHelper = new SocialLoginData();
            loginSocialHelper.acId = account.getId();
            loginSocialHelper.email = account.getEmail();
            loginSocialHelper.fname = account.getDisplayName();
            loginSocialHelper.lname = account.getGivenName().equals("") ? account.getFamilyName() : account.getDisplayName();
            if (account.getPhotoUrl() != null)
                loginSocialHelper.photoUrl = account.getPhotoUrl().getPath();
            Log.i("Google Login Data", new Gson().toJson(loginSocialHelper));
            return loginSocialHelper;
        }
        return null;
    }

    public OnResultSet getOnResultSet() {
        return onResultSet;
    }

    public boolean isAlreadyLogin() {
        return isAlreadyLogin;
    }
}
