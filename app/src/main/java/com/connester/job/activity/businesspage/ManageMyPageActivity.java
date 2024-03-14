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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.connester.job.RetrofitConnection.jsontogson.BusinessPageRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.EditProfileActivity;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ManageMyPageActivity extends AppCompatActivity {
    String business_page_id = null;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    MaterialCardView back_cv;
    TabLayout tab_layout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();
    ApiInterface apiInterface;
    ImageView page_banner_iv, page_logo_iv;
    MaterialCardView page_info_edit;
    TextView page_title_txt, tagline_bio_tv, industry_tv, address_tv, founded_yr_tv, followers_tv, no_employee_tv;
    MaterialButton jobs_mbtn, events_mbtn, setting_open_mbtn;
    ScrollView scrollView;
    FrameLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_page);

        if (getIntent() != null) {
            business_page_id = getIntent().getStringExtra("business_page_id");
        } else {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
        if (business_page_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }

        context = ManageMyPageActivity.this;
        activity = ManageMyPageActivity.this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });

        page_banner_iv = findViewById(R.id.page_banner_iv);
        page_logo_iv = findViewById(R.id.page_logo_iv);
        page_info_edit = findViewById(R.id.page_info_edit);
        page_info_edit.setOnClickListener(v -> {
            openPageEditDialog();
        });
        activityResultLauncherForPageBanner = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && d_page_banner_iv != null) {
                pageBannerFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(pageBannerFile).centerCrop().placeholder(R.drawable.user_default_banner).into(d_page_banner_iv);
            }
        });
        activityResultLauncherForPageLogo = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && d_page_logo_iv != null) {
                pageLogoFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(pageLogoFile).centerCrop().placeholder(R.drawable.default_business_pic).into(d_page_logo_iv);
            }
        });

        page_title_txt = findViewById(R.id.page_title_txt);
        tagline_bio_tv = findViewById(R.id.tagline_bio_tv);
        industry_tv = findViewById(R.id.industry_tv);
        address_tv = findViewById(R.id.address_tv);
        founded_yr_tv = findViewById(R.id.founded_yr_tv);
        followers_tv = findViewById(R.id.followers_tv);
        no_employee_tv = findViewById(R.id.no_employee_tv);

        jobs_mbtn = findViewById(R.id.jobs_mbtn);
        jobs_mbtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PageJobMangeActivity.class);
            intent.putExtra("business_page_id", business_page_id);
            startActivity(intent);
        });
        events_mbtn = findViewById(R.id.events_mbtn);
        events_mbtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PageEventMangeActivity.class);
            intent.putExtra("business_page_id", business_page_id);
            startActivity(intent);
        });
        setting_open_mbtn = findViewById(R.id.setting_open_mbtn);
        setting_open_mbtn.setOnClickListener(v -> {
            openPageSettingDialog();
        });

        setData();

        tab_layout = findViewById(R.id.tab_layout);

        fragments.add(new PagePostFragment(scrollView, business_page_id, progressBar));
        fragmentsTitle.add("Posts");
        fragments.add(new PageAnalyticsFragment(business_page_id, progressBar));
        fragmentsTitle.add("Analytics");
        fragments.add(new PageJobApplicationFragment(business_page_id, progressBar));
        fragmentsTitle.add("Job Application");
        fragments.add(new PagePeopleFragment(scrollView, business_page_id, progressBar));
        fragmentsTitle.add("People");

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = fragments.get(tab.getPosition());
                CommonFunction._LoadFirstFragment(ManageMyPageActivity.this, R.id.container, fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        CommonFunction._LoadFirstFragment(ManageMyPageActivity.this, R.id.container, fragments.get(0));
    }


    private void openPageSettingDialog() {
        BottomSheetDialog settingDialog = new BottomSheetDialog(context);
        settingDialog.setContentView(R.layout.common_option_dialog_layout);
        LinearLayout page_close_LL = settingDialog.findViewById(R.id.page_close_LL);
        page_close_LL.setVisibility(View.VISIBLE);

        page_close_LL.setOnClickListener(v -> {

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Close Business page!");
            alertDialog.setMessage("You’ll also lose any recommendations and endorsements you’ve given or received.\n" +
                    "\n" +
                    "So, You are confirm to the close this business page??\n" +
                    "And if click on continue then closed your business page.");
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
                    hashMap.put("business_page_id", business_page_id);
                    apiInterface.BUSINESS_PAGE_CLOSED(hashMap).enqueue(new MyApiCallback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            super.onResponse(call, response);
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                    if (normalCommonResponse.status) {
                                        alertDialog.dismiss();
                                        settingDialog.dismiss();
                                        Intent intent = new Intent(context, PageDisableActivity.class);
                                        intent.putExtra("business_page_id", business_page_id);
                                        intent.putExtra("BusinessPageRowResponse", new Gson().toJson(businessPageRowResponse));
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

    ActivityResultLauncher activityResultLauncherForPageBanner, activityResultLauncherForPageLogo;
    File pageLogoFile, pageBannerFile;
    ImageView d_page_banner_iv, d_page_logo_iv;
    boolean[] selectedSkill;
    List<Integer> skillList = new ArrayList<>();

    private void openPageEditDialog() {
        CommonFunction.PleaseWaitShow(context);
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.page_create_manage_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        MaterialCardView back_cv = dialog.findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        d_page_banner_iv = dialog.findViewById(R.id.page_banner_iv);
        if (businessPageRow != null && businessPageRow.banner != null)
            Glide.with(context).load(imgPath + businessPageRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(d_page_banner_iv);
        MaterialCardView page_banner_edit = dialog.findViewById(R.id.page_banner_edit);
        page_banner_edit.setOnClickListener(v -> {
            activityResultLauncherForPageBanner.launch(("image/*"));
        });
        d_page_logo_iv = dialog.findViewById(R.id.page_logo_iv);
        if (businessPageRow != null && businessPageRow.logo != null)
            Glide.with(context).load(imgPath + businessPageRow.logo).centerCrop().placeholder(R.drawable.default_business_pic).into(d_page_logo_iv);
        MaterialCardView page_logo_edit = dialog.findViewById(R.id.page_logo_edit);
        page_logo_edit.setOnClickListener(v -> {
            activityResultLauncherForPageLogo.launch(("image/*"));
        });

        EditText business_nm_input = dialog.findViewById(R.id.business_nm_input);
        if (businessPageRow != null && businessPageRow.busName != null)
            business_nm_input.setText(businessPageRow.busName);

        EditText tagline_bio_nm_input = dialog.findViewById(R.id.tagline_bio_nm_input);
        if (businessPageRow != null && businessPageRow.bio != null)
            tagline_bio_nm_input.setText(businessPageRow.bio);

        EditText web_url_input = dialog.findViewById(R.id.web_url_input);
        if (businessPageRow != null && businessPageRow.website != null)
            web_url_input.setText(businessPageRow.website);

        EditText industry_input = dialog.findViewById(R.id.industry_input);
        if (businessPageRow != null && businessPageRow.industry != null)
            industry_input.setText(businessPageRow.industry);

        EditText company_type_input = dialog.findViewById(R.id.company_type_input);
        if (businessPageRow != null && businessPageRow.orgType != null)
            company_type_input.setText(businessPageRow.orgType);

        EditText phone_number_input = dialog.findViewById(R.id.phone_number_input);
        if (businessPageRow != null && businessPageRow.phone != null)
            phone_number_input.setText(businessPageRow.phone);

        EditText founded_yr_input = dialog.findViewById(R.id.founded_yr_input);
        if (businessPageRow != null && businessPageRow.foundedYear != null)
            founded_yr_input.setText(businessPageRow.foundedYear);

        EditText company_size_input = dialog.findViewById(R.id.company_size_input);
        if (businessPageRow != null && businessPageRow.orgSize != null)
            company_size_input.setText(businessPageRow.orgSize);

        EditText location_input = dialog.findViewById(R.id.location_input);
        if (businessPageRow != null && businessPageRow.address != null)
            location_input.setText(businessPageRow.address);

        EditText description_et = dialog.findViewById(R.id.description_et);
        if (businessPageRow != null && businessPageRow.description != null)
            description_et.setText(businessPageRow.description);

        TextView skills_selected = dialog.findViewById(R.id.skills_selected);
        if (businessPageRow != null && businessPageRow.skills != null)
            skills_selected.setText(businessPageRow.skills.replace(",", ", "));

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
                            if (businessPageRow.skills != null)
                                for (String skill : businessPageRow.skills.split(",")) {
                                    int ind = normalCommonResponse.dt.indexOf(skill);
                                    if (ind >= 0) {
                                        selectedSkill[ind] = true;
                                        skillList.add(ind);
                                    }
                                }
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
                            .addFormDataPart("business_page_id", business_page_id)
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
                                        imgFileLogo.delete();
                                        imgFileBanner.delete();

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

    BusinessPageRowResponse.BusinessPageRow businessPageRow;
    BusinessPageRowResponse businessPageRowResponse;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call

    private void setData() {
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("business_page_id", business_page_id);

        apiInterface.BUSINESS_PAGE_ROW(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        businessPageRowResponse = (BusinessPageRowResponse) response.body();
                        if (businessPageRowResponse.status) {
                            businessPageRow = businessPageRowResponse.businessPageRow;
                            //handling redirect for page is not active
                            if (!businessPageRow.pageStatus.equalsIgnoreCase("ACTIVE")) {
                                Intent intent = new Intent(context, PageDisableActivity.class);
                                intent.putExtra("business_page_id", business_page_id);
                                intent.putExtra("BusinessPageRowResponse", new Gson().toJson(businessPageRowResponse));
                                startActivity(intent);
                                finish();
                            } else {
                                imgPath = businessPageRowResponse.imgPath;
                                Glide.with(context).load(imgPath + businessPageRow.banner).centerCrop().placeholder(R.drawable.user_default_banner).into(page_banner_iv);
                                Glide.with(context).load(imgPath + businessPageRow.logo).centerCrop().placeholder(R.drawable.default_business_pic).into(page_logo_iv);

                                page_title_txt.setText(businessPageRow.busName);
                                tagline_bio_tv.setText(businessPageRow.bio);
                                industry_tv.setText(businessPageRow.industry);
                                address_tv.setText(businessPageRow.address);
                                founded_yr_tv.setText(businessPageRow.foundedYear);
                                followers_tv.setText(businessPageRow.members + " followers");
                                no_employee_tv.setText(businessPageRow.orgSize);
                            }
                        } else {
                            businessPageRow = null;
                            Toast.makeText(context, businessPageRowResponse.msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });
    }
}