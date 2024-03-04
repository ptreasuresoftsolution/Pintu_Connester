package com.connester.job.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;

import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;
    SessionPref sessionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionPref = new SessionPref(SplashActivity.this);
        requestPermissionDialog();
    }

    private void requestPermissionDialog() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_VIDEO)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
        ) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE}, 6);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        {
            if (requestCode == 6) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (CommonFunction.isNetworkConnected(SplashActivity.this)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // checkLoginStatus
                                if (sessionPref.isLogin()) {
                                    openMainActivity();
                                } else {
                                    if (sessionPref.isIntroView()) {
                                        openSignInActivity();
                                    } else {
                                        openIntroActivity();
                                    }
                                }
                            }
                        }, SPLASH_TIME_OUT);
                    } else {
                        Toast.makeText(this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    ActivityCompat.finishAffinity(SplashActivity.this);
                }
            }
        }
    }

    private void openMainActivity() {
        Context context = SplashActivity.this;
        //get user full details
        new UserMaster(context).getLoginUserData(new UserMaster.CallBack() {
            @Override
            public void DataCallBack(Response response) {
                UserRowResponse userRowResponse = (UserRowResponse) response.body();
                if (userRowResponse.status) {
                    if (userRowResponse.dt.profileStatus.equals("ON")) {
                        startActivity(new Intent(context, MainActivity.class));
                        SplashActivity.this.finish();
                    } else {
                        openDisableProfileActivity();
                    }
                }
            }
        }, false);
    }

    private void openDisableProfileActivity() {
        startActivity(new Intent(SplashActivity.this, UserDisableAcActivity.class));
        SplashActivity.this.finish();
    }

    private void openSignInActivity() {
        startActivity(new Intent(this, SignInActivity.class));
        SplashActivity.this.finish();
    }

    private void openIntroActivity() {
        startActivity(new Intent(this, IntroActivity.class));
        SplashActivity.this.finish();
    }
}