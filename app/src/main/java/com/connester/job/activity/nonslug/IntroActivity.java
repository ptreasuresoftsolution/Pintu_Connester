package com.connester.job.activity.nonslug;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.connester.job.R;
import com.connester.job.activity.SignInActivity;
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