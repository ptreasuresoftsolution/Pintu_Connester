package com.connester.job.activity.community.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.GroupMembersListResponse;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class CommunityMembersFragment extends Fragment {
    String communityMasterId;
    FrameLayout progressBar;
    ScrollView scrollView;
    HashMap hashMapMain = new HashMap();

    public CommunityMembersFragment(ScrollView scrollView, String communityMasterId, FrameLayout progressBar) {
        this.scrollView = scrollView;
        this.communityMasterId = communityMasterId;
        this.progressBar = progressBar;
    }

    LinearLayout main_ll;
    ApiInterface apiInterface;
    SessionPref sessionPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionPref = new SessionPref(getContext());
        hashMapMain.put("user_master_id", sessionPref.getUserMasterId());
        hashMapMain.put("apiKey", sessionPref.getApiKey());
        hashMapMain.put("device", "ANDROID");
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community_members, container, false);


        main_ll = view.findViewById(R.id.main_ll);
        main_ll.removeAllViews();
        HashMap hashMap = new HashMap();
        hashMap.putAll(hashMapMain);
        hashMap.put("community_master_id", communityMasterId);
        apiInterface.GROUP_MEMBERS_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Context context = getContext();
                        GroupMembersListResponse groupMembersListResponse = (GroupMembersListResponse) response.body();
                        if (groupMembersListResponse.status) {
                            for (GroupMembersListResponse.Dt dt : groupMembersListResponse.dt) {
                                View jobApplicationView = getLayoutInflater().inflate(R.layout.user_pic_two_btn_list_item, null);

                                ImageView member_profile_pic = jobApplicationView.findViewById(R.id.member_profile_pic);
                                Glide.with(context).load(groupMembersListResponse.imgPath + dt.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(member_profile_pic);

                                TextView member_tv = jobApplicationView.findViewById(R.id.member_tv);
                                member_tv.setText(dt.name);

                                View.OnClickListener openUserProfile = v -> {
                                    Intent intent = new Intent(context, ProfileActivity.class);
                                    intent.putExtra("user_master_id", dt.userMasterId);
                                    startActivity(intent);
                                };
                                member_profile_pic.setOnClickListener(openUserProfile);
                                member_tv.setOnClickListener(openUserProfile);

                                MaterialButton first_mbtn = jobApplicationView.findViewById(R.id.first_mbtn);
                                first_mbtn.setVisibility(View.GONE);

                                MaterialButton second_mbtn = jobApplicationView.findViewById(R.id.second_mbtn);
                                second_mbtn.setVisibility(View.GONE);

                                main_ll.addView(jobApplicationView);
                            }
                        } else
                            Toast.makeText(getContext(), groupMembersListResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }
}