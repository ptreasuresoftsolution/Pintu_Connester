package com.connester.job.function;


import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApiCallback<T> implements Callback<T> {
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        CommonFunction.dismissDialog();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        CommonFunction.dismissDialog();
        Log.d(LogTag.API_EXCEPTION, call.request().url().encodedPath(), t);
    }
}
