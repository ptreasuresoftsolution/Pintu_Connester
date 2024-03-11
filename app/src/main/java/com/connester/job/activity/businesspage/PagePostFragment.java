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
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.module.FeedsMaster;
import com.google.android.material.card.MaterialCardView;

public class PagePostFragment extends Fragment {
    ScrollView scrollView;
    String business_page_id;
    LinearLayout main_ll;
    MaterialCardView feeds_add_ly;
    FeedsMaster feedsMaster;
    ViewPager view_pager;

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

        feedsMaster = new FeedsMaster(getContext(), getActivity());
        feedsMaster.setFeedForForward("BUSINESS");
        feedsMaster.setFeedForIdForward(business_page_id);
//        feedsMaster.setLimitGap(1);
        feedsMaster.setFeedForId(business_page_id);
        feedsMaster.setFeedFor("BUSINESS");
        feedsMaster.setTblName("MEDIA,POST");
        feedsMaster.setMainLinearLayoutChange(new FeedsMaster.MainLinearLayoutChange() {
            @Override
            public void itemAddEditChange(LinearLayout linearLayout) {
                int scal[] = CommonFunction.getScreenResolution(getContext());
                if (isVisibleToUser) {
                    linearLayout.measure(0, 0);
//                    if (lastHeight + scal[1] < linearLayout.getMeasuredHeight())
//                        lastHeight = linearLayout.getMeasuredHeight() - 500;
//                    else
                    lastHeight = linearLayout.getMeasuredHeight();
                    Log.e(LogTag.TMP_LOG, lastHeight + " sc " + scal[1] + " get " + linearLayout.getMeasuredHeight() + " x " + linearLayout.getChildCount());
//                    if ((linearLayout.getMeasuredHeight() - 200) < (scal[1])) {
//                        lastHeight = linearLayout.getMeasuredHeight() - 500;
//                    }
//                    lastHeight = linearLayout.getMeasuredHeight() - 500;

                    if (linearLayout.getChildCount() > 2) {
                        lastHeight = linearLayout.getMeasuredHeight() - 500;

                    }

                    ViewGroup.LayoutParams params = view_pager.getLayoutParams();
                    params.height = lastHeight;
                    view_pager.setLayoutParams(params);
                }
            }
        });
        feedsMaster.loadFeedMaster(main_ll, scrollView);


        return view;
    }

    int lastHeight = 0;
    boolean isVisibleToUser = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            ViewGroup.LayoutParams params = view_pager.getLayoutParams();
            params.height = lastHeight;
            view_pager.setLayoutParams(params);
        }
    }

}