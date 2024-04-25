package com.connester.job.module;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

                            HashMap hashMap = new HashMap();
                            hashMap.put("user_master_id", sessionPref.getUserMasterId());
                            hashMap.put("apiKey", sessionPref.getApiKey());
                            hashMap.put("mobile_token", sessionPref.getDEVICE_TOKEN());
                            hashMap.put("device_unique", CommonFunction.getDeviceId(context));
                            hashMap.put("device_type", "ANDROID");
                            hashMap.put("device_info", CommonFunction.getDeviceName(context));
                            apiInterface.ADD_REGISTER_TOKEN(hashMap).enqueue(new MyApiCallback() {
                                @Override
                                public void onResponse(Call call, Response response2) {
                                    super.onResponse(call, response2);
                                    if (response2.isSuccessful()) {
                                        if (response2.body() != null) {
                                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response2.body();
                                            Log.d(LogTag.CHECK_DEBUG, "On user master Token is Add or Register : " + normalCommonResponse.msg);
                                            callBack.DataCallBack(response);
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context, userRowResponse.msg, Toast.LENGTH_SHORT).show();
                            callBack.DataCallBack(response);
                        }

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
        if (ids != null && id != null) {
            //split and filter (remove blank)
            List<String> idsList = Stream.of(ids.split(",")).filter(item -> item != null && !item.isEmpty() && !item.trim().equals("")).collect(Collectors.toList());
            if (idsList.indexOf(id) >= 0)
                return true;
        }
        return false;
    }

    ComponentActivity activity;

    public UserMaster(Context context, ComponentActivity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public void initReportAttachmentLauncher() {
        activityResultLauncherForReportAttachment = activity.registerForActivityResult(new ActivityResultContracts.GetContent(), photoUri -> {
            if (photoUri != null && attachment_iv != null) {
                attachmentFile = new File(FilePath.getPath2(context, photoUri));
                Glide.with(context).load(attachmentFile).centerCrop().placeholder(R.drawable.default_business_pic).into(attachment_iv);
            }
        });
    }

    ActivityResultLauncher activityResultLauncherForReportAttachment;
    File attachmentFile;
    ImageView attachment_iv;

    public void openReportDialog(String reportTitle, String tbl_name, String tbl_id, Context context) {
        Dialog dialog = new Dialog(context, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.report_common_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        String[] reportType = context.getResources().getStringArray(R.array.report_type);

        TextView title = dialog.findViewById(R.id.title);
        title.setText("Submit a report on " + reportTitle);
        Spinner report_type_sp = dialog.findViewById(R.id.report_type_sp);
        EditText description_et = dialog.findViewById(R.id.description_et);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        attachment_iv = dialog.findViewById(R.id.attachment_iv);
        attachment_iv.setOnClickListener(v -> {
            activityResultLauncherForReportAttachment.launch(("image/*"));
        });

        MaterialButton submit_mbtn = dialog.findViewById(R.id.submit_mbtn);
        submit_mbtn.setOnClickListener(v -> {
            CommonFunction.PleaseWaitShow(context);
            CommonFunction.PleaseWaitShowMessage("Files is compressing...");
            try {

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                        .addFormDataPart("apiKey", sessionPref.getApiKey())
                        .addFormDataPart("submit_user_master_id", sessionPref.getUserMasterId())
                        .addFormDataPart("tbl_name", tbl_name)
                        .addFormDataPart("tbl_id", tbl_id)
                        .addFormDataPart("report_question", reportType[report_type_sp.getSelectedItemPosition()])
                        .addFormDataPart("report_user_describe", description_et.getText().toString());

                File attFile = null;
                if (attachmentFile != null) {
                    attFile = new Compressor(context)
                            .setMaxWidth(1080)
                            .setMaxWidth(800)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                            .compressToFile(attachmentFile);
                    builder.addFormDataPart("attechment", attFile.getName(),
                            RequestBody.create(MediaType.parse(FilePath.getMimeType(attFile)), attFile));

                }
                CommonFunction.PleaseWaitShowMessage("Files is compressed completed");


                RequestBody body = builder.build();
                CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                File finalImgFileLogo = attFile;
                apiInterface.SUBMIT_ALL_REPORT(body).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    if (finalImgFileLogo != null) finalImgFileLogo.delete();

                                    CommonFunction._LoadAlert(context, "Your report is submit & reference Id: " + normalCommonResponse.reportMasterId);
                                    dialog.dismiss();
                                }
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(LogTag.EXCEPTION, "Image Compress from Business page create Exception", e);
            }
        });
        dialog.show();
    }
}
