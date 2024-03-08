package com.connester.job.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.EducationListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.ProjectListResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.WorkExperienceListResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.DateUtils;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.connester.job.module.UserMaster;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    private void initView() {
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        hashMapDefault.put("device", "ANDROID");

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

    private void openEditLanguageDialog() {
    }

    boolean[] selectedSkill;
    List<Integer> skillList = new ArrayList<>();

    private void openEditSkillDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_skills_dialog);
        setFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView skills_selected = dialog.findViewById(R.id.skills_selected);
        skills_selected.setText(userDt.skill);
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
                            for (String skill : userDt.skill.split(",")) {
                                int ind = normalCommonResponse.dt.indexOf(skill);
                                if (ind>0){
                                    selectedSkill[ind-1] = true;
                                }
                            }
                            skills_selected.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Initialize alert dialog
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    // set title
                                    builder.setTitle("Select Skills");
                                    // set dialog non cancelable
                                    builder.setCancelable(false);

                                    builder.setMultiChoiceItems(dt, selectedSkill, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            // check condition
                                            if (b) {
                                                // when checkbox selected
                                                // Add position  in lang list
                                                skillList.add(i);
                                                // Sort array list
                                                Collections.sort(skillList);
                                            } else {
                                                // when checkbox unselected
                                                // Remove position from langList
                                                skillList.remove(Integer.valueOf(i));
                                            }
                                        }
                                    });

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Initialize string builder
                                            StringBuilder stringBuilder = new StringBuilder();
                                            // use for loop
                                            for (int j = 0; j < skillList.size(); j++) {
                                                // concat array value
                                                stringBuilder.append(dt[skillList.get(j)]);
                                                // check condition
                                                if (j != skillList.size() - 1) {
                                                    // When j value  not equal
                                                    // to lang list size - 1
                                                    // add comma
                                                    stringBuilder.append(",");
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
                                            for (int j = 0; j < selectedSkill.length; j++) {
                                                // remove all selection
                                                selectedSkill[j] = false;
                                            }
                                            // clear language list
                                            skillList.clear();
                                            // clear text view value
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
        dialog.show();
    }

    private void openAddProjectDialog() {
    }

    private void editProjectDialog(String userProjectId) {

    }

    private void openAddEducationDialog() {
    }

    private void editEducationDialog(String userEducationId) {
    }

    private void openAddWorkExperienceDialog() {
    }

    private void editWorkExperienceDialog(String userExperienceId) {
    }

    private void openEditAboutDialog() {
    }

    private void openEditInfoDialog() {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editprofile_info_dialog);
        setFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });

        EditText fullname_input = dialog.findViewById(R.id.fullname_input);
        EditText phone_number_input = dialog.findViewById(R.id.phone_number_input);
        EditText date_birth_input = dialog.findViewById(R.id.date_birth_input);
        date_birth_input.setText(DateUtils.getStringDate("yyyy-MM-dd", "dd-MMM-yyyy", userDt.birthDate));
        Calendar birthDateCalendar = DateUtils.toCalendar(DateUtils.getObjectDate("yyyy-MM-dd", userDt.birthDate));
        date_birth_input.setOnClickListener(v -> {
            new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                birthDateCalendar.set(Calendar.YEAR, year);
                birthDateCalendar.set(Calendar.MONTH, month);
                birthDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date birthDate = DateUtils.toDate(birthDateCalendar);
                date_birth_input.setText(DateUtils.getStringDate("dd-MMM-yyyy", birthDate));
            }, birthDateCalendar.get(Calendar.YEAR), birthDateCalendar.get(Calendar.MONTH), birthDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        RadioGroup radio_group_gender = dialog.findViewById(R.id.radio_group_gender);
        radio_group_gender.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            Toast.makeText(context, radioButton.getText().toString(), Toast.LENGTH_LONG).show();
        });
        EditText city_input = dialog.findViewById(R.id.city_input);
        EditText country_region_input = dialog.findViewById(R.id.country_region_input);
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);


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


    private void setFullScreenSetting(Dialog dialog) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}