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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    public void getUserClmData(CallBack callBack, String clmKey, boolean waitShow) {
        getUserClmData(callBack, clmKey, waitShow, null);
    }

    public void getUserClmData(CallBack callBack, String clmKey, boolean waitShow, String slUserMasterId) {

        if (waitShow)
            CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("key", clmKey);
        if (slUserMasterId != null) {
            hashMap.put("sl_user_master_id", slUserMasterId);
        }
        apiInterface.GET_CLM_DATA_USER_ROW(hashMap).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (waitShow)
                    CommonFunction.dismissDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        UserRowResponse userRowResponse = (UserRowResponse) response.body();
                        if (userRowResponse.status) {
                            if (slUserMasterId == null) {
                                sessionPref.setUserProfilePic(userRowResponse.imgPath + userRowResponse.dt.profilePic);
                                sessionPref.setUserEmail(userRowResponse.dt.email);
                                sessionPref.setUserPassword(userRowResponse.dt.password);
                                sessionPref.setUserFullName(userRowResponse.dt.name);
                                sessionPref.setUserName(userRowResponse.dt.userName);
                                sessionPref.setUserMasterRow(new Gson().toJson(userRowResponse.dt));
                            }
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
                Log.d(LogTag.API_EXCEPTION, "UserMaster GET_CLM_DATA_USER_ROW Fail", t);
            }
        });
    }

    public static List<String> findMutualIds(String ids1, String ids2) {
        if (ids1 == null)
            ids1 = "";
        if (ids2 == null)
            ids2 = "";
        //split and filter (remove blank)
        List<String> idA1 = Stream.of(ids1.split(",")).filter(item -> item != null && !item.isEmpty() && !item.trim().equals("")).collect(Collectors.toList());
        //unique item
        idA1 = new ArrayList<>(new HashSet<>(idA1));
        List<String> idA2 = Stream.of(ids2.split(",")).filter(item -> item != null && !item.isEmpty() && !item.trim().equals("")).collect(Collectors.toList());
        idA2 = new ArrayList<>(new HashSet<>(idA2));
        //same item in both array
        idA1.retainAll(idA2);
        return idA1;
    }

    public static boolean findIdInIds(String id, String ids) {
        //split and filter (remove blank)
        List<String> idsList = Stream.of(ids.split(",")).filter(item -> item != null && !item.isEmpty() && !item.trim().equals("")).collect(Collectors.toList());
        if (idsList.indexOf(id) >= 0)
            return true;
        return false;
    }
}
