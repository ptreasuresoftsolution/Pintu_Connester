package com.connester.job.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.function.SessionPref;
import com.google.android.material.card.MaterialCardView;

public class EditProfileActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        initView();

    }

    TextView userFullName_txt, user_position_tv, user_bio_tv, followers_tv, following_tv;
    ImageView user_banner_iv, user_pic,about_edit;
    MaterialCardView user_banner_edit, back_cv, user_pic_edit;

    private void initView() {
        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });
        user_banner_iv = findViewById(R.id.user_banner_iv);
        user_banner_edit = findViewById(R.id.user_banner_edit);
        user_pic = findViewById(R.id.user_pic);
        user_pic_edit = findViewById(R.id.user_pic_edit);

        userFullName_txt = findViewById(R.id.userFullName_txt);
        user_position_tv = findViewById(R.id.user_position_tv);
        user_bio_tv = findViewById(R.id.user_bio_tv);
        followers_tv = findViewById(R.id.followers_tv);
        following_tv = findViewById(R.id.following_tv);

        about_edit = findViewById(R.id.about_edit);
        about_edit.setOnClickListener(v -> {
            Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.network_list_menu_dialog);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            window.setAttributes(wlp);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ImageView back_iv = dialog.findViewById(R.id.back_iv);
            back_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
    }
}