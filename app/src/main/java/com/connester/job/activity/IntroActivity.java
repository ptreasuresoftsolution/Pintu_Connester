package com.connester.job.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.QuickContactBadge;

import com.connester.job.R;
import com.connester.job.function.SessionPref;

public class IntroActivity extends AppCompatActivity {
    FrameLayout go_next;
    SessionPref sessionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        sessionPref = new SessionPref(this);
        go_next = findViewById(R.id.go_next);
        go_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionPref.setIntroView(true);
                startActivity(new Intent(IntroActivity.this, SignInActivity.class));
                IntroActivity.this.finish();
            }
        });
    }
}