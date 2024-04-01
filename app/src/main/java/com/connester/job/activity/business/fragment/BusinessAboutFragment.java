package com.connester.job.activity.business.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.BusinessPageRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class BusinessAboutFragment extends Fragment {
    String businessPageId;
    FrameLayout progressBar;

    public BusinessAboutFragment(String businessPageId, FrameLayout progressBar) {
        this.businessPageId = businessPageId;
        this.progressBar = progressBar;
    }

    TextView about_tv, website_tv, phone_tv, industry_tv, company_size_tv, location_tv, company_type_tv, founded_tv;
    FlexboxLayout skill_tag_fbl;
    SessionPref sessionPref;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sessionPref = new SessionPref(getContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_about, container, false);

        about_tv = view.findViewById(R.id.about_tv);
        website_tv = view.findViewById(R.id.website_tv);
        phone_tv = view.findViewById(R.id.phone_tv);
        industry_tv = view.findViewById(R.id.industry_tv);
        company_size_tv = view.findViewById(R.id.company_size_tv);
        location_tv = view.findViewById(R.id.location_tv);
        company_type_tv = view.findViewById(R.id.company_type_tv);
        founded_tv = view.findViewById(R.id.founded_tv);
        skill_tag_fbl = view.findViewById(R.id.skill_tag_fbl);

        setData();

        return view;
    }

    private void setData() {
        Context context = getContext();
        progressBar.setVisibility(View.VISIBLE);
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("business_page_id", businessPageId);
        ApiClient.getClient().create(ApiInterface.class).BUSINESS_PAGE_ROW(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        BusinessPageRowResponse businessPageRowResponse = (BusinessPageRowResponse) response.body();
                        if (businessPageRowResponse.status) {
                            BusinessPageRowResponse.BusinessPageRow businessPageRow = businessPageRowResponse.businessPageRow;

                            about_tv.setText(businessPageRow.description);
                            website_tv.setText(businessPageRow.website);
                            phone_tv.setText(businessPageRow.phone);
                            industry_tv.setText(businessPageRow.industry);
                            company_size_tv.setText(businessPageRow.orgSize);
                            location_tv.setText(businessPageRow.address);
                            company_type_tv.setText(businessPageRow.orgType);
                            founded_tv.setText(businessPageRow.foundedYear);

                            String skills[] = businessPageRow.skills.split(",");
                            for (String skill : skills) {
                                View skillLayout = getLayoutInflater().inflate(R.layout.skill_tag_item, null);
                                TextView skill_item = skillLayout.findViewById(R.id.skill_item);
                                skill_item.setText(skill);
                                skill_tag_fbl.addView(skillLayout);
                            }
                        }

                    }
                }
            }
        });
    }
}