package com.connester.job.activity.businesspage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.PageJobApplicationResponse;
import com.connester.job.activity.businesspage.JobApplicationListActivity;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class PageJobApplicationFragment extends Fragment {
    String businessPageId;
    FrameLayout progressBar, open_active_list, open_shortlisted_list, open_rejected_list;
    TextView active_job, shortlisted_job, rejected_job;
    PageJobApplicationResponse.Json pageJobApplicationJson;

    public PageJobApplicationFragment(String businessPageId, FrameLayout progressBar) {
        this.businessPageId = businessPageId;
        this.progressBar = progressBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_job_application, container, false);

        progressBar.setVisibility(View.VISIBLE);

        active_job = view.findViewById(R.id.active_job);
        shortlisted_job = view.findViewById(R.id.shortlisted_job);
        rejected_job = view.findViewById(R.id.rejected_job);

        open_active_list = view.findViewById(R.id.open_active_list);
        open_active_list.setOnClickListener(v -> {
            if (pageJobApplicationJson != null) {
                Intent intent = new Intent(getContext(), JobApplicationListActivity.class);
                intent.putExtra("list", "Active");
                intent.putExtra("json", new Gson().toJson(pageJobApplicationJson));
                startActivity(intent);
            }
        });
        open_shortlisted_list = view.findViewById(R.id.open_shortlisted_list);
        open_shortlisted_list.setOnClickListener(v -> {
            if (pageJobApplicationJson != null) {
                Intent intent = new Intent(getContext(), JobApplicationListActivity.class);
                intent.putExtra("list", "Shortlisted");
                intent.putExtra("json", new Gson().toJson(pageJobApplicationJson));
                startActivity(intent);
            }
        });
        open_rejected_list = view.findViewById(R.id.open_rejected_list);
        open_rejected_list.setOnClickListener(v -> {
            if (pageJobApplicationJson != null) {
                Intent intent = new Intent(getContext(), JobApplicationListActivity.class);
                intent.putExtra("list", "Rejected");
                intent.putExtra("json", new Gson().toJson(pageJobApplicationJson));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SessionPref sessionPref = new SessionPref(getContext());
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("business_page_id", businessPageId);
        ApiClient.getClient().create(ApiInterface.class).BUSINESS_PAGE_JOB_APPLICATION(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PageJobApplicationResponse pageJobApplicationResponse = (PageJobApplicationResponse) response.body();
                        if (pageJobApplicationResponse.status) {
                            pageJobApplicationJson = pageJobApplicationResponse.json;
                            if (pageJobApplicationJson.activeList.status)
                                active_job.setText(String.valueOf(pageJobApplicationJson.activeList.dt.size()));
                            if (pageJobApplicationJson.shortlistedList.status)
                                shortlisted_job.setText(String.valueOf(pageJobApplicationJson.shortlistedList.dt.size()));
                            if (pageJobApplicationJson.rejectedList.status)
                                rejected_job.setText(String.valueOf(pageJobApplicationJson.rejectedList.dt.size()));
                        } else
                            Toast.makeText(getContext(), pageJobApplicationResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}