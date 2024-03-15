package com.connester.job.activity.businesspage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSuggestedListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.business.BusinessActivity;
import com.connester.job.activity.NetworkActivity;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class RecommendedPagesFragment extends Fragment {
    SessionPref sessionPref;
    ApiInterface apiInterface;
    FrameLayout progressBar;
    GridView grid_lt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommended_pages, container, false);
        sessionPref = new SessionPref(getContext());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        grid_lt = view.findViewById(R.id.grid_lt);

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        //connectReqUsersMaster / connectUsers / followReqUsers / followerUsers / followingUsers / userCommunitys / userBusinessPages / userEvents /
        //suggestedCityUser / suggestedIndustryUser / suggestedGroup / suggestedBusPages
        hashMap.put("fnName", NetworkActivity.SeeAllFnName.suggestedBusPages.getVal());
        apiInterface.NETWORK_SEE_ALL_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NetworkSuggestedListResponse.JsonDt.SugBusPages sugBusPages =  new Gson().fromJson(new Gson().toJson(response.body()),NetworkSuggestedListResponse.JsonDt.SugBusPages.class);
                        if (sugBusPages.status) {
                            grid_lt.setAdapter(getSuggestedPagesAdapter(sugBusPages));
                        } else
                            Toast.makeText(getContext(), sugBusPages.msg, Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }


        });
        return view;
    }

    private BaseAdapter getSuggestedPagesAdapter(NetworkSuggestedListResponse.JsonDt.SugBusPages sugBusPages) {
        String imgPath = sugBusPages.imgPath;
        Context context = getContext();
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sugBusPages.dt.size();
            }

            @Override
            public NetworkSuggestedListResponse.JsonDt.SugBusPages.Dt getItem(int position) {
                return sugBusPages.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_pages, parent, false);

                NetworkSuggestedListResponse.JsonDt.SugBusPages.Dt row = getItem(position);
                View.OnClickListener openBusinessPage = v -> {
                    Intent intent = new Intent(context, BusinessActivity.class);
                    intent.putExtra("business_page_id", row.businessPageId);
                    startActivity(intent);
                };

                ImageView page_logo_iv = view.findViewById(R.id.page_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_groups_pic).into(page_logo_iv);
                page_logo_iv.setOnClickListener(openBusinessPage);
                TextView page_name_tv = view.findViewById(R.id.page_name_tv);
                page_name_tv.setText(row.busName);
                page_name_tv.setOnClickListener(openBusinessPage);
                TextView page_member_tv = view.findViewById(R.id.page_member_tv);
                page_member_tv.setText(row.members + " members");

                MaterialButton page_follow_btn = view.findViewById(R.id.page_follow_btn);
                page_follow_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // call follow page request api
                        progressBar.setVisibility(View.VISIBLE);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("business_page_id", row.businessPageId);
                        apiInterface.PAGE_FOLLOW_REQUEST(hashMap).enqueue(new MyApiCallback(progressBar) {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status)
                                            removeItem(position);
                                        else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                sugBusPages.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }
}