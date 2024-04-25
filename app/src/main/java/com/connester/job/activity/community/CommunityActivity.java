package com.connester.job.activity.community;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.GroupRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.activity.community.fragment.CommunityMembersFragment;
import com.connester.job.activity.community.fragment.CommunityPostFragment;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.connester.job.module.VisitMaster;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CommunityActivity extends AppCompatActivity {
    String community_master_id = null;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    MaterialCardView back_cv;
    TabLayout tab_layout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();
    ApiInterface apiInterface;
    ImageView group_banner_iv, group_logo_iv, privacy_img;
    TextView group_title_txt, privacy_tv;
    MaterialButton join_exit_mbtn, more_option_mbtn;
    ScrollView scrollView;
    FrameLayout progressBar;
    UserMaster userMaster;
    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if (getIntent() != null) {
            community_master_id = getIntent().getStringExtra("community_master_id");
        }
        if (community_master_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }

        context = CommunityActivity.this;
        activity = CommunityActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        userMaster = new UserMaster(context, CommunityActivity.this);
        userMaster.initReportAttachmentLauncher();

        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });

        group_banner_iv = findViewById(R.id.group_banner_iv);
        group_logo_iv = findViewById(R.id.group_logo_iv);

        group_title_txt = findViewById(R.id.group_title_txt);
        privacy_img = findViewById(R.id.privacy_img);
        privacy_tv = findViewById(R.id.privacy_tv);

        join_exit_mbtn = findViewById(R.id.join_exit_mbtn);

        more_option_mbtn = findViewById(R.id.more_option_mbtn);
        more_option_mbtn.setOnClickListener(v -> {
            //more option popup open
            openMoreOptionDialog();
        });

        setData();

        tab_layout = findViewById(R.id.tab_layout);

        fragments.add(new CommunityPostFragment(scrollView, community_master_id, progressBar));
        fragmentsTitle.add("Posts");
        fragments.add(new CommunityMembersFragment(scrollView, community_master_id, progressBar));
        fragmentsTitle.add("Group Members");

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = fragments.get(tab.getPosition());
                CommonFunction._LoadFirstFragment(CommunityActivity.this, R.id.container, fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        CommonFunction._LoadFirstFragment(CommunityActivity.this, R.id.container, fragments.get(0));

        new VisitMaster(context, activity).visitedCommunity(community_master_id);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                setData();
                tab_layout.selectTab(tab_layout.getTabAt(tab_layout.getSelectedTabPosition()));
            }
        });
    }

    GroupRowResponse.GroupRow groupRow;
    GroupRowResponse groupRowResponse;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call

    private void setData() {
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("community_master_id", community_master_id);

        apiInterface.GROUP_ROW(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                        groupRowResponse = (GroupRowResponse) response.body();
                        if (groupRowResponse.status) {
                            groupRow = groupRowResponse.groupRow;
                            //handling redirect for page is not active
                            if (!groupRow.groupStatus.equalsIgnoreCase("ACTIVE")) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(context, "Page not found! This page is deactivated! Pleas go back.", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                imgPath = groupRowResponse.imgPath;
                                Glide.with(context).load(imgPath + groupRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(group_banner_iv);
                                Glide.with(context).load(imgPath + groupRow.logo).centerCrop().placeholder(R.drawable.default_groups_pic).into(group_logo_iv);

                                group_title_txt.setText(groupRow.name);
                                privacy_tv.setText(CommonFunction.capitalize(groupRow.type));
                                //set icon
                                if (groupRow.type.equalsIgnoreCase("PUBLIC")) {
                                    privacy_img.setImageResource(R.drawable.people_fill_public);
                                } else {
                                    privacy_img.setImageResource(R.drawable.person_fill_lock_private);
                                }

                                if (groupRowResponse.isMember) {
                                    join_exit_mbtn.setText("Exit group");
                                    join_exit_mbtn.setIcon(getDrawable(R.drawable.log_out));
                                    join_exit_mbtn.setOnClickListener(v -> {
                                        //exit call manage
                                        CommonFunction.PleaseWaitShow(context);
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                                        hashMap.put("apiKey", sessionPref.getApiKey());
                                        hashMap.put("community_master_id", community_master_id);
                                        hashMap.put("exit_user_master_id", sessionPref.getUserMasterId());
                                        apiInterface.GROUP_EXIT_CALL(hashMap).enqueue(new MyApiCallback(progressBar) {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                super.onResponse(call, response);
                                                progressBar.setVisibility(View.GONE);
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null) {
                                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                                        if (normalCommonResponse.status) setData();
                                                        else
                                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    });
                                } else if (groupRowResponse.isRequested) {
                                    join_exit_mbtn.setText("Pending");
                                    join_exit_mbtn.setIcon(getDrawable(R.drawable.account_multiple_plus_follow));
                                    join_exit_mbtn.setEnabled(false);
                                } else {
                                    join_exit_mbtn.setText("Join");
                                    join_exit_mbtn.setIcon(getDrawable(R.drawable.account_multiple_plus_follow));
                                    join_exit_mbtn.setOnClickListener(v -> {
                                        //join call manage
                                        CommonFunction.PleaseWaitShow(context);
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                                        hashMap.put("apiKey", sessionPref.getApiKey());
                                        hashMap.put("community_master_id", community_master_id);
                                        apiInterface.GROUP_JOIN_REQUEST(hashMap).enqueue(new MyApiCallback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                super.onResponse(call, response);
                                                progressBar.setVisibility(View.GONE);
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null) {
                                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                                        if (normalCommonResponse.status) setData();
                                                        else
                                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    });
                                }

                                tab_layout.selectTab(tab_layout.getTabAt(0));
                            }
                        } else {
                            groupRow = null;
                            Toast.makeText(context, groupRowResponse.msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });
    }

    private void openMoreOptionDialog() {
        BottomSheetDialog optionDialog = new BottomSheetDialog(context);
        optionDialog.setContentView(R.layout.common_option_dialog_layout);
        LinearLayout about_group_LL = optionDialog.findViewById(R.id.about_group_LL);
        about_group_LL.setVisibility(View.VISIBLE);
        about_group_LL.setOnClickListener(v -> {
            openGroupDetailsDialog();
            optionDialog.dismiss();
        });

        LinearLayout link_copy_LL = optionDialog.findViewById(R.id.link_copy_LL);
        link_copy_LL.setVisibility(View.VISIBLE);
        link_copy_LL.setOnClickListener(v -> {
            if (community_master_id != null) {
                String urlLink = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/community/" + groupRow.communityLink;
                CommonFunction.copyToClipBoard(context, urlLink);
                optionDialog.dismiss();
            }
        });

        LinearLayout report_LL = optionDialog.findViewById(R.id.report_LL);
        report_LL.setVisibility(View.VISIBLE);
        report_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMaster.openReportDialog("Group", "community_master", community_master_id, context);
                optionDialog.dismiss();
            }
        });
        optionDialog.show();
    }

    private void openGroupDetailsDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_details_view_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView description_tv = dialog.findViewById(R.id.description_tv);
        description_tv.setText(groupRow.bio);
        TextView member_tv = dialog.findViewById(R.id.member_tv);
        member_tv.setText("+" + groupRow.members + " Members");
        TextView industry_tv = dialog.findViewById(R.id.industry_tv);
        industry_tv.setText(groupRow.industry);
        TextView rules_tv = dialog.findViewById(R.id.rules_tv);
        rules_tv.setText(groupRow.rules);
        TextView group_type_tv = dialog.findViewById(R.id.group_type_tv);
        group_type_tv.setText(groupRow.type);

        dialog.show();
    }

}