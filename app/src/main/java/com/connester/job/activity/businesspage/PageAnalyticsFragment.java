package com.connester.job.activity.businesspage;

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
import com.connester.job.RetrofitConnection.jsontogson.PageAnalyticsResponse;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class PageAnalyticsFragment extends Fragment {
    String businessPageId;
    FrameLayout progressBar;
    TextView search_appearances, page_views, total_followers, post_impressions;

    public PageAnalyticsFragment(String businessPageId, FrameLayout progressBar) {
        this.businessPageId = businessPageId;
        this.progressBar = progressBar;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_analytics, container, false);
        search_appearances = view.findViewById(R.id.search_appearances);
        page_views = view.findViewById(R.id.page_views);
        total_followers = view.findViewById(R.id.total_followers);
        post_impressions = view.findViewById(R.id.post_impressions);

        SessionPref sessionPref = new SessionPref(getContext());

        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("business_page_id", businessPageId);
        ApiClient.getClient().create(ApiInterface.class).BUSINESS_PAGE_ANALYTICS(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        PageAnalyticsResponse pageAnalyticsResponse = (PageAnalyticsResponse) response.body();
                        if (pageAnalyticsResponse.status) {
                            search_appearances.setText(pageAnalyticsResponse.dt.pageSearchAppreanace);
                            page_views.setText(pageAnalyticsResponse.dt.pageViews);
                            total_followers.setText(pageAnalyticsResponse.dt.pageMember);
                            post_impressions.setText(pageAnalyticsResponse.dt.pageFeedsImpression);
                        } else
                            Toast.makeText(getContext(), pageAnalyticsResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }
}