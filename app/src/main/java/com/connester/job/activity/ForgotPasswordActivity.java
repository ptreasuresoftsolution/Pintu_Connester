package com.connester.job.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email_input;
    MaterialButton sendTempPass_btn, backToLogin_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email_input = findViewById(R.id.email_input);
        sendTempPass_btn = findViewById(R.id.sendTempPass_btn);
        backToLogin_btn = findViewById(R.id.backToLogin_btn);
        backToLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sendTempPass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonFunction.PleaseWaitShow(ForgotPasswordActivity.this);
                HashMap hashMap = new HashMap();
                hashMap.put("email", email_input.getText().toString());
                ApiClient.getClient().create(ApiInterface.class).CALL_FORGOT_PASS(hashMap).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        CommonFunction.dismissDialog();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                Toast.makeText(ForgotPasswordActivity.this, normalCommonResponse.msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        CommonFunction.dismissDialog();
                        Log.d(LogTag.API_EXCEPTION, "CALL FORGOT PASS", t);
                    }
                });
            }
        });
    }
}