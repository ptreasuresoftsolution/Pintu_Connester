package com.connester.job.module.notification_message;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.CountingFileRequestBody;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.SendMessageResponse;
import com.connester.job.function.Constant;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class FileMessageUploadService extends Worker {

    public static final String UPLOAD_FILE_PROCESS = "UPLOAD_FILE_PROCESS";
    public static final String UPLOAD_FILE_COMPLETE = "UPLOAD_FILE_COMPLETE";
    public static final String UPLOAD_FILE_START = "UPLOAD_FILE_START";

    String chat_master_id;
    String fileType;
    String msgFileLocalUri;

    Context context = getApplicationContext();
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    private int notificationId = this.getId().hashCode();
    private UUID workId = this.getId();

    public FileMessageUploadService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
//        this.context = context;
    }

    @Override
    public void onStopped() {
        Log.e(LogTag.CHECK_DEBUG, "Stop call in file upload worker");
        super.onStopped();
    }

    MultipartBody requestBody;

    public Result doWork() {
        Log.e(LogTag.CHECK_DEBUG, "oneTimeWorkRequest Upload starting... in background With Quality");

        chat_master_id = getInputData().getString("chat_master_id");
        fileType = getInputData().getString("fileType");
        msgFileLocalUri = getInputData().getString("msgFileLocalUri");
        SessionPref sessionPref = new SessionPref(context);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        createNotificationChannel();

        try {
            //call file upload api

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("user_master_id", sessionPref.getUserMasterId());
            builder.addFormDataPart("apiKey", sessionPref.getApiKey());
            builder.addFormDataPart("device_type", "ANDROID");
            builder.addFormDataPart("chat_master_id", chat_master_id);
            builder.addFormDataPart("file_type", fileType);

            if (fileType.equalsIgnoreCase(ChatModule.FileType.IMAGE.getVal())){

            }else if (fileType.equalsIgnoreCase(ChatModule.FileType.VIDEO.getVal())){

            }else {//DOC

            }
            File file = new File(msgFileLocalUri);//remain compress process


            String mimeType = FilePath.getMimeType(file);
            builder.addFormDataPart("msg_file", file.getName(), RequestBody.create(MediaType.parse(mimeType), file));

            requestBody = builder.build();
            CountingFileRequestBody countingFileRequestBody = new CountingFileRequestBody(requestBody, "msg_file", new CountingFileRequestBody.ProgressListener() {
                @Override
                public void transferred(String key, int num) {
                    Log.e(LogTag.TMP_LOG, "progress % is : " + num);
                    if (!isStopped()) {

                        Intent intent = new Intent(UPLOAD_FILE_PROCESS);
                        intent.putExtra("chat_master_id", chat_master_id);
                        intent.putExtra("process", num);
                        context.sendBroadcast(intent);


                        Intent intents = new Intent(context, CharSequence.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intents, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
//                        PendingIntent pendingIntent = WorkManager.getInstance().createCancelPendingIntent(workId);

                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constant.channel_id);
                        builder.setContentTitle("File Sending")
                                .setContentText("Sending file in progress")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.cloud_upload_fill).setProgress(100, num, false)
                                .setAutoCancel(true)
                                .setSound(null)
                                .setOngoing(true)
                                .setSilent(true)
                                .setContentIntent(pendingIntent);

                        notificationManager.notify(notificationId, builder.build());
                    }
                }
            });

            apiInterface.CHAT_FILES_UPLOADER(countingFileRequestBody).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            SendMessageResponse sendMessageResponse = (SendMessageResponse) response.body();
                            if (sendMessageResponse.status) {

                                Intent intent = new Intent(UPLOAD_FILE_COMPLETE);
                                intent.putExtra("chat_master_id", chat_master_id);
                                intent.putExtra("pushJson", new Gson().toJson(sendMessageResponse.pushJson));
                                context.sendBroadcast(intent);
                                notificationManager.cancel(notificationId);
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(LogTag.EXCEPTION, "File Upload Fail Exception", e);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constant.channel_id);
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("Upload Failed")).setTicker("Upload Failed");
            notificationManager.notify(notificationId, builder.build());

            //call error message status api
            HashMap hashMap = new HashMap();
            hashMap.put("user_master_id", sessionPref.getUserMasterId());
            hashMap.put("apiKey", sessionPref.getApiKey());
            hashMap.put("chat_master_id", chat_master_id);
            hashMap.put("error_msg", "Error:Something wrong! in file upload process!");
            apiInterface.MESSAGE_STATUS_ERROR(hashMap).enqueue(new MyApiCallback() {
                @Override
                public void onResponse(Call call, Response response) {
                    super.onResponse(call, response);
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                            if (normalCommonResponse.status) {
                                Log.d(LogTag.CHECK_DEBUG, "Confirm to api upload in file error");
                            }
                        }
                    }
                }
            });
            Result.failure();
        }

        return Result.success();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(Constant.channel_id);
            if (notificationChannel == null) {
                String channelname = context.getString(R.string.upload_file_channel_name);
                notificationChannel = new NotificationChannel(Constant.channel_id, channelname, NotificationManager.IMPORTANCE_DEFAULT);
            }
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
