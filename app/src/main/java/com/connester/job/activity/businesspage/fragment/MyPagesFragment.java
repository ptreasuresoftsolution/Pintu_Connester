package com.connester.job.activity.businesspage.fragment;

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
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.MyPagesListResponse;
import com.connester.job.activity.business.BusinessActivity;
import com.connester.job.activity.businesspage.ManageMyPageActivity;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class MyPagesFragment extends Fragment {
    SessionPref sessionPref;
    ApiInterface apiInterface;
    FrameLayout progressBar;
    GridView grid_lt;

    SwipeRefreshLayout swipe_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_pages, container, false);
        sessionPref = new SessionPref(getContext());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        grid_lt = view.findViewById(R.id.grid_lt);

        swipe_refresh = view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                setData();
            }
        });
        setData();
        return view;
    }

    private void setData() {

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");

        apiInterface.GET_MY_PAGES_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                        MyPagesListResponse myPagesListResponse = (MyPagesListResponse) response.body();
                        if (myPagesListResponse.status) {
                            grid_lt.setAdapter(getMyPagesAdapter(myPagesListResponse));
                        } else
                            Toast.makeText(getContext(), myPagesListResponse.msg, Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private ListAdapter getMyPagesAdapter(MyPagesListResponse myPagesListResponse) {
        String imgPath = myPagesListResponse.imgPath;
        Context context = getContext();
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return myPagesListResponse.dt.size();
            }

            @Override
            public MyPagesListResponse.Dt getItem(int position) {
                return myPagesListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_pages, parent, false);

                MyPagesListResponse.Dt row = getItem(position);
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

                MaterialButton page_manage_btn = view.findViewById(R.id.page_follow_btn);
                page_manage_btn.setText("Manage");
                page_manage_btn.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ManageMyPageActivity.class);
                    intent.putExtra("business_page_id", row.businessPageId);
                    startActivity(intent);
                });
                return view;
            }
        };
        return baseAdapter;
    }
}