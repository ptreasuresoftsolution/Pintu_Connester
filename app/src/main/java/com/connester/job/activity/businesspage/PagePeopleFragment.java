package com.connester.job.activity.businesspage;

import android.content.Context;
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
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.PageMembersResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class PagePeopleFragment extends Fragment {
    FrameLayout progressBar;
    String business_page_id;
    ScrollView scrollView;
    HashMap hashMapMain = new HashMap();

    public PagePeopleFragment(ScrollView scrollView, String business_page_id, FrameLayout progressBar) {
        this.scrollView = scrollView;
        this.business_page_id = business_page_id;
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
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        main_ll = view.findViewById(R.id.main_ll);
        main_ll.removeAllViews();
        HashMap hashMap = new HashMap();
        hashMap.putAll(hashMapMain);
        hashMap.put("business_page_id", business_page_id);
        apiInterface.PAGES_MEMBERS_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Context context = getContext();
                        PageMembersResponse pageMembersResponse = (PageMembersResponse) response.body();
                        if (pageMembersResponse.status) {
                            for (PageMembersResponse.Dt dt : pageMembersResponse.dt) {
                                View jobApplicationView = getLayoutInflater().inflate(R.layout.job_application_list_item, null);


                                ImageView member_profile_pic = jobApplicationView.findViewById(R.id.member_profile_pic);
                                Glide.with(context).load(pageMembersResponse.imgPath + dt.profilePic).centerCrop().placeholder(R.drawable.user_default_banner).into(member_profile_pic);

                                TextView member_tv = jobApplicationView.findViewById(R.id.member_tv);
                                member_tv.setText(dt.name);

                                TextView first_tv = jobApplicationView.findViewById(R.id.first_tv);
                                first_tv.setText("Remove");
                                first_tv.setOnClickListener(v -> {
                                    //call application add to active
                                    CommonFunction.PleaseWaitShow(context);
                                    HashMap hashMap = new HashMap();
                                    hashMap.putAll(hashMapMain);
                                    hashMap.put("removeBusinessPageMemberId", dt.userMasterId);
                                    hashMap.put("business_page_id", business_page_id);
                                    apiInterface.PAGES_MEMBERS_REMOVE(hashMap).enqueue(new MyApiCallback() {
                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            super.onResponse(call, response);
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                                    if (normalCommonResponse.status) {
                                                        main_ll.removeView(jobApplicationView);
                                                    }
                                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                });

                                LinearLayout second_line_ll = jobApplicationView.findViewById(R.id.second_line_ll);
                                second_line_ll.setVisibility(View.GONE);
                                TextView second_tv = jobApplicationView.findViewById(R.id.second_tv);
                                second_tv.setVisibility(View.GONE);

                                main_ll.addView(jobApplicationView);
                            }
                        } else
                            Toast.makeText(getContext(), pageMembersResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}