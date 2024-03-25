package com.connester.job.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.EducationItemResponse;
import com.connester.job.RetrofitConnection.jsontogson.EducationListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.ProjectItemResponse;
import com.connester.job.RetrofitConnection.jsontogson.ProjectListResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.WorkExperienceItemResponse;
import com.connester.job.RetrofitConnection.jsontogson.WorkExperienceListResponse;
import com.connester.job.activity.message.ChatHistoryUsersActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.DateUtils;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.connester.job.module.UserMaster;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    UserRowResponse.Dt userDt;
    UserMaster userMaster;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call
    FlexboxLayout user_skill_tag_fbl, user_language_tag_fbl;

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


    TextView userFullName_txt, user_position_tv, user_bio_tv, followers_tv, following_tv, about_me_tv;
    ImageView user_banner_iv, user_pic, about_edit, profile_option_iv, work_experience_add, education_add, project_add, skill_edit, language_edit;
    MaterialCardView user_banner_edit, back_cv, user_pic_edit;
    LinearLayout work_experience_ll, education_ll, project_ll;
    MaterialButton post_activity_mbtn, inbox_open_mbtn, setting_open_mbtn;

    ActivityResultLauncher activityResultLauncherForProfilePic, activityResultLauncherForProfileBanner;

    private void initView() {
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        hashMapDefault.put("device", "ANDROID");

        back_cv = findViewById(R.id.back_cv);
        back_cv.setOnClickListener(v -> {
            onBackPressed();
        });
        user_banner_iv = findViewById(R.id.user_banner_iv);
        activityResultLauncherForProfileBanner = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            CommonFunction.PleaseWaitShow(context);
            CommonFunction.PleaseWaitShowMessage("Files is compressing...");
            try {
                File pFile = new File(FilePath.getPath2(context, photoUri));
                File imgFile = new Compressor(context)
                        .setMaxWidth(1080)
                        .setMaxWidth(800)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                        .compressToFile(pFile);
                Log.e(LogTag.TMP_LOG, "Path :" + imgFile.getAbsolutePath());

                CommonFunction.PleaseWaitShowMessage("Files is compressed completed");
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                        .addFormDataPart("apiKey", "RBqtNuh+0qdrKn+Bb9WafA==")
                        .addFormDataPart("clmNm", "profile_banner")
                        .addFormDataPart("old_pic", userDt.profilePic);

                builder.addFormDataPart("profile_img", imgFile.getName(),
                        RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFile)), imgFile));
                RequestBody body = builder.build();
                CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                apiInterface.EDIT_PROFILE_CHANGE_PROFILE_PIC_BANNER(body).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    imgFile.delete();

                                    setData();
                                }
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                Log.e(LogTag.EXCEPTION, "Image Compress Exception", e);
            }
        });
        user_banner_edit = findViewById(R.id.user_banner_edit);
        user_banner_edit.setOnClickListener(v -> {
            activityResultLauncherForProfileBanner.launch(("image/*"));
        });

        user_pic = findViewById(R.id.user_pic);
        activityResultLauncherForProfilePic = registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            CommonFunction.PleaseWaitShow(context);
            CommonFunction.PleaseWaitShowMessage("Files is compressing...");
            try {
                File pFile = new File(FilePath.getPath2(context, photoUri));
                File imgFile = new Compressor(context)
                        .setMaxWidth(1080)
                        .setMaxWidth(800)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                        .compressToFile(pFile);
                Log.e(LogTag.TMP_LOG, "Path :" + imgFile.getAbsolutePath());

                CommonFunction.PleaseWaitShowMessage("Files is compressed completed");
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                        .addFormDataPart("apiKey", "RBqtNuh+0qdrKn+Bb9WafA==")
                        .addFormDataPart("clmNm", "profile_pic")
                        .addFormDataPart("old_pic", userDt.profilePic);

                builder.addFormDataPart("profile_img", imgFile.getName(),
                        RequestBody.create(MediaType.parse(FilePath.getMimeType(imgFile)), imgFile));
                RequestBody body = builder.build();
                CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                apiInterface.EDIT_PROFILE_CHANGE_PROFILE_PIC_BANNER(body).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    imgFile.delete();

                                    setData();
                                }
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            } catch (Exception e) {
                Log.e(LogTag.EXCEPTION, "Image Compress Exception", e);
            }
        });
        user_pic_edit = findViewById(R.id.user_pic_edit);
        user_pic_edit.setOnClickListener(v -> {
            activityResultLauncherForProfilePic.launch(("image/*"));
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
            startActivity(new Intent(context, ChatHistoryUsersActivity.class));
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
                profileOptionDialog.dismiss();
                Intent intent = new Intent(context, ChatHistoryUsersActivity.class);
                intent.putExtra("action", "pick");
                intent.putExtra("message", Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/profile/" + userDt.profileLink);
                startActivity(intent);
            });
            LinearLayout web_portfolio_option_LL = profileOptionDialog.findViewById(R.id.web_portfolio_option_LL);
            web_portfolio_option_LL.setVisibility(View.VISIBLE);
            web_portfolio_option_LL.setOnClickListener(v1 -> {
                profileOptionDialog.dismiss();
                Intent intent = new Intent(context, ShowPortfolioApplyJobActivity.class);
                intent.putExtra("user_master_id", sessionPref.getUserMasterId());
                startActivity(intent);
            });
            LinearLayout edit_profile_option_LL = profileOptionDialog.findViewById(R.id.edit_profile_option_LL);
            edit_profile_option_LL.setVisibility(View.VISIBLE);
            edit_profile_option_LL.setOnClickListener(v1 -> {
                profileOptionDialog.dismiss();
                openEditInfoDialog();
            });

            profileOptionDialog.show();
        });

        about_me_tv = findViewById(R.id.about_me_tv);
        about_edit = findViewById(R.id.about_edit);
        about_edit.setOnClickListener(v -> {
            openEditAboutDialog();
        });

        work_experience_ll = findViewById(R.id.work_experience_ll);
        work_experience_add = findViewById(R.id.work_experience_add);
        work_experience_add.setOnClickListener(v -> {
            openAddWorkExperienceDialog();
        });

        education_ll = findViewById(R.id.education_ll);
        education_add = findViewById(R.id.education_add);
        education_add.setOnClickListener(v -> {
            openAddEducationDialog();
        });

        project_ll = findViewById(R.id.project_ll);
        project_add = findViewById(R.id.project_add);
        project_add.setOnClickListener(v -> {
            openAddProjectDialog();
        });

        user_skill_tag_fbl = findViewById(R.id.user_skill_tag_fbl);
        skill_edit = findViewById(R.id.skill_edit);
        skill_edit.setOnClickListener(v -> {
            openEditSkillDialog();
        });

        user_language_tag_fbl = findViewById(R.id.user_language_tag_fbl);
        language_edit = findViewById(R.id.language_edit);
        language_edit.setOnClickListener(v -> {
            openEditLanguageDialog();
        });
    }

    boolean[] selectedLanguage;
    List<Integer> languageList = new ArrayList<>();

    private void openEditLanguageDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_language_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView language_selected = dialog.findViewById(R.id.language_selected);
        if (userDt.language != null)
            language_selected.setText(userDt.language.replace(",", ", "));
        CommonFunction.PleaseWaitShow(context);
        apiInterface.GET_LANGUAGE_TBL(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {

                            selectedLanguage = new boolean[normalCommonResponse.dt.size()];
                            String languageDt[] = new String[normalCommonResponse.dt.size()];
                            String dt[] = normalCommonResponse.dt.toArray(languageDt);
                            if (userDt.language != null)
                                for (String language : userDt.language.split(",")) {
                                    int ind = normalCommonResponse.dt.indexOf(language);
                                    if (ind >= 0) {
                                        selectedLanguage[ind] = true;
                                        languageList.add(ind);
                                    }
                                }
                            language_selected.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Select language");
                                    builder.setCancelable(false);

                                    builder.setMultiChoiceItems(dt, selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            if (b) {
                                                languageList.add(i);
                                                Collections.sort(languageList);
                                            } else {
                                                languageList.remove(Integer.valueOf(i));
                                            }
                                        }
                                    });

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            for (int j = 0; j < languageList.size(); j++) {
                                                stringBuilder.append(dt[languageList.get(j)]);
                                                if (j != languageList.size() - 1) {
                                                    stringBuilder.append(", ");
                                                }
                                            }
                                            // set text on textView
                                            language_selected.setText(stringBuilder.toString());
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
                                            Arrays.fill(selectedLanguage, false);
                                            languageList.clear();
                                            language_selected.setText("");
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
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("key", "language");
            hashMap.put("val", language_selected.getText().toString().replace(", ", ","));
            hashMap.put("update", "single");

            apiInterface.EDIT_PROFILE_INFO_OR_CLM_ITEM(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
        dialog.show();
    }

    boolean[] selectedSkill;
    List<Integer> skillList = new ArrayList<>();

    //selection proper not working and default select also proper not working (Both: Skill / language)
    private void openEditSkillDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_skills_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView skills_selected = dialog.findViewById(R.id.skills_selected);
        if (userDt.skill != null)
            skills_selected.setText(userDt.skill.replace(",", ", "));
        CommonFunction.PleaseWaitShow(context);
        apiInterface.GET_SKILL_TBL(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            selectedSkill = new boolean[normalCommonResponse.dt.size()];
                            String skillDt[] = new String[normalCommonResponse.dt.size()];
                            String dt[] = normalCommonResponse.dt.toArray(skillDt);
                            if (userDt.skill != null)
                                for (String skill : userDt.skill.split(",")) {
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
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("key", "skill");
            hashMap.put("val", skills_selected.getText().toString().replace(", ", ","));
            hashMap.put("update", "single");

            apiInterface.EDIT_PROFILE_INFO_OR_CLM_ITEM(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
        dialog.show();
    }

    Calendar startDateCalendar, endDateCalendar;

    private void openAddProjectDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_project_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView title = dialog.findViewById(R.id.title);
        title.setText("Add a project");
        EditText project_nm_et = dialog.findViewById(R.id.project_nm_et);
        EditText company_et = dialog.findViewById(R.id.company_et);

        EditText start_date_et = dialog.findViewById(R.id.start_date_et);
        startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        start_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(startDateCalendar);
                start_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText end_date_et = dialog.findViewById(R.id.end_date_et);
        endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        end_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(endDateCalendar);
                end_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText description_et = dialog.findViewById(R.id.description_et);

        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setVisibility(View.GONE);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("project_name", project_nm_et.getText().toString());
            hashMap.put("company_name", company_et.getText().toString());
            hashMap.put("start_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", start_date_et.getText().toString()));
            hashMap.put("end_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", end_date_et.getText().toString()));
            hashMap.put("project_desc", description_et.getText().toString());
            apiInterface.PROJECT_ITEM_ADD_EDIT(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        dialog.show();
    }

    private void editProjectDialog(String userProjectId) {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_project_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView title = dialog.findViewById(R.id.title);
        title.setText("Edit a project");
        EditText project_nm_et = dialog.findViewById(R.id.project_nm_et);
        EditText company_et = dialog.findViewById(R.id.company_et);

        EditText start_date_et = dialog.findViewById(R.id.start_date_et);
        startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        start_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(startDateCalendar);
                start_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText end_date_et = dialog.findViewById(R.id.end_date_et);
        endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        end_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(endDateCalendar);
                end_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText description_et = dialog.findViewById(R.id.description_et);

        //set project data
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("user_project_id", userProjectId);
        hashMap.put("device", "ANDROID");
        apiInterface.GET_PROJECT_ITEM(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ProjectItemResponse projectItemResponse = (ProjectItemResponse) response.body();
                        if (projectItemResponse.status) {
                            if (projectItemResponse.dt.projectName != null)
                                project_nm_et.setText(projectItemResponse.dt.projectName);

                            if (projectItemResponse.dt.companyName != null)
                                company_et.setText(projectItemResponse.dt.companyName);

                            if (projectItemResponse.dt.startDate != null) {
                                start_date_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy", projectItemResponse.dt.startDate));
                                startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", projectItemResponse.dt.startDate));
                            }
                            if (projectItemResponse.dt.endDate != null) {
                                end_date_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy", projectItemResponse.dt.endDate));
                                endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", projectItemResponse.dt.endDate));
                            }

                            if (projectItemResponse.dt.projectDesc != null)
                                description_et.setText(projectItemResponse.dt.projectDesc);
                        } else {
                            Toast.makeText(context, projectItemResponse.msg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }
            }
        });

        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMapRemove = new HashMap();
            hashMapRemove.put("user_master_id", sessionPref.getUserMasterId());
            hashMapRemove.put("apiKey", sessionPref.getApiKey());
            hashMapRemove.put("user_project_id", userProjectId);

            apiInterface.PROJECT_ITEM_REMOVE(hashMapRemove).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMapEdit = new HashMap();
            hashMapEdit.put("user_master_id", sessionPref.getUserMasterId());
            hashMapEdit.put("apiKey", sessionPref.getApiKey());
            hashMapEdit.put("project_name", project_nm_et.getText().toString());
            hashMapEdit.put("company_name", company_et.getText().toString());
            hashMapEdit.put("start_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", start_date_et.getText().toString()));
            hashMapEdit.put("end_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", end_date_et.getText().toString()));
            hashMapEdit.put("project_desc", description_et.getText().toString());
            hashMapEdit.put("user_project_id", userProjectId);

            apiInterface.PROJECT_ITEM_ADD_EDIT(hashMapEdit).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        dialog.show();
    }

    private void openAddEducationDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_education_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Add a education");

        EditText degree_input = dialog.findViewById(R.id.degree_input);
        EditText university_input = dialog.findViewById(R.id.university_input);
        EditText most_subject_input = dialog.findViewById(R.id.most_subject_input);

        EditText start_date_et = dialog.findViewById(R.id.start_date_et);
        startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        start_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(startDateCalendar);
                start_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText end_date_et = dialog.findViewById(R.id.end_date_et);
        endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        end_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(endDateCalendar);
                end_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText description_et = dialog.findViewById(R.id.description_et);

        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setVisibility(View.GONE);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("degree", degree_input.getText().toString());
            hashMap.put("school_institute_uni", university_input.getText().toString());
            hashMap.put("study_field", most_subject_input.getText().toString());
            hashMap.put("start_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", start_date_et.getText().toString()));
            hashMap.put("end_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", end_date_et.getText().toString()));
            hashMap.put("edu_desc", description_et.getText().toString());
            apiInterface.EDUCATION_ITEM_ADD_EDIT(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        dialog.show();
    }

    private void editEducationDialog(String userEducationId) {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_education_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Edit a education");

        EditText degree_input = dialog.findViewById(R.id.degree_input);
        EditText university_input = dialog.findViewById(R.id.university_input);
        EditText most_subject_input = dialog.findViewById(R.id.most_subject_input);

        EditText start_date_et = dialog.findViewById(R.id.start_date_et);
        startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        start_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(startDateCalendar);
                start_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText end_date_et = dialog.findViewById(R.id.end_date_et);
        endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        end_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(endDateCalendar);
                end_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText description_et = dialog.findViewById(R.id.description_et);

        //set education data
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMapSelect = new HashMap();
        hashMapSelect.put("user_master_id", sessionPref.getUserMasterId());
        hashMapSelect.put("apiKey", sessionPref.getApiKey());
        hashMapSelect.put("user_education_id", userEducationId);
        hashMapSelect.put("device", "ANDROID");
        apiInterface.GET_EDUCATION_ITEM(hashMapSelect).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        EducationItemResponse educationItemResponse = (EducationItemResponse) response.body();
                        if (educationItemResponse.status) {
                            if (educationItemResponse.dt.degree != null)
                                degree_input.setText(educationItemResponse.dt.degree);

                            if (educationItemResponse.dt.schoolInstituteUni != null)
                                university_input.setText(educationItemResponse.dt.schoolInstituteUni);

                            if (educationItemResponse.dt.studyField != null)
                                most_subject_input.setText(educationItemResponse.dt.studyField);

                            if (educationItemResponse.dt.startDate != null) {
                                start_date_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy", educationItemResponse.dt.startDate));
                                startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", educationItemResponse.dt.startDate));
                            }
                            if (educationItemResponse.dt.endDate != null) {
                                end_date_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy", educationItemResponse.dt.endDate));
                                endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", educationItemResponse.dt.endDate));
                            }

                            if (educationItemResponse.dt.eduDesc != null)
                                description_et.setText(educationItemResponse.dt.eduDesc);
                        } else {
                            Toast.makeText(context, educationItemResponse.msg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }
            }
        });

        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMapRemove = new HashMap();
            hashMapRemove.put("user_master_id", sessionPref.getUserMasterId());
            hashMapRemove.put("apiKey", sessionPref.getApiKey());
            hashMapRemove.put("user_education_id", userEducationId);

            apiInterface.EDUCATION_ITEM_REMOVE(hashMapRemove).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("degree", degree_input.getText().toString());
            hashMap.put("school_institute_uni", university_input.getText().toString());
            hashMap.put("study_field", most_subject_input.getText().toString());
            hashMap.put("start_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", start_date_et.getText().toString()));
            hashMap.put("end_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", end_date_et.getText().toString()));
            hashMap.put("edu_desc", description_et.getText().toString());
            hashMap.put("user_education_id", userEducationId);
            apiInterface.EDUCATION_ITEM_ADD_EDIT(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        dialog.show();
    }

    private void openAddWorkExperienceDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_work_experiences_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Add a work experience");

        EditText job_title = dialog.findViewById(R.id.job_title);
        EditText company_et = dialog.findViewById(R.id.company_et);

        EditText start_date_et = dialog.findViewById(R.id.start_date_et);
        startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        start_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(startDateCalendar);
                start_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText end_date_et = dialog.findViewById(R.id.end_date_et);
        endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        end_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(endDateCalendar);
                end_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        CheckBox is_current_company = dialog.findViewById(R.id.is_current_company);

        EditText description_et = dialog.findViewById(R.id.description_et);

        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setVisibility(View.GONE);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("job_title", job_title.getText().toString());
            hashMap.put("job_company", company_et.getText().toString());
            hashMap.put("start_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", start_date_et.getText().toString()));
            hashMap.put("end_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", end_date_et.getText().toString()));
            hashMap.put("is_current_company", is_current_company.isChecked() ? "1" : "0");
            hashMap.put("job_desc", description_et.getText().toString());
            apiInterface.WORK_EXPERIENCE_ITEM_ADD_EDIT(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        dialog.show();
    }

    private void editWorkExperienceDialog(String userExperienceId) {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_work_experiences_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Edit a work experience");

        EditText job_title = dialog.findViewById(R.id.job_title);
        EditText company_et = dialog.findViewById(R.id.company_et);

        EditText start_date_et = dialog.findViewById(R.id.start_date_et);
        startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        start_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(startDateCalendar);
                start_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        EditText end_date_et = dialog.findViewById(R.id.end_date_et);
        endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        end_date_et.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(endDateCalendar);
                end_date_et.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH), endDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        CheckBox is_current_company = dialog.findViewById(R.id.is_current_company);

        EditText description_et = dialog.findViewById(R.id.description_et);
        //set work experience data
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMapSelect = new HashMap();
        hashMapSelect.put("user_master_id", sessionPref.getUserMasterId());
        hashMapSelect.put("apiKey", sessionPref.getApiKey());
        hashMapSelect.put("user_experience_id", userExperienceId);
        hashMapSelect.put("device", "ANDROID");
        apiInterface.GET_WORK_EXPERIENCE_ITEM(hashMapSelect).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WorkExperienceItemResponse workExperienceItemResponse = (WorkExperienceItemResponse) response.body();
                        if (workExperienceItemResponse.status) {
                            if (workExperienceItemResponse.dt.jobTitle != null)
                                job_title.setText(workExperienceItemResponse.dt.jobTitle);

                            if (workExperienceItemResponse.dt.jobCompany != null)
                                company_et.setText(workExperienceItemResponse.dt.jobCompany);

                            if (workExperienceItemResponse.dt.startDate != null) {
                                start_date_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy", workExperienceItemResponse.dt.startDate));
                                startDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", workExperienceItemResponse.dt.startDate));
                            }
                            if (workExperienceItemResponse.dt.endDate != null) {
                                end_date_et.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy", workExperienceItemResponse.dt.endDate));
                                endDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", workExperienceItemResponse.dt.endDate));
                            }

                            if (workExperienceItemResponse.dt.isCurrentCompany != null && workExperienceItemResponse.dt.isCurrentCompany.equals("1"))
                                is_current_company.setChecked(true);

                            if (workExperienceItemResponse.dt.jobDesc != null)
                                description_et.setText(workExperienceItemResponse.dt.jobDesc);
                        } else {
                            Toast.makeText(context, workExperienceItemResponse.msg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }
            }
        });

        MaterialButton remove_mbtn = dialog.findViewById(R.id.remove_mbtn);
        remove_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMapRemove = new HashMap();
            hashMapRemove.put("user_master_id", sessionPref.getUserMasterId());
            hashMapRemove.put("apiKey", sessionPref.getApiKey());
            hashMapRemove.put("user_experience_id", userExperienceId);

            apiInterface.WORK_EXPERIENCE_ITEM_REMOVE(hashMapRemove).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("job_title", job_title.getText().toString());
            hashMap.put("job_company", company_et.getText().toString());
            hashMap.put("start_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", start_date_et.getText().toString()));
            hashMap.put("end_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", end_date_et.getText().toString()));
            hashMap.put("is_current_company", is_current_company.isChecked() ? "1" : "0");
            hashMap.put("job_desc", description_et.getText().toString());
            hashMap.put("user_experience_id", userExperienceId);
            apiInterface.WORK_EXPERIENCE_ITEM_ADD_EDIT(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        dialog.show();
    }

    private void openEditAboutDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_about_me_dialog);
        setDialogFullScreenSetting(dialog);

        EditText edit_bio_et = dialog.findViewById(R.id.edit_bio_et);
        if (userDt.bio != null)
            edit_bio_et.setText(userDt.bio);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("key", "bio");
            hashMap.put("val", edit_bio_et.getText().toString());
            hashMap.put("update", "single");
            apiInterface.EDIT_PROFILE_INFO_OR_CLM_ITEM(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
        dialog.show();
    }

    String gender = "Male";
    Calendar birthDateCalendar;

    private void openEditInfoDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_info_dialog);
        setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        EditText fullname_input = dialog.findViewById(R.id.fullname_input);
        if (userDt.name != null)
            fullname_input.setText(userDt.name);
        EditText phone_number_input = dialog.findViewById(R.id.phone_number_input);
        if (userDt.mainPhone != null)
            phone_number_input.setText(userDt.mainPhone);

        EditText date_birth_input = dialog.findViewById(R.id.date_birth_input);
        date_birth_input.setText(DateUtils.getStringDate("yyyy-MM-dd", "dd-MMM-yyyy", DateUtils.TODAYDATEforDB()));
        birthDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", DateUtils.TODAYDATEforDB()));
        if (userDt.birthDate != null) {
            date_birth_input.setText(DateUtils.getStringDate("yyyy-MM-dd", "dd-MMM-yyyy", userDt.birthDate));
            birthDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", userDt.birthDate));
        }
        date_birth_input.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                birthDateCalendar.set(Calendar.YEAR, year);
                birthDateCalendar.set(Calendar.MONTH, month);
                birthDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(birthDateCalendar);
                date_birth_input.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, birthDateCalendar.get(Calendar.YEAR), birthDateCalendar.get(Calendar.MONTH), birthDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        RadioButton male_rb = dialog.findViewById(R.id.male_rb);
        male_rb.setChecked(true);
        gender = male_rb.getText().toString();
        RadioButton female_rb = dialog.findViewById(R.id.female_rb);
        if (userDt.gender != null && userDt.gender.equalsIgnoreCase("female")) {
            female_rb.setChecked(true);
            gender = female_rb.getText().toString();
        }
        RadioGroup radio_group_gender = dialog.findViewById(R.id.radio_group_gender);
        radio_group_gender.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            gender = radioButton.getText().toString();
        });

        EditText city_input = dialog.findViewById(R.id.city_input);
        if (userDt.city != null)
            city_input.setText(userDt.city);
        EditText country_region_input = dialog.findViewById(R.id.country_region_input);
        if (userDt.countryRegion != null)
            country_region_input.setText(userDt.countryRegion);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("name", fullname_input.getText().toString());
            hashMap.put("birth_date", DateUtils.getStringDate("dd-MMM-yyyy", "yyyy-MM-dd", date_birth_input.getText().toString()));
            hashMap.put("main_phone", phone_number_input.getText().toString());
            hashMap.put("gender", gender);
            hashMap.put("city", city_input.getText().toString());
            hashMap.put("country_region", country_region_input.getText().toString());
            hashMap.put("update", "ac-setting");
            apiInterface.EDIT_PROFILE_INFO_OR_CLM_ITEM(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                setData();
                                dialog.dismiss();
                            }
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });

        dialog.show();
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
                    Glide.with(context).load(imgPath + userDt.profileBanner).centerCrop().placeholder(R.drawable.user_default_banner).into(user_banner_iv);
                    userFullName_txt.setText(userDt.name);
                    user_position_tv.setText(userDt.position);
                    user_bio_tv.setText(userDt.bio);
                    followers_tv.setText(userDt.followerIds + " followers");
                    about_me_tv.setText(userDt.bio);

                    loadWorkExperience();
                    loadEducation();
                    loadProjects();

                    //skills set
                    user_skill_tag_fbl.removeAllViews();
                    for (String skill : userDt.skill.split(",")) {
                        View skillItem = getLayoutInflater().inflate(R.layout.skill_tag_item, null);
                        TextView skill_item = skillItem.findViewById(R.id.skill_item);
                        skill_item.setText(skill);
                        user_skill_tag_fbl.addView(skillItem);
                    }

                    //language set
                    user_language_tag_fbl.removeAllViews();
                    for (String language : userDt.language.split(",")) {
                        View languageItem = getLayoutInflater().inflate(R.layout.skill_tag_item, null);
                        TextView language_item = languageItem.findViewById(R.id.skill_item);
                        language_item.setText(language);
                        user_language_tag_fbl.addView(languageItem);
                    }
                }
            }
        }, "*", true);
    }

    HashMap hashMapDefault = new HashMap<>();

    private void loadProjects() {
        CommonFunction.PleaseWaitShow(context);
        apiInterface.PROJECT_LIST(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ProjectListResponse projectListResponse = (ProjectListResponse) response.body();
                        if (projectListResponse.status) {
                            project_ll.removeAllViews();
                            for (ProjectListResponse.Dt dt : projectListResponse.dt) {
                                View profile_project_ed_work_item = getLayoutInflater().inflate(R.layout.profile_project_ed_work_item, null);
                                ImageView item_edit = profile_project_ed_work_item.findViewById(R.id.item_edit);
                                item_edit.setOnClickListener(v -> {
                                    editProjectDialog(dt.userProjectId);
                                });
                                TextView item_title = profile_project_ed_work_item.findViewById(R.id.item_title);
                                item_title.setText(dt.projectName);
                                TextView item_subtitle = profile_project_ed_work_item.findViewById(R.id.item_subtitle);
                                item_subtitle.setText(dt.companyName);
                                TextView item_duration = profile_project_ed_work_item.findViewById(R.id.item_duration);
                                String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.startDate);
                                String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.endDate);
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate).replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "-" + gap);
                                project_ll.addView(profile_project_ed_work_item);
                            }
                        }
                    }
                }
            }
        });
    }


    private void loadEducation() {
        CommonFunction.PleaseWaitShow(context);
        apiInterface.EDUCATION_LIST(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        EducationListResponse educationListResponse = (EducationListResponse) response.body();
                        if (educationListResponse.status) {
                            education_ll.removeAllViews();
                            for (EducationListResponse.Dt dt : educationListResponse.dt) {
                                View profile_project_ed_work_item = getLayoutInflater().inflate(R.layout.profile_project_ed_work_item, null);
                                ImageView item_edit = profile_project_ed_work_item.findViewById(R.id.item_edit);
                                item_edit.setOnClickListener(v -> {
                                    editEducationDialog(dt.userEducationId);
                                });
                                TextView item_title = profile_project_ed_work_item.findViewById(R.id.item_title);
                                item_title.setText(dt.degree);
                                TextView item_subtitle = profile_project_ed_work_item.findViewById(R.id.item_subtitle);
                                item_subtitle.setText(dt.schoolInstituteUni);
                                TextView item_duration = profile_project_ed_work_item.findViewById(R.id.item_duration);
                                String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.startDate);
                                String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.endDate);
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate).replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "-" + gap);
                                education_ll.addView(profile_project_ed_work_item);
                            }
                        }
                    }
                }
            }
        });

    }

    private void loadWorkExperience() {
        CommonFunction.PleaseWaitShow(context);
        apiInterface.WORK_EXPERIENCE_LIST(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        WorkExperienceListResponse workExperienceListResponse = (WorkExperienceListResponse) response.body();
                        if (workExperienceListResponse.status) {
                            work_experience_ll.removeAllViews();
                            for (WorkExperienceListResponse.Dt dt : workExperienceListResponse.dt) {
                                View profile_project_ed_work_item = getLayoutInflater().inflate(R.layout.profile_project_ed_work_item, null);
                                ImageView item_edit = profile_project_ed_work_item.findViewById(R.id.item_edit);
                                item_edit.setOnClickListener(v -> {
                                    editWorkExperienceDialog(dt.userExperienceId);
                                });
                                TextView item_title = profile_project_ed_work_item.findViewById(R.id.item_title);
                                item_title.setText(dt.jobTitle);
                                TextView item_subtitle = profile_project_ed_work_item.findViewById(R.id.item_subtitle);
                                item_subtitle.setText(dt.jobCompany);
                                TextView item_duration = profile_project_ed_work_item.findViewById(R.id.item_duration);
                                String startTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.startDate);
                                String endTime = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy", dt.endDate);
                                String gap = FeedsMaster.feedTimeCount(dt.startDate, dt.endDate).replace("ago", "");
                                item_duration.setText(startTime + " - " + endTime + "-" + gap);
                                work_experience_ll.addView(profile_project_ed_work_item);
                            }
                        }
                    }
                }
            }
        });
    }


    public static void setDialogFullScreenSetting(Dialog dialog) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}