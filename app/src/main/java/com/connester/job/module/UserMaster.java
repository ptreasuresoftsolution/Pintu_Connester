package com.connester.job.module;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMaster {
    public interface CallBack {
        public void DataCallBack(Response response);
    }

    Context context;
    ApiInterface apiInterface;
    SessionPref sessionPref;

    public UserMaster(Context context) {
        this.context = context;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public void getLoginUserData(CallBack callBack) {
        getLoginUserData(callBack, true);
    }

    public void getLoginUserData(CallBack callBack, boolean waitShow) {
        if (waitShow)
            CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        apiInterface.GET_LOGIN_USER_ROW(hashMap).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (waitShow)
                    CommonFunction.dismissDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        UserRowResponse userRowResponse = (UserRowResponse) response.body();
                        if (userRowResponse.status) {
                            sessionPref.setUserProfilePic(userRowResponse.imgPath + userRowResponse.dt.profilePic);
                            sessionPref.setUserEmail(userRowResponse.dt.email);
                            sessionPref.setUserPassword(userRowResponse.dt.password);
                            sessionPref.setUserFullName(userRowResponse.dt.name);
                            sessionPref.setUserName(userRowResponse.dt.userName);
                            sessionPref.setUserMasterRow(new Gson().toJson(userRowResponse.dt));
                        } else
                            Toast.makeText(context, userRowResponse.msg, Toast.LENGTH_SHORT).show();
                        callBack.DataCallBack(response);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (waitShow)
                    CommonFunction.dismissDialog();
                Log.d(LogTag.API_EXCEPTION, "UserMaster GET_LOGIN_USER_ROW Fail", t);
            }
        });
    }
}
