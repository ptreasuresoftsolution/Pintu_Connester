package com.connester.job.function;


import android.util.Log;
import android.view.View;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApiCallback<T> implements Callback<T> {
    View progressBar;

    public MyApiCallback() {
    }

    public MyApiCallback(View progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        CommonFunction.dismissDialog();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        CommonFunction.dismissDialog();
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        Log.d(LogTag.API_EXCEPTION, call.request().url().encodedPath(), t);
    }
}
