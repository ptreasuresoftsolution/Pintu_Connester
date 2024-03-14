package com.connester.job.activity.mycommunity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class MyCommunityActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ImageView back_iv;
    ViewPager view_pager;
    TabLayout tab_layout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();
    TextView create_group_tv;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_community);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });

        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        fragments.add(new RecommendedGroupFragment());
        fragmentsTitle.add("Recommended");
        fragments.add(new MyGroupFragment());
        fragmentsTitle.add("My Group");
        fragments.add(new RequestedGroupFragment());
        fragmentsTitle.add("Requested");
        view_pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return fragmentsTitle.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        });

        create_group_tv = findViewById(R.id.create_group_tv);
        create_group_tv.setOnClickListener(v -> {
            createGroupDialogOpen();
        });

        activityResultLauncherForGroupBanner = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && group_banner_iv != null) {
                groupBannerFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(groupBannerFile).centerCrop().placeholder(R.drawable.user_default_banner).into(group_banner_iv);
            }
        });
        activityResultLauncherForGroupLogo = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && group_logo_iv != null) {
                groupLogoFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(groupLogoFile).centerCrop().placeholder(R.drawable.default_business_pic).into(group_logo_iv);
            }
        });
    }

    ActivityResultLauncher activityResultLauncherForGroupBanner, activityResultLauncherForGroupLogo;
    File groupLogoFile, groupBannerFile;
    ImageView group_banner_iv, group_logo_iv;

    private void createGroupDialogOpen() {
        CommonFunction.PleaseWaitShow(context);
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_create_manage_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        MaterialCardView back_cv = dialog.findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        String[] groupType = context.getResources().getStringArray(R.array.group_type);

        group_banner_iv = dialog.findViewById(R.id.group_banner_iv);
        MaterialCardView group_banner_edit = dialog.findViewById(R.id.group_banner_edit);
        group_banner_edit.setOnClickListener(v -> {
            activityResultLauncherForGroupBanner.launch(("image/*"));
        });
        group_logo_iv = dialog.findViewById(R.id.group_logo_iv);
        MaterialCardView group_logo_edit = dialog.findViewById(R.id.group_logo_edit);
        group_logo_edit.setOnClickListener(v -> {
            activityResultLauncherForGroupLogo.launch(("image/*"));
        });

        EditText group_nm_input = dialog.findViewById(R.id.group_nm_input);
        EditText description_et = dialog.findViewById(R.id.description_et);
        EditText industry_input = dialog.findViewById(R.id.industry_input);
        Spinner group_type_sp = dialog.findViewById(R.id.group_type_sp);
        EditText rules_et = dialog.findViewById(R.id.rules_et);

        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.PleaseWaitShow(context);
                CommonFunction.PleaseWaitShowMessage("Files is compressing...");
                try {
                    if (groupLogoFile == null) {
                        Toast.makeText(context, "Please select logo!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (groupBannerFile == null) {
                        Toast.makeText(context, "Please select banner!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File imgFileLogo = new Compressor(context)
                            .setMaxWidth(1080)
                            .setMaxWidth(800)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                            .compressToFile(groupLogoFile);
                    File imgFileBanner = new Compressor(context)
                            .setMaxWidth(1080)
                            .setMaxWidth(800)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                            .compressToFile(groupBannerFile);

                    CommonFunction.PleaseWaitShowMessage("Files is compressed completed");

                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM)
                            .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                            .addFormDataPart("apiKey", "RBqtNuh+0qdrKn+Bb9WafA==")

                            .addFormDataPart("name", group_nm_input.getText().toString())
                            .addFormDataPart("bio", description_et.getText().toString())
                            .addFormDataPart("industry", industry_input.getText().toString())
                            .addFormDataPart("type", Arrays.asList(groupType).get(group_type_sp.getSelectedItemPosition()))
                            .addFormDataPart("rules", rules_et.getText().toString());

                    builder.addFormDataPart("logo", imgFileLogo.getName(),
                            RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFileLogo)), imgFileLogo));

                    builder.addFormDataPart("banner", imgFileBanner.getName(),
                            RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFileBanner)), imgFileBanner));
                    RequestBody body = builder.build();
                    CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                    apiInterface.GROUP_CREATE_MANAGE_CALL(body).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        imgFileBanner.delete();
                                        imgFileLogo.delete();

                                        Intent intent = new Intent(context, ManageMyCommunityActivity.class);
                                        intent.putExtra("community_master_id", String.valueOf(normalCommonResponse.communityMasterId));
                                        startActivity(intent);
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

}