package com.connester.job.module;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.jsontogson.FeedsRow;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.SessionPref;

import java.util.ArrayList;

public class FeedsMaster {
    Context context;
    Activity activity;
    LayoutInflater layoutInflater;
    SessionPref sessionPref;
    UserRowResponse.Dt userRow;
    boolean isNeedCloseBtn = true;

    public void setNeedCloseBtn(boolean needCloseBtn) {
        isNeedCloseBtn = needCloseBtn;
    }

    public FeedsMaster(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
        userRow = sessionPref.getUserMasterRowInObject();
        layoutInflater = LayoutInflater.from(context);
    }

    ArrayList<View> feedsViews = new ArrayList<>();

    public View getFeedsPhotoView(FeedsRow feedsRow) {
        View view = layoutInflater.inflate(R.layout.feeds_photos_layout, null);
        setFeedsTitleCommon(view,userRow,feedsRow,isNeedCloseBtn);
        setFeedsLikeCommentShareCommon(view,userRow,feedsRow,isNeedCloseBtn);

        return view;
    }

    private void setFeedsLikeCommentShareCommon(View view, UserRowResponse.Dt userRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {
    }

    private void setFeedsTitleCommon(View view, UserRowResponse.Dt userRow, FeedsRow feedsRow, boolean isNeedCloseBtn) {
        
    }
}
