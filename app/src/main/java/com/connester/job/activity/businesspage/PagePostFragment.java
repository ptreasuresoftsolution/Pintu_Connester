package com.connester.job.activity.businesspage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.connester.job.R;
import com.connester.job.activity.nonslug.AddFeedsActivity;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.google.android.material.card.MaterialCardView;

public class PagePostFragment extends Fragment {
    ScrollView scrollView;
    String business_page_id;
    LinearLayout main_ll;
    MaterialCardView feeds_add_ly;
    FeedsMaster feedsMaster;
    ViewPager view_pager;
    SessionPref sessionPref;

    public PagePostFragment(ScrollView scrollView, String business_page_id, ViewPager view_pager) {
        this.scrollView = scrollView;
        this.business_page_id = business_page_id;
        this.view_pager = view_pager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_post, container, false);
        main_ll = view.findViewById(R.id.main_ll);
        feeds_add_ly = view.findViewById(R.id.feeds_add_ly);
        feeds_add_ly.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddFeedsActivity.class);
            intent.putExtra("feed_for", "BUSINESS");
            intent.putExtra("business_page_id", business_page_id);
            startActivity(intent);
        });

        sessionPref = new SessionPref(getContext());
        feedsMaster = new FeedsMaster(getContext(), getActivity());
//        feedsMaster.setFeedForForward("BUSINESS");
//        feedsMaster.setFeedForIdForward(business_page_id);
//        feedsMaster.setLimitGap(1);
//        feedsMaster.setFeedForId(business_page_id);


        feedsMaster.setFeedForId(sessionPref.getUserMasterId());

        feedsMaster.setFeedFor("USER");
        feedsMaster.setTblName("MEDIA,POST");
        feedsMaster.loadFeedMaster(main_ll, scrollView);

        if (isVisibleToUser) {
            feedsMaster.setViewDisable(false);
        } else {
            feedsMaster.setViewDisable(true);
        }

        return view;
    }


    boolean isVisibleToUser = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (feedsMaster != null)
            feedsMaster.setViewDisable(true);
        if (isVisibleToUser) {
            if (feedsMaster != null)
                feedsMaster.setViewDisable(false);
          /*  ViewGroup.LayoutParams params = view_pager.getLayoutParams();
            params.height = lastHeight;
            view_pager.setLayoutParams(params);*/
        }
        Log.e(LogTag.TMP_LOG, "bb " + isVisibleToUser + " fed " + (feedsMaster != null));
    }

}