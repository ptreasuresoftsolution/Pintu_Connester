package com.connester.job.module.notification_message;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.ChatUserListResponse;
import com.connester.job.RetrofitConnection.jsontogson.MessageListResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserStatusUpdateResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChatModule {
    public static final String MSG_RECEIVED_FILTER = "MSG_RECEIVED_FILTER";
    public static final String MSG_DELIVERED_FILTER = "MSG_DELIVERED_FILTER";
    public static final String MSG_READ_FILTER = "MSG_READ_FILTER";
    public static final String CHAT_STATUS_UPDATE_FILTER = "CHAT_STATUS_UPDATE_FILTER";

    public static final long MSG_ROW_LIMIT = 10;


    public enum FileType {
        IMAGE("IMAGE"), VIDEO("VIDEO"), DOC("DOC");
        private String val;

        FileType(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    Context context;
    Activity activity;
    ApiInterface apiInterface;
    SessionPref sessionPref;
    HashMap defaultUserData = new HashMap();

    public ChatModule(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        defaultUserData.put("user_master_id", sessionPref.getUserMasterId());
        defaultUserData.put("apiKey", sessionPref.getApiKey());
        defaultUserData.put("device", "ANDROID");
    }

    public interface MessageListCallBack {
        public void callBack(MessageListResponse messageListResponse);
    }

    public void getCusMessage(String view_cus_id, long start, MessageListCallBack messageListCallBack) {
        if (start == 0)
            CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.putAll(defaultUserData);
        hashMap.put("sl_user_master_id", view_cus_id);
        hashMap.put("start", start);
        apiInterface.MESSAGE_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MessageListResponse messageListResponse = (MessageListResponse) response.body();
                        if (messageListResponse.status) {
                            messageListCallBack.callBack(messageListResponse);
                        } else
                            Toast.makeText(context, messageListResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    static boolean callApi = false;

    public void chatStatusApiCall(String status) {
        if (callApi) {
            return;
        }
        callApi = true;
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.putAll(defaultUserData);
        hashMap.put("status", status);
        apiInterface.USER_STATUS_UPDATE_CALL(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        UserStatusUpdateResponse userStatusUpdateResponse = (UserStatusUpdateResponse) response.body();
                        Log.d(LogTag.CHECK_DEBUG, "User status update: " + userStatusUpdateResponse.status);
                        callApi = false;
                    }
                }
            }
        });
    }

    public static int findIndexOfForMessage(List<MessageListResponse.Dt> tableChatDatas, String chatMasterId) {
        for (int i = 0; i < tableChatDatas.size(); i++) {
            if (tableChatDatas.get(i).chatMasterId.equalsIgnoreCase(chatMasterId)) {
                return i;
            }
        }
        return -1;
    }

    public static int findIndexOfForUser(List<ChatUserListResponse.Dt> chatUserDatas, String userMasterId) {
        for (int i = 0; i < chatUserDatas.size(); i++) {
            if (chatUserDatas.get(i).userMasterId.equalsIgnoreCase(userMasterId)) {
                return i;
            }
        }
        return -1;
    }


    public static int getDocFileResource(String msgFile) {
        if (msgFile != null && !msgFile.equalsIgnoreCase("")) {
            if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("pdf")) {
                return R.drawable.file_earmark_pdf;
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("doc") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("docx")) {
                return R.drawable.file_earmark_word;
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("xls") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("xlsx") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("csv")) {
                return R.drawable.file_earmark_excel;
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("ppt") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("pptx")) {
                return R.drawable.file_earmark_ppt;
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("txt")) {
                return R.drawable.file_earmark_text;
            }
        }
        return R.drawable.file_earmark_document;
    }
    public static String getDocFileOpenType(String msgFile) {
        if (msgFile != null && !msgFile.equalsIgnoreCase("")) {
            if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("pdf")) {
                return "application/pdf";
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("doc") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("docx")) {
                return "application/msword";
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("xls") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("xlsx") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("csv")) {
                return "application/vnd.ms-excel";
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("ppt") || CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("pptx")) {
                return "application/vnd.ms-powerpoint";
            } else if (CommonFunction.getFileExtension(msgFile).equalsIgnoreCase("txt")) {
                return "text/plain";
            }
        }
        return "*/*";
    }
    /*
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip")) {
            // ZIP file
            intent.setDataAndType(uri, "application/zip");
        } else if (url.toString().contains(".rar")){
            // RAR file
            intent.setDataAndType(uri, "application/x-rar-compressed");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*//*");
        }
     */
}
