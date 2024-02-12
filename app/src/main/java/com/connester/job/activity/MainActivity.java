package com.connester.job.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.connester.job.R;
import com.connester.job.function.LogTag;
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
        activity = MainActivity.this;
        sessionPref = new SessionPref(context);
        redirectSettings();

        check();
    }

    LinearLayout feeds_mainList;
    ScrollView scrollView;


    private void check() {
        feeds_mainList = findViewById(R.id.feeds_mainList);
        scrollView = findViewById(R.id.scrollView);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View vv[] = new View[12];
        for (int i = 0; i < 12; i++) {
            vv[i] = layoutInflater.inflate(R.layout.feeds_photos_layout, null);
            feeds_mainList.addView(vv[i], i);
        }
        View find = feeds_mainList.getChildAt(6);
        TextView fullname_txt = find.findViewById(R.id.fullname_txt);
        fullname_txt.setText("Check Layout 6");
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Log.e(LogTag.TMP_LOG, "Sc X:" + scrollView.getX() + " & ScSc X:" + scrollView.getScrollX() + " & view X:" + find.getX() + " & viewSc X:" + find.getScrollX());
                Log.e(LogTag.TMP_LOG, "Sc Y:" + scrollView.getY() + " & ScSc Y:" + scrollView.getScrollY() + " & view Y:" + find.getY() + " & viewSc Y:" + find.getScrollY());
                int location[] = new int[2];
                find.getLocationOnScreen(location);
                int viewX = location[0];
                int viewY = location[1];
                Log.e(LogTag.TMP_LOG, "Location x:" + viewX + " & location Y:" + viewY + " & width:" + find.getWidth() + " & height:" + find.getHeight());
                if ((-(find.getHeight() / 2)) < viewY && viewY < (find.getHeight() / 2) ){
                    Log.e(LogTag.TMP_LOG, "CHK >> is view on screen");
                }

            }
        });
//        addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
//            @Override
//            public void onWindowFocusChanged(boolean hasFocus) {
//            }
//        });
    }

    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

    private void redirectSettings() {
        Intent intent = getIntent();
        if (sessionPref.getUserName().isEmpty() || sessionPref.getUserName().equalsIgnoreCase("")) {//check setupOne
            startActivity(new Intent(context, StepActivity.class));
            finish();
        }
        if (!sessionPref.isLogin()) { // check is notLogin
            startActivity(new Intent(context, SignInActivity.class));
            finish();
        }
        //redirect triggers Click from notification
        if (intent != null) {
            if (intent.getStringExtra("trigger") != null &&
                    intent.getStringExtra("trigger").equalsIgnoreCase("EditProfileActivity")) {
                startActivity(new Intent(context, EditProfileActivity.class));
            }
        }
    }
}