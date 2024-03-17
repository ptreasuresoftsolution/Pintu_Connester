package com.connester.job.activity.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.GroupRowResponse;
import com.connester.job.activity.nonslug.AddFeedsActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class CommunityPostFragment extends Fragment {
    ScrollView scrollView;
    String communityMasterId;
    FrameLayout progressBar;
    LinearLayout main_ll;
    MaterialCardView feeds_add_ly;
    FeedsMaster feedsMaster;
    SessionPref sessionPref;

    public CommunityPostFragment(ScrollView scrollView, String communityMasterId, FrameLayout progressBar) {
        this.scrollView = scrollView;
        this.communityMasterId = communityMasterId;
        this.progressBar = progressBar;
    }

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community_post, container, false);
        context = getContext();
        main_ll = view.findViewById(R.id.main_ll);


        feeds_add_ly = view.findViewById(R.id.feeds_add_ly);
        feeds_add_ly.setVisibility(View.GONE);
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("community_master_id", communityMasterId);

        ApiClient.getClient().create(ApiInterface.class).GROUP_ROW(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        GroupRowResponse groupRowResponse = (GroupRowResponse) response.body();
                        if (groupRowResponse.status) {
                            GroupRowResponse.GroupRow groupRow = groupRowResponse.groupRow;
                            //handling redirect for page is not active
                            if (!groupRow.groupStatus.equalsIgnoreCase("ACTIVE")) {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(context, "Page not found! This page is deactivated! Pleas go back.", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                if (groupRowResponse.isMember) {
                                    feeds_add_ly.setVisibility(View.VISIBLE);
                                    feeds_add_ly.setOnClickListener(v -> {
                                        Intent intent = new Intent(getContext(), AddFeedsActivity.class);
                                        intent.putExtra("feed_for", "COMMUNITY");
                                        intent.putExtra("community_master_id", communityMasterId);
                                        startActivity(intent);
                                    });
                                }
                            }
                        }

                    }
                }
            }
        });

        sessionPref = new SessionPref(getContext());
        feedsMaster = new FeedsMaster(getContext(), getActivity());
        feedsMaster.setProgressBar(progressBar);
        feedsMaster.setFeedForForward("COMMUNITY");
        feedsMaster.setFeedForIdForward(communityMasterId);
        feedsMaster.setFeedForId(communityMasterId);
        feedsMaster.setFeedFor("COMMUNITY");
        feedsMaster.setTblName("MEDIA,POST");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (feedsMaster != null)
            feedsMaster.loadFeedMaster(main_ll, scrollView);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (feedsMaster != null) {
            ArrayList<FeedsMaster.FeedStorage> feedsViews = feedsMaster.getFeedsViews();
            for (FeedsMaster.FeedStorage feedStorage : feedsViews) {
                if (feedStorage.isVideoFeeds) {
                    if (feedStorage.styledPlayerView.getTag().equals("play")) {
                        if (feedStorage.player != null && feedStorage.player.isPlaying()) {
                            feedStorage.player.pause();
                        }
                    }
                }
            }
        }
    }
}