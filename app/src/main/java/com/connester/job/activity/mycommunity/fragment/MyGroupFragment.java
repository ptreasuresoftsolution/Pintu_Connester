package com.connester.job.activity.mycommunity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.MyGroupListResponse;
import com.connester.job.activity.community.CommunityActivity;
import com.connester.job.activity.mycommunity.ManageMyCommunityActivity;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class MyGroupFragment extends Fragment {
    SessionPref sessionPref;
    ApiInterface apiInterface;
    FrameLayout progressBar;
    ListView grid_lt;

    SwipeRefreshLayout swipe_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_group, container, false);
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

        apiInterface.GET_MY_GROUP_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                            swipe_refresh.setRefreshing(false);
                        }
                        MyGroupListResponse myGroupListResponse = (MyGroupListResponse) response.body();
                        if (myGroupListResponse.status) {
                            grid_lt.setAdapter(getMyGroupAdapter(myGroupListResponse));
                        } else
                            Toast.makeText(getContext(), myGroupListResponse.msg, Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private ListAdapter getMyGroupAdapter(MyGroupListResponse myGroupListResponse) {
        String imgPath = myGroupListResponse.imgPath;
        Context context = getContext();
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return myGroupListResponse.dt.size();
            }

            @Override
            public MyGroupListResponse.Dt getItem(int position) {
                return myGroupListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.my_group_card_row, parent, false);

                MyGroupListResponse.Dt row = getItem(position);
                View.OnClickListener openGroup = v -> {
                    Intent intent = new Intent(context, CommunityActivity.class);
                    intent.putExtra("community_master_id", String.valueOf(row.communityMasterId));
                    startActivity(intent);
                };

                ImageView group_logo_pic = view.findViewById(R.id.group_logo_pic);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_groups_pic).into(group_logo_pic);
                group_logo_pic.setOnClickListener(openGroup);
                TextView group_name_tv = view.findViewById(R.id.group_name_tv);
                group_name_tv.setText(row.name);
                group_name_tv.setOnClickListener(openGroup);
                TextView group_members = view.findViewById(R.id.group_members);
                group_members.setText(row.members + " members");


                ImageView my_group_option = view.findViewById(R.id.my_group_option);
                my_group_option.setOnClickListener(v1 -> {

                    BottomSheetDialog settingDialog = new BottomSheetDialog(context);
                    settingDialog.setContentView(R.layout.common_option_dialog_layout);
                    LinearLayout manage_group_option_LL = settingDialog.findViewById(R.id.manage_group_option_LL);
                    manage_group_option_LL.setVisibility(View.VISIBLE);

                    manage_group_option_LL.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), ManageMyCommunityActivity.class);
                        intent.putExtra("community_master_id", String.valueOf(row.communityMasterId));
                        startActivity(intent);
                        settingDialog.dismiss();
                    });
                    settingDialog.show();
                });
                return view;
            }
        };
        return baseAdapter;
    }
}