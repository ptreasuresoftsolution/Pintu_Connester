package com.connester.job.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.connester.job.R;
import com.connester.job.function.SessionPref;

public class MainActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        activity=MainActivity.this;
        sessionPref = new SessionPref(context);
        redirectSettings();
    }

    private void redirectSettings() {
        Intent intent = getIntent();
        if(sessionPref.getUserName().isEmpty() || sessionPref.getUserName().equalsIgnoreCase("")) {//check setupOne
            startActivity(new Intent(context,StepActivity.class));
            finish();
        }
        if(!sessionPref.isLogin()){ // check is notLogin
            startActivity(new Intent(context,SignInActivity.class));
            finish();
        }
        //redirect triggers Click from notification
        if(intent != null){
            if (intent.getStringExtra("trigger") != null &&
                    intent.getStringExtra("trigger").equalsIgnoreCase("EditProfileActivity")){
                startActivity(new Intent(context, EditProfileActivity.class));
            }
        }
    }
}