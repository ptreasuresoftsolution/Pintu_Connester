package com.connester.job.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.Constant;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    UserRowResponse.Dt userDt;
    UserMaster userMaster;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        userMaster = new UserMaster(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        initView();
        setData();
    }


    TextView userFullName_txt, user_position_tv, user_bio_tv, followers_tv, following_tv;
    ImageView user_banner_iv, user_pic, about_edit, profile_option_iv;
    MaterialCardView user_banner_edit, back_cv, user_pic_edit;
    //    LinearLayout;
    MaterialButton post_activity_mbtn, inbox_open_mbtn, setting_open_mbtn;

    private void initView() {
        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });
        user_banner_iv = findViewById(R.id.user_banner_iv);
        user_banner_edit = findViewById(R.id.user_banner_edit);
        user_banner_edit.setOnClickListener(v -> {
            //open photo picker and upload banner api call
        });
        user_pic = findViewById(R.id.user_pic);
        user_pic_edit = findViewById(R.id.user_pic_edit);
        user_pic_edit.setOnClickListener(v -> {
            //open photo picker and upload pic api call
        });

        userFullName_txt = findViewById(R.id.userFullName_txt);
        user_position_tv = findViewById(R.id.user_position_tv);
        user_bio_tv = findViewById(R.id.user_bio_tv);
        followers_tv = findViewById(R.id.followers_tv);
        following_tv = findViewById(R.id.following_tv);

        post_activity_mbtn = findViewById(R.id.post_activity_mbtn);
        post_activity_mbtn.setOnClickListener(v -> {
            startActivity(new Intent(context, Activity_Activity.class));
        });
        inbox_open_mbtn = findViewById(R.id.inbox_open_mbtn);
        inbox_open_mbtn.setOnClickListener(v -> {
            startActivity(new Intent(context, MessageActivity.class));
        });
        setting_open_mbtn = findViewById(R.id.setting_open_mbtn);
        setting_open_mbtn.setOnClickListener(v -> {
            startActivity(new Intent(context, UserMenuActivity.class));
        });
        profile_option_iv = findViewById(R.id.profile_option_iv);
        profile_option_iv.setOnClickListener(v -> {
            BottomSheetDialog profileOptionDialog = new BottomSheetDialog(context);
            profileOptionDialog.setContentView(R.layout.common_option_dialog_layout);

            LinearLayout share_profile_option_LL = profileOptionDialog.findViewById(R.id.share_profile_option_LL);
            share_profile_option_LL.setVisibility(View.VISIBLE);
            share_profile_option_LL.setOnClickListener(v1 -> {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("action", "pick");
                intent.putExtra("message", Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/profile/" + userDt.profileLink);
                startActivity(intent);
            });
            LinearLayout web_portfolio_option_LL = profileOptionDialog.findViewById(R.id.web_portfolio_option_LL);
            web_portfolio_option_LL.setVisibility(View.VISIBLE);
            web_portfolio_option_LL.setOnClickListener(v1 -> {
                startActivity(new Intent(context, ShowPortfolioApplyJobActivity.class));
            });
            LinearLayout edit_profile_option_LL = profileOptionDialog.findViewById(R.id.edit_profile_option_LL);
            edit_profile_option_LL.setVisibility(View.VISIBLE);
            edit_profile_option_LL.setOnClickListener(v1 -> {
                openEditInfoDialog();
            });

            profileOptionDialog.show();
        });

        about_edit = findViewById(R.id.about_edit);
        about_edit.setOnClickListener(v -> {

        });
    }

    private void setData() {
         userMaster.getUserClmData(new UserMaster.CallBack() {
             @Override
             public void DataCallBack(Response response) {
                 UserRowResponse userRowResponse = (UserRowResponse) response.body();
                 if (userRowResponse.status) {
                     userDt = userRowResponse.dt;
                     imgPath = userRowResponse.imgPath;


                     Glide.with(context).load(imgPath + userDt.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);
                 }
             }
         }, "*", true);
    }

    private void openEditInfoDialog() {
        Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.editprofile_info_dialog);
        setFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        EditText fullname_input = dialog.findViewById(R.id.fullname_input);
        EditText phone_number_input = dialog.findViewById(R.id.phone_number_input);
        EditText date_birth_input = dialog.findViewById(R.id.date_birth_input);
        RadioGroup radio_group_gender = dialog.findViewById(R.id.radio_group_gender);
        EditText city_input = dialog.findViewById(R.id.city_input);
        EditText country_region_input = dialog.findViewById(R.id.country_region_input);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);


        dialog.show();
    }

    private void setFullScreenSetting(Dialog dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}