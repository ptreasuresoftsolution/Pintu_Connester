package com.connester.job.activity.mycommunity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.GroupMembersListResponse;
import com.connester.job.RetrofitConnection.jsontogson.GroupRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ManageMyCommunityActivity extends AppCompatActivity {
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
    MaterialCardView group_info_edit;
    TextView group_title_txt, privacy_tv;
    MaterialButton details_mbtn, setting_open_mbtn;
    ScrollView scrollView;
    FrameLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_community);
        if (getIntent() != null) {
            community_master_id = getIntent().getStringExtra("community_master_id");
        } else {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
        if (community_master_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }

        context = ManageMyCommunityActivity.this;
        activity = ManageMyCommunityActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });

        group_banner_iv = findViewById(R.id.group_banner_iv);
        group_logo_iv = findViewById(R.id.group_logo_iv);
        group_info_edit = findViewById(R.id.group_info_edit);
        group_info_edit.setOnClickListener(v -> {
            openGroupEditDialog();
        });
        activityResultLauncherForGroupBanner = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && d_group_banner_iv != null) {
                groupBannerFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(groupBannerFile).centerCrop().placeholder(R.drawable.user_default_banner).into(d_group_banner_iv);
            }
        });
        activityResultLauncherForGroupLogo = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && d_group_logo_iv != null) {
                groupLogoFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(groupLogoFile).centerCrop().placeholder(R.drawable.default_groups_pic).into(d_group_logo_iv);
            }
        });

        group_title_txt = findViewById(R.id.group_title_txt);
        privacy_img = findViewById(R.id.privacy_img);
        privacy_tv = findViewById(R.id.privacy_tv);

        details_mbtn = findViewById(R.id.details_mbtn);
        details_mbtn.setOnClickListener(v -> {
            openGroupDetailsDialog();
        });
        setting_open_mbtn = findViewById(R.id.setting_open_mbtn);
        setting_open_mbtn.setOnClickListener(v -> {
            openGroupSettingDialog();
        });

        setData();

        tab_layout = findViewById(R.id.tab_layout);

        fragments.add(new GroupPostFragment(scrollView, community_master_id, progressBar));
        fragmentsTitle.add("Posts");
        fragments.add(new GroupMembersFragment(scrollView,community_master_id, progressBar));
        fragmentsTitle.add("Group Members");
        fragments.add(new MemberRequestFragment(scrollView, community_master_id, progressBar));
        fragmentsTitle.add("Member Request");

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = fragments.get(tab.getPosition());
                CommonFunction._LoadFirstFragment(ManageMyCommunityActivity.this, R.id.container, fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        CommonFunction._LoadFirstFragment(ManageMyCommunityActivity.this, R.id.container, fragments.get(0));
    }

    private void openGroupDetailsDialog() {
        CommonFunction.PleaseWaitShow(context);
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_create_manage_dialog);
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


    private void openGroupSettingDialog() {
        BottomSheetDialog settingDialog = new BottomSheetDialog(context);
        settingDialog.setContentView(R.layout.common_option_dialog_layout);

        LinearLayout blocking_person_ll = settingDialog.findViewById(R.id.blocking_person_ll);
        blocking_person_ll.setVisibility(View.VISIBLE);
        blocking_person_ll.setOnClickListener(v -> {
            openBlockingMemberListDialog();
            settingDialog.dismiss();
        });

        LinearLayout group_close_LL = settingDialog.findViewById(R.id.group_close_LL);
        group_close_LL.setVisibility(View.VISIBLE);
        group_close_LL.setOnClickListener(v -> {

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Close Group!");
            alertDialog.setMessage("You’ll also lose any recommendations and endorsements you’ve given or received. and no more members added in this group\n" +
                    "\n" +
                    "So, You are confirm to the close this group??\n" +
                    "And if click on continue then closed your group.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //call api close account
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("community_master_id", community_master_id);

                    apiInterface.GROUP_CLOSED(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        alertDialog.dismiss();
                                        settingDialog.dismiss();
                                        Intent intent = new Intent(context, GroupDisableActivity.class);
                                        intent.putExtra("community_master_id", community_master_id);
                                        intent.putExtra("GroupRowResponse", new Gson().toJson(groupRowResponse));
                                        startActivity(intent);
                                        finish();
                                    }
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
            alertDialog.show();
        });
        settingDialog.show();
    }

    private void openBlockingMemberListDialog() {
        CommonFunction.PleaseWaitShow(context);
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_view_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        ListView list_lt = dialog.findViewById(R.id.list_lt);

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("community_master_id", community_master_id);

        apiInterface.GROUP_BLOCKED_MEMBERS_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        GroupMembersListResponse groupMembersListResponse = (GroupMembersListResponse) response.body();
                        if (groupMembersListResponse.status) {
                            list_lt.setAdapter(getBlockedMemberGroupAdapter(groupMembersListResponse));
                        } else
                            Toast.makeText(context, groupMembersListResponse.msg, Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
        dialog.show();
    }

    private ListAdapter getBlockedMemberGroupAdapter(GroupMembersListResponse groupMembersListResponse) {
        String imgPath = groupMembersListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return groupMembersListResponse.dt.size();
            }

            @Override
            public GroupMembersListResponse.Dt getItem(int position) {
                return groupMembersListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.user_pic_two_btn_list_item, parent, false);

                GroupMembersListResponse.Dt row = getItem(position);
                View.OnClickListener openUser = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", String.valueOf(row.userMasterId));
                    startActivity(intent);
                };

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_groups_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUser);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUser);
                MaterialButton first_mbtn = view.findViewById(R.id.first_mbtn);
                first_mbtn.setVisibility(View.GONE);

                MaterialButton second_mbtn = view.findViewById(R.id.second_mbtn);
                second_mbtn.setText("Unblock");
                second_mbtn.setOnClickListener(v1 -> {
                    CommonFunction.PleaseWaitShow(context);
                    HashMap hashMap = new HashMap();
                    hashMap.put("user_master_id", sessionPref.getUserMasterId());
                    hashMap.put("apiKey", sessionPref.getApiKey());
                    hashMap.put("memberId", row.userMasterId);
                    hashMap.put("community_master_id", community_master_id);

                    apiInterface.UN_BLOCKED_GROUP_MEMBERS_LIST(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        removeItem(position);
                                    }
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                });
                return view;
            }

            private void removeItem(int position) {
                groupMembersListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    ActivityResultLauncher activityResultLauncherForGroupBanner, activityResultLauncherForGroupLogo;
    File groupLogoFile, groupBannerFile;
    ImageView d_group_banner_iv, d_group_logo_iv;

    private void openGroupEditDialog() {
        CommonFunction.PleaseWaitShow(context);
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_create_manage_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        String[] groupType = context.getResources().getStringArray(R.array.group_type);

        MaterialCardView back_cv = dialog.findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        d_group_banner_iv = dialog.findViewById(R.id.group_banner_iv);
        if (groupRow != null && groupRow.banner != null)
            Glide.with(context).load(imgPath + groupRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(d_group_banner_iv);
        MaterialCardView group_banner_edit = dialog.findViewById(R.id.group_banner_edit);
        group_banner_edit.setOnClickListener(v -> {
            activityResultLauncherForGroupBanner.launch(("image/*"));
        });

        d_group_logo_iv = dialog.findViewById(R.id.group_logo_iv);
        if (groupRow != null && groupRow.logo != null)
            Glide.with(context).load(imgPath + groupRow.logo).centerCrop().placeholder(R.drawable.default_groups_pic).into(d_group_logo_iv);
        MaterialCardView group_logo_edit = dialog.findViewById(R.id.group_logo_edit);
        group_logo_edit.setOnClickListener(v -> {
            activityResultLauncherForGroupLogo.launch(("image/*"));
        });

        EditText group_nm_input = dialog.findViewById(R.id.group_nm_input);
        if (groupRow != null && groupRow.name != null)
            group_nm_input.setText(groupRow.name);

        EditText description_et = dialog.findViewById(R.id.description_et);
        if (groupRow != null && groupRow.bio != null)
            description_et.setText(groupRow.bio);

        EditText industry_input = dialog.findViewById(R.id.industry_input);
        if (groupRow != null && groupRow.industry != null)
            industry_input.setText(groupRow.industry);

        Spinner group_type_sp = dialog.findViewById(R.id.group_type_sp);
        if (groupRow != null && groupRow.type != null)
            group_type_sp.setSelection(Arrays.asList(groupType).indexOf(groupRow.type));

        EditText rules_et = dialog.findViewById(R.id.rules_et);
        if (groupRow != null && groupRow.rules != null)
            rules_et.setText(groupRow.rules);

        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.PleaseWaitShow(context);
                CommonFunction.PleaseWaitShowMessage("Files is compressing...");
                try {

                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM)
                            .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                            .addFormDataPart("apiKey", "RBqtNuh+0qdrKn+Bb9WafA==")
                            .addFormDataPart("community_master_id", community_master_id)
                            .addFormDataPart("name", group_nm_input.getText().toString())
                            .addFormDataPart("bio", description_et.getText().toString())
                            .addFormDataPart("industry", industry_input.getText().toString())
                            .addFormDataPart("type", groupType[group_type_sp.getSelectedItemPosition()])
                            .addFormDataPart("rules", rules_et.getText().toString());

                    File imgFileLogo = null;
                    if (groupLogoFile != null) {
                        imgFileLogo = new Compressor(context)
                                .setMaxWidth(1080)
                                .setMaxWidth(800)
                                .setQuality(50)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                                .compressToFile(groupLogoFile);
                        builder.addFormDataPart("logo", imgFileLogo.getName(),
                                RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFileLogo)), imgFileLogo));
                        builder.addFormDataPart("old_logo", groupRow.logo);

                    }
                    File imgFileBanner = null;
                    if (groupBannerFile != null) {
                        imgFileBanner = new Compressor(context)
                                .setMaxWidth(1080)
                                .setMaxWidth(800)
                                .setQuality(50)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                                .compressToFile(groupBannerFile);
                        builder.addFormDataPart("banner", imgFileBanner.getName(),
                                RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFileBanner)), imgFileBanner));
                        builder.addFormDataPart("old_banner", groupRow.banner);
                    }


                    CommonFunction.PleaseWaitShowMessage("Files is compressed completed");


                    RequestBody body = builder.build();
                    CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                    File finalImgFileLogo = imgFileLogo;
                    File finalImgFileBanner = imgFileBanner;
                    apiInterface.GROUP_CREATE_MANAGE_CALL(body).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        if (finalImgFileLogo != null) finalImgFileLogo.delete();
                                        if (finalImgFileBanner != null) finalImgFileBanner.delete();

                                        setData();
                                        dialog.dismiss();
                                    }
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(LogTag.EXCEPTION, "Image Compress from Business page create Exception", e);
                }
            }
        });

        dialog.show();
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
                        groupRowResponse = (GroupRowResponse) response.body();
                        if (groupRowResponse.status) {
                            groupRow = groupRowResponse.groupRow;
                            //handling redirect for page is not active
                            if (!groupRow.groupStatus.equalsIgnoreCase("ACTIVE")) {
                                Intent intent = new Intent(context, GroupDisableActivity.class);
                                intent.putExtra("community_master_id", community_master_id);
                                intent.putExtra("GroupRowResponse", new Gson().toJson(groupRowResponse));
                                startActivity(intent);
                                finish();
                            } else {
                                imgPath = groupRowResponse.imgPath;
                                Glide.with(context).load(imgPath + groupRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(group_banner_iv);
                                Glide.with(context).load(imgPath + groupRow.logo).centerCrop().placeholder(R.drawable.default_business_pic).into(group_logo_iv);

                                group_title_txt.setText(groupRow.name);
                                privacy_tv.setText(CommonFunction.capitalize(groupRow.type));
                                //set icon
                                if (groupRow.type.equalsIgnoreCase("PUBLIC")) {
                                    privacy_img.setImageResource(R.drawable.people_fill_public);
                                } else {
                                    privacy_img.setImageResource(R.drawable.person_fill_lock_private);
                                }
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
}