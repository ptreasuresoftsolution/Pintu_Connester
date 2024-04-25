package com.connester.job.activity.mysaveditem.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.connester.job.R;
import com.connester.job.activity.mysaveditem.MySavedItemActivity;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;

public class PostsFragment extends Fragment {
    ScrollView scrollView;
    LinearLayout feeds_save_post_list;
    FrameLayout progressBar;
    FeedsMaster feedsMaster;
    SessionPref sessionPref;
    SwipeRefreshLayout swipe_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        scrollView = view.findViewById(R.id.scrollView);
        feeds_save_post_list = view.findViewById(R.id.feeds_save_post_list);
        progressBar = view.findViewById(R.id.progressBar);

        sessionPref = new SessionPref(getContext());
        if (sessionPref.getUserMasterRowInObject().saveFeeds != null && !sessionPref.getUserMasterRowInObject().saveFeeds.equalsIgnoreCase("")) {
            feedsMaster = new FeedsMaster(getContext(), getActivity(),null);
            feedsMaster.setUserMaster(MySavedItemActivity.userMaster);
            feedsMaster.setProgressBar(progressBar);
            feedsMaster.setChkClose(false);
            feedsMaster.setFeedsIds(sessionPref.getUserMasterRowInObject().saveFeeds);
            feedsMaster.setTblName("MEDIA,POST");
            feedsMaster.loadFeedMaster(feeds_save_post_list, scrollView, 25);

            swipe_refresh = view.findViewById(R.id.swipe_refresh);
            swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipe_refresh.setRefreshing(true);
                    feedsMaster.setMainLinearLayoutChange(new FeedsMaster.MainLinearLayoutChange() {
                        @Override
                        public void itemAddEditChange(LinearLayout linearLayout) {
                            if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                                swipe_refresh.setRefreshing(false);
                            }
                        }
                    });
                    feedsMaster.loadFeedMaster(feeds_save_post_list, scrollView, 25);
                }
            });
        }
        return view;
    }
}
