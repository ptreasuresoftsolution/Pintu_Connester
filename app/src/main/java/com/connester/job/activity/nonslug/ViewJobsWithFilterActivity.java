package com.connester.job.activity.nonslug;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.connester.job.module.notification_message.TypingOnlineListener;
import com.connester.job.plugins.multipleselectedspinner.KeyPairBoolData;
import com.connester.job.plugins.multipleselectedspinner.MultiSpinnerListener;
import com.connester.job.plugins.multipleselectedspinner.MultiSpinnerSearch;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ViewJobsWithFilterActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    FrameLayout progressBar;
    ApiInterface apiInterface;
    LinearLayout main_ll;
    FeedsMaster feedsMaster;
    ScrollView scrollView;
    SwipeRefreshLayout swipe_refresh;
    EditText title_post_et, job_location_et;
    ImageView back_iv;
    MaterialCardView filter_open_cv;
    UserRowResponse.Dt dt;
    HashMap filterHashMap = new HashMap<>();
    TypingOnlineListener typingOnlineListener_job_location, typingOnlineListener_title_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs_with_filter);

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        dt = sessionPref.getUserMasterRowInObject();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        filterHashMap.put("title_post", "");
        filterHashMap.put("job_location", "");
        filterHashMap.put("job_type", "");
        filterHashMap.put("requirements", "");
        filterHashMap.put("skills", "");


        scrollView = findViewById(R.id.scrollView);
        main_ll = findViewById(R.id.main_ll);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        progressBar = findViewById(R.id.progressBar);
        swipe_refresh = findViewById(R.id.swipe_refresh);

        title_post_et = findViewById(R.id.title_post_et);
        if (dt.position != null && !dt.position.isEmpty()) {
            title_post_et.setText(dt.position);
            filterHashMap.put("title_post", dt.position);
        }
        title_post_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (typingOnlineListener_title_post != null)
                    typingOnlineListener_title_post.typing();
            }
        });

        job_location_et = findViewById(R.id.job_location_et);
        if (dt.city != null && !dt.city.isEmpty()) {
            job_location_et.setText(dt.city);
            filterHashMap.put("job_location", dt.city);
        }
        job_location_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (typingOnlineListener_job_location != null)
                    typingOnlineListener_job_location.typing();
            }
        });

        typingOnlineListener_job_location = new TypingOnlineListener() {
            @Override
            public void online() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!job_location_et.getText().toString().isEmpty()) {
                            filterHashMap.put("job_location", job_location_et.getText().toString());
                            if (feedsMaster != null)
                                feedsMaster.callSuggestedJobsEventsFeeds(main_ll, scrollView, filterHashMap);
                        }
                    }
                });
            }

            @Override
            public void typing() {
                super.typing();
            }
        };
        typingOnlineListener_title_post = new TypingOnlineListener() {
            @Override
            public void online() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!title_post_et.getText().toString().isEmpty()) {
                            filterHashMap.put("title_post", title_post_et.getText().toString());
                            if (feedsMaster != null)
                                feedsMaster.callSuggestedJobsEventsFeeds(main_ll, scrollView, filterHashMap);
                        }
                    }
                });
            }

            @Override
            public void typing() {
                super.typing();
            }
        };

        feedsMaster = new FeedsMaster(context, activity, ViewJobsWithFilterActivity.this);
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.callSuggestedJobsEventsFeeds(main_ll, scrollView, filterHashMap);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                feedsMaster.setMainLinearLayoutChange(new FeedsMaster.MainLinearLayoutChange() {
                    @Override
                    public void itemAddEditChange(LinearLayout linearLayout) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
                feedsMaster.callSuggestedJobsEventsFeeds(main_ll, scrollView, filterHashMap);
            }
        });

        filter_open_cv = findViewById(R.id.filter_open_cv);
        filter_open_cv.setOnClickListener(v -> {
            if (dialog != null) {
                job_position_et.setText(title_post_et.getText().toString());
                location_et.setText(job_location_et.getText().toString());
                dialog.show();
            }
        });
        setFilterDialog();
    }

    Dialog dialog;
    private List<String> fullSkills = null;
    EditText job_position_et, location_et;

    private void setFilterDialog() {
        dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.job_filter_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        String[] jobType = context.getResources().getStringArray(R.array.job_type);

        job_position_et = dialog.findViewById(R.id.job_position_et);
        Spinner job_type_sp = dialog.findViewById(R.id.job_type_sp);
        location_et = dialog.findViewById(R.id.location_et);
        EditText experience_et = dialog.findViewById(R.id.experience_et);
        MultiSpinnerSearch skills_selected = dialog.findViewById(R.id.skills_selected);
        HashMap hashMapDefault = new HashMap();
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        apiInterface.GET_SKILL_TBL(hashMapDefault).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            fullSkills = normalCommonResponse.dt;

                            // Pass true If you want searchView above the list. Otherwise false. default = true.
                            skills_selected.setSearchEnabled(true);

                            // A text that will display in search hint.
                            skills_selected.setSearchHint("Select your skill");

                            // Set text that will display when search result not found...
                            skills_selected.setEmptyTitle("Not Data Found!");

                            //A text that will display in clear text button
                            skills_selected.setClearText("Close & Clear");

                            //create arrayList of key with select or not
                            List<KeyPairBoolData> listArray = new ArrayList<>();
                            for (String skillItem : normalCommonResponse.dt) {
                                listArray.add(new KeyPairBoolData(skillItem, false));
                            }

                            // Removed second parameter, position. Its not required now..
                            // If you want to pass preselected items, you can do it while making listArray,
                            // Pass true in setSelected of any item that you want to preselect
                            skills_selected.setItems(listArray, new MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(List<KeyPairBoolData> items) {
                                    for (int i = 0; i < items.size(); i++) {
                                        if (items.get(i).isSelected()) {
                                            Log.d(LogTag.CHECK_DEBUG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
        MaterialButton save_mbtn = dialog.findViewById(R.id.save_mbtn);
        save_mbtn.setOnClickListener(v -> {
            ArrayList<String> selectedSkills = new ArrayList<>();
            for (KeyPairBoolData keyPairBoolData : skills_selected.getSelectedItems()) {
                selectedSkills.add(keyPairBoolData.getName());
            }
            title_post_et.setText(job_position_et.getText().toString());
            job_location_et.setText(location_et.getText().toString());
            filterHashMap.put("title_post", job_position_et.getText().toString());
            filterHashMap.put("job_location", location_et.getText().toString());
            filterHashMap.put("job_type", Arrays.asList(jobType).get(job_type_sp.getSelectedItemPosition()));
            filterHashMap.put("requirements", experience_et.getText().toString());//experience
            filterHashMap.put("skills", TextUtils.join(",", selectedSkills.toArray(new String[selectedSkills.size()])));
            feedsMaster.callSuggestedJobsEventsFeeds(main_ll, scrollView, filterHashMap);
            dialog.dismiss();
        });
        MaterialButton reset_mbtn = dialog.findViewById(R.id.reset_mbtn);
        reset_mbtn.setOnClickListener(v -> {
            job_position_et.setText("");
            job_type_sp.setSelection(0);
            location_et.setText("");
            experience_et.setText("");
        });
    }
}