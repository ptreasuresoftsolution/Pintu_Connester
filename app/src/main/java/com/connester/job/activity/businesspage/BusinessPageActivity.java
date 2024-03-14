package com.connester.job.activity.businesspage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class BusinessPageActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ImageView back_iv;
    ViewPager view_pager;
    TabLayout tab_layout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();
    TextView create_page_tv;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_page);
        context = BusinessPageActivity.this;
        activity = BusinessPageActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });

        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        fragments.add(new RecommendedPagesFragment());
        fragmentsTitle.add("Recommended");
        fragments.add(new MyPagesFragment());
        fragmentsTitle.add("My Pages");
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

        create_page_tv = findViewById(R.id.create_page_tv);
        create_page_tv.setOnClickListener(v -> {
            createBusinessPageDialogOpen();
        });

        activityResultLauncherForPageBanner = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && page_banner_iv != null) {
                pageBannerFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(pageBannerFile).centerCrop().placeholder(R.drawable.user_default_banner).into(page_banner_iv);
            }
        });
        activityResultLauncherForPageLogo = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && page_logo_iv != null) {
                pageLogoFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(pageLogoFile).centerCrop().placeholder(R.drawable.default_business_pic).into(page_logo_iv);
            }
        });
    }

    ActivityResultLauncher activityResultLauncherForPageBanner, activityResultLauncherForPageLogo;
    File pageLogoFile, pageBannerFile;
    ImageView page_banner_iv, page_logo_iv;

    private void createBusinessPageDialogOpen() {
        CommonFunction.PleaseWaitShow(context);
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.page_create_manage_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        MaterialCardView back_cv = dialog.findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        page_banner_iv = dialog.findViewById(R.id.page_banner_iv);
        MaterialCardView page_banner_edit = dialog.findViewById(R.id.page_banner_edit);
        page_banner_edit.setOnClickListener(v -> {
            activityResultLauncherForPageBanner.launch(("image/*"));
        });
        page_logo_iv = dialog.findViewById(R.id.page_logo_iv);
        MaterialCardView page_logo_edit = dialog.findViewById(R.id.page_logo_edit);
        page_logo_edit.setOnClickListener(v -> {
            activityResultLauncherForPageLogo.launch(("image/*"));
        });

        EditText business_nm_input = dialog.findViewById(R.id.business_nm_input);
        EditText tagline_bio_nm_input = dialog.findViewById(R.id.tagline_bio_nm_input);
        EditText web_url_input = dialog.findViewById(R.id.web_url_input);
        EditText industry_input = dialog.findViewById(R.id.industry_input);
        EditText company_type_input = dialog.findViewById(R.id.company_type_input);
        EditText phone_number_input = dialog.findViewById(R.id.phone_number_input);
        EditText founded_yr_input = dialog.findViewById(R.id.founded_yr_input);
        EditText company_size_input = dialog.findViewById(R.id.company_size_input);
        EditText location_input = dialog.findViewById(R.id.location_input);
        EditText description_et = dialog.findViewById(R.id.description_et);

        TextView skills_selected = dialog.findViewById(R.id.skills_selected);
        HashMap hashMapDefault = new HashMap();
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        hashMapDefault.put("device", "ANDROID");
        apiInterface.GET_SKILL_TBL(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            selectedSkill = new boolean[normalCommonResponse.dt.size()];
                            String dt[] = normalCommonResponse.dt.toArray(new String[normalCommonResponse.dt.size()]);
                            skills_selected.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Select Skills");
                                    builder.setCancelable(false);

                                    builder.setMultiChoiceItems(dt, selectedSkill, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            if (b) {
                                                skillList.add(i);
                                                Collections.sort(skillList);
                                            } else {
                                                skillList.remove(Integer.valueOf(i));
                                            }
                                        }
                                    });

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            for (int j = 0; j < skillList.size(); j++) {
                                                stringBuilder.append(dt[skillList.get(j)]);
                                                if (j != skillList.size() - 1) {
                                                    stringBuilder.append(", ");
                                                }
                                            }
                                            // set text on textView
                                            skills_selected.setText(stringBuilder.toString());
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // dismiss dialog
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // use for loop
                                            Arrays.fill(selectedSkill, false);
                                            skillList.clear();
                                            skills_selected.setText("");
                                        }
                                    });
                                    // show dialog
                                    builder.show();
                                }
                            });
                        }
                    }
                }
            }
        });

        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonFunction.PleaseWaitShow(context);
                CommonFunction.PleaseWaitShowMessage("Files is compressing...");
                try {
                    if (pageLogoFile == null) {
                        Toast.makeText(context, "Please select logo!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pageBannerFile == null) {
                        Toast.makeText(context, "Please select banner!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File imgFileLogo = new Compressor(context)
                            .setMaxWidth(1080)
                            .setMaxWidth(800)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                            .compressToFile(pageLogoFile);
                    File imgFileBanner = new Compressor(context)
                            .setMaxWidth(1080)
                            .setMaxWidth(800)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                            .compressToFile(pageBannerFile);

                    CommonFunction.PleaseWaitShowMessage("Files is compressed completed");

                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM)
                            .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                            .addFormDataPart("apiKey", "RBqtNuh+0qdrKn+Bb9WafA==")
                            .addFormDataPart("bus_name", business_nm_input.getText().toString())
                            .addFormDataPart("website", web_url_input.getText().toString())
                            .addFormDataPart("industry", industry_input.getText().toString())
                            .addFormDataPart("org_size", company_size_input.getText().toString())
                            .addFormDataPart("org_type", company_type_input.getText().toString())
                            .addFormDataPart("bio", tagline_bio_nm_input.getText().toString())
                            .addFormDataPart("founded_year", founded_yr_input.getText().toString())
                            .addFormDataPart("phone", phone_number_input.getText().toString())
                            .addFormDataPart("address", location_input.getText().toString())
                            .addFormDataPart("description", description_et.getText().toString())
                            .addFormDataPart("skills", skills_selected.getText().toString());

                    builder.addFormDataPart("logo", imgFileLogo.getName(),
                            RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFileLogo)), imgFileLogo));

                    builder.addFormDataPart("banner", imgFileBanner.getName(),
                            RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFileBanner)), imgFileBanner));
                    RequestBody body = builder.build();
                    CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                    apiInterface.PAGE_CREATE_MANAGE_CALL(body).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        imgFileBanner.delete();
                                        imgFileLogo.delete();

                                        Intent intent = new Intent(context, ManageMyPageActivity.class);
                                        intent.putExtra("business_page_id", String.valueOf(normalCommonResponse.businessPageId));
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

    boolean[] selectedSkill;
    List<Integer> skillList = new ArrayList<>();
}