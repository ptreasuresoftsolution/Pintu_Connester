package com.connester.job.activity.mycommunity;

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
import com.connester.job.RetrofitConnection.jsontogson.RequestedGroupListResponse;
import com.connester.job.activity.community.CommunityActivity;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class RequestedGroupFragment extends Fragment {
    SessionPref sessionPref;
    ApiInterface apiInterface;
    FrameLayout progressBar;
    GridView grid_lt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_requested_group, container, false);
        sessionPref = new SessionPref(getContext());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        grid_lt = view.findViewById(R.id.grid_lt);

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        apiInterface.GET_MY_REQUESTED_GROUP_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        RequestedGroupListResponse requestedGroupListResponse = (RequestedGroupListResponse) response.body();
                        if (requestedGroupListResponse.status) {
                            grid_lt.setAdapter(getRequestedGroupAdapter(requestedGroupListResponse));
                        } else
                            Toast.makeText(getContext(), requestedGroupListResponse.msg, Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }


        });
        return view;
    }

    private BaseAdapter getRequestedGroupAdapter(RequestedGroupListResponse requestedGroupListResponse) {
        String imgPath = requestedGroupListResponse.imgPath;
        Context context = getContext();
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return requestedGroupListResponse.dt.size();
            }

            @Override
            public RequestedGroupListResponse.Dt getItem(int position) {
                return requestedGroupListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_network_group, parent, false);

                RequestedGroupListResponse.Dt row = getItem(position);
                View.OnClickListener openGroup = v -> {
                    Intent intent = new Intent(context, CommunityActivity.class);
                    intent.putExtra("community_master_id", row.communityMasterId);
                    startActivity(intent);
                };

                ImageView group_logo_iv = view.findViewById(R.id.group_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_groups_pic).into(group_logo_iv);
                group_logo_iv.setOnClickListener(openGroup);
                TextView group_name_tv = view.findViewById(R.id.group_name_tv);
                group_name_tv.setText(row.name);
                group_name_tv.setOnClickListener(openGroup);
                TextView group_members = view.findViewById(R.id.group_members);
                group_members.setText(row.members + " members");

                MaterialButton group_exit_mbtn = view.findViewById(R.id.group_exit_mbtn);
                group_exit_mbtn.setText("Pending");
                return view;
            }

            private void removeItem(int position) {
                requestedGroupListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }
}