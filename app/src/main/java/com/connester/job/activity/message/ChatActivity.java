package com.connester.job.activity.message;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.FirebaseFCMResponse;
import com.connester.job.RetrofitConnection.jsontogson.MessageListResponse;
import com.connester.job.RetrofitConnection.jsontogson.MessageStatusUpdateResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.SendMessageResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserStatusUpdateResponse;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.DateUtils;
import com.connester.job.function.FilePath;
import com.connester.job.function.FileUtils;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.connester.job.module.notification_message.ChatModule;
import com.connester.job.module.notification_message.FileMessageUploadService;
import com.connester.job.module.notification_message.TypingOnlineListener;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    String user_master_id;//compulsory filed
    String message = null;
    boolean isEncryMsg = false;
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ApiInterface apiInterface;
    UserRowResponse.Dt userDt;
    UserMaster userMaster;
    ChatModule chatModule;
    String imgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/auto/"; //overwrite on api call
    String chatImgPath = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/chat/";

    HashMap defaultUserData = new HashMap();

    WorkManager workManager;
    NotificationManager notificationManager;

    SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        chatModule = new ChatModule(context, activity);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        userMaster = new UserMaster(context, ChatActivity.this);
        userMaster.initReportAttachmentLauncher();

        workManager = WorkManager.getInstance(getApplication());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        defaultUserData.put("user_master_id", sessionPref.getUserMasterId());
        defaultUserData.put("apiKey", sessionPref.getApiKey());
        defaultUserData.put("device", "ANDROID");

        if (getIntent() != null) {
            user_master_id = getIntent().getStringExtra("user_master_id");
            isEncryMsg = getIntent().getBooleanExtra("isEncryMsg", false);
            message = getIntent().getStringExtra("message");
            if (isEncryMsg && message != null) {
                message = CommonFunction.base64Decode(message);
            }
        }
        if (user_master_id == null) {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }

        activityResultLauncherForPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), fileUri -> {
            if (fileUri != null) {
                uploadFileDialog(ChatModule.FileType.IMAGE.getVal(), fileUri);
            }
        });
        activityResultLauncherForVideo = registerForActivityResult(new ActivityResultContracts.GetContent(), fileUri -> {
            if (fileUri != null) {
                uploadFileDialog(ChatModule.FileType.VIDEO.getVal(), fileUri);
            }
        });
        activityResultLauncherForDoc = registerForActivityResult(new ActivityResultContracts.GetContent(), fileUri -> {
            if (fileUri != null) {
//                getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uploadFileDialog(ChatModule.FileType.DOC.getVal(), fileUri);
            }
        });

        initView();
    }


    ActivityResultLauncher activityResultLauncherForPhoto, activityResultLauncherForVideo, activityResultLauncherForDoc;
    EditText message_ed_txt;
    ImageView back, profile_pic, btnFileGallery, chatOption, send_message_btn;
    LinearLayout chat_profile;
    TextView name, statusTxt, txtBlock, date;
    MaterialCardView date_area;
    RecyclerView message_list;
    LinearLayout reply_layout;
    TextView textQuotedMessage;
    ImageButton cancelButton;

    BottomSheetDialog chatOptionBottomSheetDialog;

    private void initView() {
        chat_profile = findViewById(R.id.btn_chat_profile);
        View.OnClickListener openProfile = v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user_master_id", user_master_id);
            startActivity(intent);
            if (chatOptionBottomSheetDialog != null && chatOptionBottomSheetDialog.isShowing())
                chatOptionBottomSheetDialog.dismiss();
        };
        chat_profile.setOnClickListener(openProfile);
        back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        profile_pic = findViewById(R.id.profile_pic);
        name = findViewById(R.id.name);
        statusTxt = findViewById(R.id.statusTxt);
        chatOption = findViewById(R.id.chat_option);
        chatOption.setOnClickListener(v -> {
            chatOptionBottomSheetDialog = new BottomSheetDialog(context);
            chatOptionBottomSheetDialog.setContentView(R.layout.common_option_dialog_layout);

            LinearLayout profile_view_LL = chatOptionBottomSheetDialog.findViewById(R.id.profile_view_LL),
                    delete_conversation_LL = chatOptionBottomSheetDialog.findViewById(R.id.delete_conversation_LL),
                    block_user_LL = chatOptionBottomSheetDialog.findViewById(R.id.block_user_LL),
                    report_LL = chatOptionBottomSheetDialog.findViewById(R.id.report_LL);

            profile_view_LL.setVisibility(View.VISIBLE);
            delete_conversation_LL.setVisibility(View.VISIBLE);
            block_user_LL.setVisibility(View.VISIBLE);
            report_LL.setVisibility(View.VISIBLE);

            profile_view_LL.setOnClickListener(openProfile);
            delete_conversation_LL.setOnClickListener(v1 -> {
                //call chat clear api
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.putAll(defaultUserData);
                hashMap.put("sl_user_master_id", user_master_id);
                apiInterface.MESSAGE_CONVERSATION_CLEAR(hashMap).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status) {
                                    tableChatDatas.clear();
                                    if (message_list != null) {
                                        message_list.getAdapter().notifyDataSetChanged();
                                    }
                                    chatOptionBottomSheetDialog.dismiss();
                                } else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            });
            block_user_LL.setOnClickListener(v1 -> {
                //call block member api
                CommonFunction.PleaseWaitShow(context);
                HashMap hashMap = new HashMap();
                hashMap.putAll(defaultUserData);
                hashMap.put("id", user_master_id);
                apiInterface.BLOCKED_USER(hashMap).enqueue(new MyApiCallback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                if (normalCommonResponse.status)
                                    chatOptionBottomSheetDialog.dismiss();
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            });
            report_LL.setOnClickListener(v1 -> {
                //call user chat report
                userMaster.openReportDialog("Profile", "user_master", user_master_id, context);
                chatOptionBottomSheetDialog.dismiss();
            });
            chatOptionBottomSheetDialog.show();
        });
//        ViewerProfileDetailsApiCall(); // call on resume


        //replay selection
        cancelButton = findViewById(R.id.cancelButton);
        reply_layout = findViewById(R.id.reply_layout);
        textQuotedMessage = findViewById(R.id.textQuotedMessage);

        //open attach & send message
        btnFileGallery = findViewById(R.id.btnFileGallery);
        btnFileGallery.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.common_option_dialog_layout);

            LinearLayout pick_img_LL = bottomSheetDialog.findViewById(R.id.pick_img_LL),
                    pick_video_LL = bottomSheetDialog.findViewById(R.id.pick_video_LL),
                    pick_doc_LL = bottomSheetDialog.findViewById(R.id.pick_doc_LL);

            pick_img_LL.setVisibility(View.VISIBLE);
            pick_video_LL.setVisibility(View.VISIBLE);
            pick_doc_LL.setVisibility(View.VISIBLE);

            pick_img_LL.setOnClickListener(v1 -> {
                activityResultLauncherForPhoto.launch(("image/*"));
                bottomSheetDialog.dismiss();
            });
            pick_video_LL.setOnClickListener(v1 -> {
                activityResultLauncherForVideo.launch(("video/*"));
                bottomSheetDialog.dismiss();
            });
            pick_doc_LL.setOnClickListener(v1 -> {
                activityResultLauncherForDoc.launch(("*/*"));
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });
        message_ed_txt = findViewById(R.id.message_ed_txt);
        if (message != null && !message.equalsIgnoreCase("")) {
            message_ed_txt.setText(message);
        }
        message_ed_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                typingOnlineListener.typing();
            }
        });

        send_message_btn = findViewById(R.id.send_message_btn);
        send_message_btn.setOnClickListener(view -> {
            if (!iAmBlock) {
                if (message_ed_txt.getText().toString().trim().equals("")) {
                    message_ed_txt.setError("Please enter message");
                    Toast.makeText(ChatActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                } else {
                    // send message
                    sendChatMessage(message_ed_txt.getText().toString(), "", "TEXT", null);
                    message_ed_txt.setText("");
                }
            } else {
                Toast.makeText(this, "you are block by this user", Toast.LENGTH_LONG).show();
            }
        });
        txtBlock = findViewById(R.id.txtBlock);

        message_list = findViewById(R.id.message_list);
        date_area = findViewById(R.id.date_area);
        date = findViewById(R.id.date);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        message_list.setLayoutManager(layoutManager);
        message_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (tableChatDatas.size() == 0)
                    return;

                date_area.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = (LinearLayoutManager) message_list.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                String dateFormate = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMMM yyyy", tableChatDatas.get(lastVisibleItemPosition).msgSendTime);
                date.setText(dateFormate);

//              Log.e(LogTag.CHECK_DEBUG, " & firstVisibleItemPosition : " + firstVisibleItemPosition + " & visibleItemCount : " + visibleItemCount + " &  totalItemCount : "+totalItemCount + " &  scroll view Y : " + dy + " & start list  : " + start);
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    if (scrollFlg) {
                        scrollFlg = false;
                        Log.e(LogTag.CHECK_DEBUG, "Add next data : " + start);
                        tableChatData(start);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                scrollFlg = true;
                            }
                        }, 1000);
                    }

                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                date_area.setVisibility(View.GONE);
            }

        });

        tableChatData(start);

        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                tableChatData(0);
            }
        });
    }

    ArrayList<MessageListResponse.Dt> tableChatDatas = new ArrayList<>();
    int preArraySize = 0;
    long start = 0;
    boolean scrollFlg = true;
    boolean iAmBlock = false;

    BroadcastReceiver msgReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chat_master_id = intent.getExtras().getString("chat_master_id");
            String rec_user_master_id = intent.getExtras().getString("rec_user_master_id");
            String send_user_master_id = intent.getExtras().getString("send_user_master_id");
            String pushJsonString = intent.getExtras().getString("pushJson");//normal format
            Log.e(LogTag.CHECK_DEBUG, "Message received call Broadcast : " + chat_master_id);
            if (ChatActivity.this.user_master_id.equals(send_user_master_id)) {
                if (pushJsonString != null) {
                    SendMessageResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, SendMessageResponse.PushJson.class);
                    MessageListResponse.Dt tableChatData = new MessageListResponse().new Dt();
                    tableChatData.chatMasterId = pushJson.chatData.chatMasterId;
                    tableChatData.sendUserMasterId = pushJson.chatData.sendUserMasterId;
                    tableChatData.recUserMasterId = pushJson.chatData.recUserMasterId;
                    tableChatData.msgType = pushJson.chatData.msgType;
                    tableChatData.msg = pushJson.chatData.msg;
                    tableChatData.msgFile = pushJson.chatData.msgFile;
                    tableChatData.fileType = pushJson.chatData.fileType;
                    tableChatData.msgSendTime = pushJson.chatData.msgSendTime;
                    tableChatData.msgStatus = pushJson.chatData.msgStatus;
                    tableChatData.msgError = "";
                    tableChatDatas.add(0, tableChatData);
                    if (message_list != null) {
                        if (message_list.getAdapter() != null) {
                            message_list.smoothScrollToPosition(0);
                            message_list.getAdapter().notifyDataSetChanged();
                            updateStatusRead(tableChatData.chatMasterId);
                        }
                    }
                }
            }
        }
    };

    BroadcastReceiver msgReadDelivered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chat_master_id = intent.getExtras().getString("chat_master_id");
            String pushJsonString = intent.getExtras().getString("pushJson");
            try {
                if (pushJsonString != null)
                    if (message_list != null) {
                        if (message_list.getAdapter() != null) {
                            int index = chatModule.findIndexOfForMessage(tableChatDatas, chat_master_id);
                            if (index > -1) {
                                MessageStatusUpdateResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, MessageStatusUpdateResponse.PushJson.class);
                                MessageListResponse.Dt tableChatData = tableChatDatas.get(index);
                                if (!pushJson.chatData.msgStatus.equalsIgnoreCase("ERROR"))
                                    tableChatData.msgError = "";
                                tableChatData.msgStatus = pushJson.chatData.msgStatus;
                                tableChatData.msgDeliverTime = pushJson.chatData.statusTime;
                                tableChatDatas.set(index, tableChatData);
                                message_list.getAdapter().notifyDataSetChanged();

                                Log.e(LogTag.CHECK_DEBUG, "Call notify read/Delivery Time " + pushJson.chatData.statusTime);
                            }
                        }
                    }
            } catch (Exception e) {
                Log.e(LogTag.EXCEPTION, "Delivery status Exception", e);
            }
        }
    };

    BroadcastReceiver statusChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String user_master_id = intent.getExtras().getString("user_master_id");
            String pushJsonString = intent.getExtras().getString("pushJson");
            Log.e(LogTag.CHECK_DEBUG, "statusChange call Broadcast ");
            if (user_master_id.equals(ChatActivity.this.user_master_id)) {
                if (pushJsonString != null) {
                    UserStatusUpdateResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, UserStatusUpdateResponse.PushJson.class);
                    if (pushJson.chatData.status.equalsIgnoreCase("offline")) {
                        Date statusDatetime = DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", pushJson.chatData.statusTime);
                        String stTxt = "last seen ";
                        if (DateUtils.getStringDate("yyyy-MM-dd", new Date()).equals(DateUtils.getStringDate("yyyy-MM-dd", statusDatetime))) {
                            stTxt += DateUtils.getStringDate("hh:mm a", statusDatetime);
                        } else {
                            stTxt += DateUtils.getStringDate("dd MMM, hh:mm a", statusDatetime);
                        }
                        statusTxt.setText(stTxt);
                    } else {
                        statusTxt.setText(pushJson.chatData.status);
                    }
                }
            }
        }
    };

    BroadcastReceiver fileUploadProcessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chat_master_id = intent.getExtras().getString("chat_master_id");
            int num = intent.getExtras().getInt("process");
            Log.e(LogTag.TMP_LOG, "process upload" + num);

            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(num + "/" + 100);
                    myHolder.progress_circular.setProgress(num);
                }
            });*/
        }
    };

    BroadcastReceiver fileCompleteUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chat_master_id = intent.getExtras().getString("chat_master_id");
            String pushJsonString = intent.getExtras().getString("pushJson");//normal format
            if (pushJsonString != null) {
                SendMessageResponse.PushJson pushJson = new Gson().fromJson(pushJsonString, SendMessageResponse.PushJson.class);
                MessageListResponse.Dt tableChatData = new MessageListResponse().new Dt();
                tableChatData.chatMasterId = pushJson.chatData.chatMasterId;
                tableChatData.sendUserMasterId = pushJson.chatData.sendUserMasterId;
                tableChatData.recUserMasterId = pushJson.chatData.recUserMasterId;
                tableChatData.msgType = pushJson.chatData.msgType;
                tableChatData.msg = pushJson.chatData.msg;
                tableChatData.msgFile = pushJson.chatData.msgFile;
                tableChatData.fileType = pushJson.chatData.fileType;
                tableChatData.msgSendTime = pushJson.chatData.msgSendTime;
                tableChatData.msgStatus = pushJson.chatData.msgStatus;
                if (pushJson.chatData.msgError != null) {
                    tableChatData.msgError = pushJson.chatData.msgError;
                }
                if (message_list != null) {
                    if (message_list.getAdapter() != null) {
                        int index = chatModule.findIndexOfForMessage(tableChatDatas, chat_master_id);
                        if (index > -1) {
                            tableChatDatas.set(index, tableChatData);
                            message_list.getAdapter().notifyItemChanged(index);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilterReceived = new IntentFilter(ChatModule.MSG_RECEIVED_FILTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(msgReceived, intentFilterReceived, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(msgReceived, intentFilterReceived);
        }
        IntentFilter intentFilterReadDelivered = new IntentFilter();
        intentFilterReadDelivered.addAction(ChatModule.MSG_DELIVERED_FILTER);
        intentFilterReadDelivered.addAction(ChatModule.MSG_READ_FILTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(msgReadDelivered, intentFilterReadDelivered, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(msgReadDelivered, intentFilterReadDelivered);
        }
        IntentFilter statusUpdate = new IntentFilter(ChatModule.CHAT_STATUS_UPDATE_FILTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(statusChange, statusUpdate, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(statusChange, statusUpdate);
        }

        IntentFilter process = new IntentFilter(FileMessageUploadService.UPLOAD_FILE_PROCESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(fileUploadProcessReceiver, process, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(fileUploadProcessReceiver, process);
        }

        IntentFilter complete = new IntentFilter(FileMessageUploadService.UPLOAD_FILE_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(fileCompleteUploadReceiver, complete, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(fileCompleteUploadReceiver, complete);
        }
        ViewerProfileDetailsApiCall();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(msgReceived);
        unregisterReceiver(msgReadDelivered);
        unregisterReceiver(statusChange);

        unregisterReceiver(fileUploadProcessReceiver);
        unregisterReceiver(fileCompleteUploadReceiver);
    }

    private void tableChatData(long start) {
        if (start == 0) {
            tableChatDatas.clear();
        }
        preArraySize = tableChatDatas.size();
        chatModule.getCusMessage(user_master_id, start, new ChatModule.MessageListCallBack() {
            @Override
            public void callBack(MessageListResponse messageListResponse) {
                if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
                    swipe_refresh.setRefreshing(false);
                }

                chatImgPath = messageListResponse.chatImgPath;
                if (messageListResponse.dt.size() > 0 && start == 0) {
                    if (!messageListResponse.dt.get(0).msgStatus.equalsIgnoreCase("READ")) {
                        updateStatusRead(messageListResponse.dt.get(0).chatMasterId);//update all message as read
                    }
                }
                tableChatDatas.addAll(messageListResponse.dt);

                if (start == 0) {
                    message_list.setAdapter(new RecyclerView.Adapter<myHolder>() {

                        @NonNull
                        @Override
                        public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.row_chat_msg_data, parent, false);
                            return new myHolder(view);
                        }

                        @Override
                        public void onBindViewHolder(@NonNull myHolder holder, int position) {
                            MessageListResponse.Dt tableChatData = tableChatDatas.get(position);
                            String direction_Type = "received";
                            if (tableChatData.sendUserMasterId.equalsIgnoreCase(sessionPref.getUserMasterId())) {
                                direction_Type = "send";
                            }

                            holder.received_msg_area.setVisibility(View.GONE);
                            holder.received_replay_msg_area.setVisibility(View.GONE);

                            holder.video_file_area.setVisibility(View.GONE);
                            holder.photo_thumb_layout.setVisibility(View.GONE);
                            holder.photo_thumb.setVisibility(View.GONE);

                            //scroll the show in uploading process
                            holder.ic_upload.setVisibility(View.GONE);
                            holder.progress_circular.setVisibility(View.GONE);

                            holder.send_replay_msg_area.setVisibility(View.GONE);
                            holder.send_msg_area.setVisibility(View.GONE);

                            String dateFormate = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMMM yyyy", tableChatData.msgSendTime);
                            holder.date_status.setText(dateFormate);

                            if (position < (tableChatDatas.size() - 1)) {
                                String preDateFormate = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMMM yyyy", tableChatDatas.get(position + 1).msgSendTime);
                                if (dateFormate.equalsIgnoreCase(preDateFormate)) {
                                    holder.date_status_area.setVisibility(View.GONE);
                                } else {
                                    holder.date_status_area.setVisibility(View.VISIBLE);
                                }
                            } else {
                                holder.date_status_area.setVisibility(View.VISIBLE);
                            }

                            //time_status_area
                            LinearLayout.LayoutParams linearLayout = (LinearLayout.LayoutParams) holder.time_status_area.getLayoutParams();
                            holder.time_text.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "hh:mm a", tableChatData.msgSendTime));

                            if (direction_Type.equalsIgnoreCase("send")) {
                                //send message
                                if (tableChatData.replayMsgId != null && !tableChatData.replayMsgId.equalsIgnoreCase("0")) {
                                    //replay type
                                    holder.send_replay_msg_area.setVisibility(View.VISIBLE);
                                } else {
                                    //non replay type
                                    if (tableChatData.msgType.equalsIgnoreCase("TEXT")) {
                                        //text
                                        holder.send_msg_area.setVisibility(View.VISIBLE);
                                        holder.send_msg.setText(tableChatData.msg);
                                        holder.ic_upload.setVisibility(View.GONE);
                                        holder.progress_circular.setVisibility(View.GONE);
                                    } else if (tableChatData.msgType.equals("FILE")) {
                                        //file
                                        if (tableChatData.msgFile != null && !tableChatData.msgFile.trim().equals("") || (tableChatData.msgError != null && tableChatData.msgError.equals("wait"))) {
                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.photo_thumb_layout.getLayoutParams();
                                            layoutParams.gravity = Gravity.RIGHT;
                                            holder.photo_thumb_layout.setLayoutParams(layoutParams);

                                            LinearLayout.LayoutParams layoutParamsVideo = (LinearLayout.LayoutParams) holder.video_file_area.getLayoutParams();
                                            layoutParamsVideo.gravity = Gravity.RIGHT;
                                            holder.video_file_area.setLayoutParams(layoutParamsVideo);


                                            Log.d(LogTag.CHECK_DEBUG, "fileType " + tableChatData.fileType);
                                            if (tableChatData.msgError != null && tableChatData.msgError.equals("wait") || tableChatData.msgError.contains("wait progress")) {
                                                //setting remain
                                                holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setPadding(4, 4, 4, 4);
                                                holder.ic_upload.setVisibility(View.VISIBLE);
                                                holder.overlay_img.setVisibility(View.VISIBLE);
                                                holder.progress_circular.setVisibility(View.VISIBLE);
                                                String num = "0";
                                                if (!tableChatData.msgError.equals("wait")) {
                                                    String arr[] = tableChatData.msgError.split(" ");
                                                    num = arr[arr.length - 1];
                                                    holder.progress_circular.setProgress(Integer.parseInt(num));
                                                    Log.d(LogTag.CHECK_DEBUG, "FILE process" + arr);
                                                }
                                                if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.VIDEO.getVal())) {
                                                    holder.photo_thumb.setImageResource(R.drawable.file_earmark_play_video);
                                                } else if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.DOC.getVal())) {
                                                    holder.photo_thumb.setImageResource(ChatModule.getDocFileResource(tableChatData.msgFile));
                                                }
                                            } else if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.VIDEO.getVal())) {
                                                holder.video_file_area.setVisibility(View.VISIBLE);

                                                long thumb = 150 * 150;
                                                RequestOptions options = new RequestOptions().frame(thumb);
                                                Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).apply(options).into(holder.video_thumb);

                                                holder.video_file_area.setOnClickListener(v -> {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.parse(chatImgPath + tableChatData.msgFile), "video/*");
                                                    startActivity(intent);
                                                });

                                            } else if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.IMAGE.getVal())) {
                                                holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setPadding(0, 0, 0, 0);
                                                holder.overlay_img.setVisibility(View.GONE);
                                                Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).into(holder.photo_thumb);

                                                holder.photo_thumb_layout.setOnClickListener(v -> {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.parse(chatImgPath + tableChatData.msgFile), "image/*");
                                                    startActivity(intent);
                                                });
                                            } else {//DOC file
                                                holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setPadding(4, 4, 4, 4);
                                                holder.photo_thumb.setImageResource(ChatModule.getDocFileResource(tableChatData.msgFile));

                                                holder.photo_thumb_layout.setOnClickListener(v -> {
                                                    try {
                                                        String mimeType = FilePath.getMimeType(tableChatData.msgFile);
                                                        mimeType = ChatModule.getDocFileOpenType(tableChatData.msgFile);
//                                                        mimeType= URLConnection.guessContentTypeFromName(tableChatData.msgFile);
                                                        Log.d(LogTag.CHECK_DEBUG, "Document file open mime type :" + mimeType);
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(Uri.parse(chatImgPath + tableChatData.msgFile), mimeType != null ? mimeType : "*/*");
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    } catch (Exception e) {
                                                        Toast.makeText(context, "file open application not found", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent();
                                                        intent.setAction(Intent.ACTION_VIEW);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.addCategory("android.intent.category.DEFAULT");
                                                        intent.setData(Uri.parse(chatImgPath + tableChatData.msgFile));
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                                //time_status_area
                                linearLayout.gravity = Gravity.RIGHT;
                                holder.status_img.setVisibility(View.VISIBLE);
                                if (tableChatData.msgError != null && tableChatData.msgError.equals("wait")) {
                                    holder.status_img.setImageResource(R.drawable.feeds_time);
                                } else if (tableChatData.msgError != null && !tableChatData.msgError.equals("wait") && !tableChatData.msgError.trim().equals("")) {
                                    holder.status_img.setImageResource(R.drawable.report_item);
                                } else if (tableChatData.msgStatus.equalsIgnoreCase("READ")) {
                                    holder.status_img.setImageResource(R.drawable.ic_read_msg);
                                } else if (tableChatData.msgStatus.equalsIgnoreCase("DELIVER")) {
                                    holder.status_img.setImageResource(R.drawable.ic_delivered_msg);
                                } else {
                                    holder.status_img.setImageResource(R.drawable.ic_send_msg);
                                }
                            } else {
                                if (tableChatData.msgStatus.equalsIgnoreCase("ERROR")) {
                                    holder.time_text.setVisibility(View.GONE);
                                } else {
                                    holder.time_text.setVisibility(View.VISIBLE);
                                    //received message
                                    if (tableChatData.replayMsgId != null && !tableChatData.replayMsgId.equalsIgnoreCase("0")) {
                                        //replay type
                                        holder.received_replay_msg_area.setVisibility(View.VISIBLE);
                                    } else {
                                        //non replay type
                                        if (tableChatData.msgType.equalsIgnoreCase("TEXT")) {
                                            //text
                                            holder.received_msg_area.setVisibility(View.VISIBLE);
                                            holder.received_msg.setText(tableChatData.msg);
                                        } else if (tableChatData.msgType.equalsIgnoreCase("FILE")) {
                                            //file
                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.photo_thumb_layout.getLayoutParams();
                                            layoutParams.gravity = Gravity.LEFT;
                                            holder.photo_thumb_layout.setLayoutParams(layoutParams);

                                            LinearLayout.LayoutParams layoutParamsVideo = (LinearLayout.LayoutParams) holder.video_file_area.getLayoutParams();
                                            layoutParamsVideo.gravity = Gravity.LEFT;
                                            holder.video_file_area.setLayoutParams(layoutParamsVideo);
                                            if (tableChatData.msgFile != null && !tableChatData.msgFile.trim().equals("")) {

                                                if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.VIDEO.getVal())) {
                                                    holder.video_file_area.setVisibility(View.VISIBLE);
                                                    Log.e(LogTag.TMP_LOG, "file path " + tableChatData.msgFile);
                                                    long thumb = 150 * 150;
                                                    RequestOptions options = new RequestOptions().frame(thumb);
                                                    Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).apply(options).into(holder.video_thumb);

                                                    holder.video_file_area.setOnClickListener(v -> {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(Uri.parse(chatImgPath + tableChatData.msgFile), "video/*");
                                                        startActivity(intent);
                                                    });


                                                } else if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.IMAGE.getVal())) {
                                                    holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                    holder.photo_thumb.setVisibility(View.VISIBLE);
                                                    holder.photo_thumb.setPadding(0, 0, 0, 0);
                                                    holder.overlay_img.setVisibility(View.GONE);
                                                    Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).into(holder.photo_thumb);
                                                    holder.photo_thumb_layout.setOnClickListener(v -> {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(Uri.parse(chatImgPath + tableChatData.msgFile), "image/*");
                                                        startActivity(intent);
                                                    });

                                                } else {//DOC
                                                    holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                    holder.photo_thumb.setVisibility(View.VISIBLE);
                                                    holder.photo_thumb.setPadding(4, 4, 4, 4);
                                                    holder.photo_thumb.setImageResource(ChatModule.getDocFileResource(tableChatData.msgFile));

                                                    holder.photo_thumb_layout.setOnClickListener(v -> {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(Uri.parse(chatImgPath + tableChatData.msgFile), ChatModule.getDocFileOpenType(tableChatData.msgFile));
                                                        startActivity(intent);
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }
                                //time_status_area
                                linearLayout.gravity = Gravity.LEFT;
                                holder.status_img.setVisibility(View.GONE);

                            }

//                    holder.send_msg_area.setOnTouchListener(ChatActivity.this);
//                    holder.send_msg_area.setOnDragListener(ChatActivity.this);
                        }

                        @Override
                        public int getItemCount() {
                            return tableChatDatas.size();
                        }

                    });
                } else {
                    if (preArraySize != tableChatDatas.size()) {
//                message_list.getAdapter().notifyItemRangeInserted(preArraySize, DatabaseHelper.ROW_LIMIT);
                        message_list.post(new Runnable() {
                            public void run() {
                                message_list.getAdapter().notifyDataSetChanged();
                            }
                        });
                    }
                }
                ChatActivity.this.start = start + ChatModule.MSG_ROW_LIMIT;
            }
        });

    }

    class myHolder extends RecyclerView.ViewHolder {
        MaterialCardView received_msg_area, received_replay_msg_area;
        TextView received_msg, received_replay_msg, replay_msg_received;
        MaterialCardView photo_thumb_layout, video_file_area;
        ImageView video_thumb, photo_thumb, ic_upload;
        MaterialCardView send_replay_msg_area, send_msg_area;
        TextView send_replay_msg, replay_msg_send, send_msg;
        ProgressBar progress_circular;
        View overlay_img;
        TextView time_text, date_status;
        ImageView status_img;
        LinearLayout time_status_area;
        MaterialCardView date_status_area;

        public myHolder(@NonNull View row_chat_msg_data) {
            super(row_chat_msg_data);

            received_msg_area = row_chat_msg_data.findViewById(R.id.received_msg_area);
            received_replay_msg_area = row_chat_msg_data.findViewById(R.id.received_replay_msg_area);

            received_msg = row_chat_msg_data.findViewById(R.id.received_msg);
            received_replay_msg = row_chat_msg_data.findViewById(R.id.received_replay_msg);
            replay_msg_received = row_chat_msg_data.findViewById(R.id.replay_msg_received);

            video_file_area = row_chat_msg_data.findViewById(R.id.video_file_area);
            photo_thumb_layout = row_chat_msg_data.findViewById(R.id.photo_thumb_layout);

            video_thumb = row_chat_msg_data.findViewById(R.id.video_thumb);
            photo_thumb = row_chat_msg_data.findViewById(R.id.photo_thumb);
            overlay_img = row_chat_msg_data.findViewById(R.id.overlay_img);
            progress_circular = row_chat_msg_data.findViewById(R.id.progress_circular);
            ic_upload = row_chat_msg_data.findViewById(R.id.ic_upload);

            send_replay_msg_area = row_chat_msg_data.findViewById(R.id.send_replay_msg_area);
            send_msg_area = row_chat_msg_data.findViewById(R.id.send_msg_area);

            send_replay_msg = row_chat_msg_data.findViewById(R.id.send_replay_msg);
            replay_msg_send = row_chat_msg_data.findViewById(R.id.replay_msg_send);
            send_msg = row_chat_msg_data.findViewById(R.id.send_msg);

            time_status_area = row_chat_msg_data.findViewById(R.id.time_status_area);
            time_text = row_chat_msg_data.findViewById(R.id.time_text);
            status_img = row_chat_msg_data.findViewById(R.id.status_img);
            date_status = row_chat_msg_data.findViewById(R.id.date_status);
            date_status_area = row_chat_msg_data.findViewById(R.id.date_status_area);
        }
    }

    TypingOnlineListener typingOnlineListener = new TypingOnlineListener() {
        @Override
        public void online() {
            chatModule.chatStatusApiCall("online");
            Log.d(LogTag.CHECK_DEBUG, "update chat status Online by api");
        }

        @Override
        public void typing() {
            if (!this.isTyping) {
                chatModule.chatStatusApiCall("typing");
                Log.d(LogTag.CHECK_DEBUG, "update chat status typing by api");
            }
            super.typing();
        }
    };

    private void sendChatMessage(String msg, String fileUrl, String msg_type, String fileType) {
        //insert
        MessageListResponse.Dt tableChatData = new MessageListResponse().new Dt();
        tableChatData.sendUserMasterId = sessionPref.getUserMasterId();
        tableChatData.recUserMasterId = user_master_id;
        tableChatData.msgType = msg_type;
        tableChatData.msgSendTime = DateUtils.TODAYDATETIMEforDB();
        tableChatData.msgStatus = "ERROR";
        tableChatData.msgError = "wait";
        if (msg_type.equalsIgnoreCase("TEXT")) {
            tableChatData.msg = msg;
        } else {//FILE
            tableChatData.msgFile = fileUrl;
            if (fileType != null) {
                tableChatData.fileType = fileType;
            } else {
                tableChatData.fileType = CommonFunction.fileType(fileUrl);
            }
        }
        tableChatDatas.add(0, tableChatData);
        if (message_list != null) {
            if (message_list.getAdapter() != null) {
                message_list.smoothScrollToPosition(0);
                message_list.getAdapter().notifyDataSetChanged();
            }
        }

        HashMap hashMap = new HashMap();
        hashMap.putAll(defaultUserData);
        hashMap.put("rec_user_master_id", user_master_id);
        hashMap.put("msg_type", msg_type);
        if (msg_type.equalsIgnoreCase("TEXT")) {
            hashMap.put("msg", msg);
        } else {
            hashMap.put("file_type", tableChatData.fileType);
        }
        apiInterface.MESSAGE_SEND(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        SendMessageResponse sendMessageResponse = (SendMessageResponse) response.body();
                        if (sendMessageResponse.status) {
                            FirebaseFCMResponse firebaseFCMResponse = new Gson().fromJson(sendMessageResponse.fcmResponse, FirebaseFCMResponse.class);
                            if (message_list != null) {
                                if (message_list.getAdapter() != null) {
                                    int index = tableChatDatas.indexOf(tableChatData);
//                                    Toast.makeText(context, "index : " + index, Toast.LENGTH_LONG).show();
                                    tableChatData.msgStatus = sendMessageResponse.pushJson.chatData.msgStatus;
                                    if (!tableChatData.msgStatus.equalsIgnoreCase("ERROR"))
                                        tableChatData.msgError = "";
                                    tableChatData.chatMasterId = String.valueOf(sendMessageResponse.chatMasterId);
                                    tableChatData.msgSendTime = sendMessageResponse.pushJson.chatData.msgSendTime;
                                    tableChatDatas.set(index, tableChatData);
                                    message_list.getAdapter().notifyItemChanged(index);
                                    if (msg_type.equalsIgnoreCase("FILE")) {
                                        uploadFileProcess(tableChatData.chatMasterId, tableChatData.fileType, tableChatData.msgFile);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void uploadFileProcess(String chat_master_id, String fileType, String
            msgFileLocalUri) {
        try {
            List<WorkInfo> workInfos = workManager.getWorkInfosByTag(chat_master_id).get();
            WorkInfo.State state = workInfos != null && workInfos.size() > 0 ? workInfos.get(0).getState() : null;
            if (state != null) {
                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED) {
                    Toast.makeText(context, "cannot start upload. a upload is already in progress", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        } catch (Exception e) {
            Log.e(LogTag.EXCEPTION, "quality Workinfo exception ", e);
        }
        Data.Builder builder = new Data.Builder();
        builder.putString("chat_master_id", chat_master_id);
        builder.putString("fileType", fileType);
        builder.putString("msgFileLocalUri", msgFileLocalUri);

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(FileMessageUploadService.class)
                .addTag(chat_master_id)
                .setInputData(builder.build())
                .build();

        workManager.enqueue(oneTimeWorkRequest);
    }

    ExoPlayer player;

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
        }
    }

    private void uploadFileDialog(String fileType, Uri fileUri) {
        Dialog sendFileDialog = new Dialog(this);
        sendFileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sendFileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sendFileDialog.setContentView(R.layout.photo_video_send_dailog);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        sendFileDialog.getWindow().setLayout(width, height);


        ImageView closeDailog = sendFileDialog.findViewById(R.id.closeDailog);
        TextView file_name = sendFileDialog.findViewById(R.id.file_name);
        TextView fileTitle = sendFileDialog.findViewById(R.id.fileTitle);
        ImageView imgSendBtnDialog = sendFileDialog.findViewById(R.id.imgSendBtnDialog);
        String filePath = FilePath.getPath2(context, fileUri);
        if (fileType.equalsIgnoreCase(ChatModule.FileType.VIDEO.getVal())) {
            StyledPlayerView video = sendFileDialog.findViewById(R.id.video_play);
            video.setVisibility(View.VISIBLE);
            fileTitle.setText("Video");
            player = new ExoPlayer.Builder(context).build();
            player.setPlayWhenReady(true);
            video.setPlayer(player);
            // Create and add media item
            MediaItem mediaItem = MediaItem.fromUri(fileUri);
            player.addMediaItem(mediaItem);
            // Prepare exoplayer
            player.prepare();
            sendFileDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (player != null && player.isPlaying()) {
                        player.stop();
                        player.release();
                    }
                }
            });
        } else if (fileType.equalsIgnoreCase(ChatModule.FileType.IMAGE.getVal())) {
            ImageView imgSelectShowDialog = sendFileDialog.findViewById(R.id.imgSelectShowDialog);
            imgSelectShowDialog.setVisibility(View.VISIBLE);
            fileTitle.setText("Image");
            Glide.with(this).load(fileUri).into(imgSelectShowDialog);
        } else { //DOC
            ImageView imgSelectShowDialog = sendFileDialog.findViewById(R.id.imgSelectShowDialog);
            imgSelectShowDialog.setVisibility(View.VISIBLE);
            fileTitle.setText("Document");
            imgSelectShowDialog.setImageResource(R.drawable.file_earmark_document);
            if (DocumentsContract.isDocumentUri(this, fileUri)) {
                FileUtils fileUtils = new FileUtils(context);
                filePath = fileUtils.getPath(fileUri);
            }
        }
//        file_name.setText(filePath);
        Log.e(LogTag.TMP_LOG, "File path : " + fileUri);
        String finalFilePath = filePath;
        imgSendBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileUri != null) {
                    sendChatMessage("", finalFilePath, "FILE", fileType);
                } else {
                    Toast.makeText(ChatActivity.this, "File Selected", Toast.LENGTH_SHORT).show();
                }
                sendFileDialog.dismiss();
            }
        });

        closeDailog.setOnClickListener(v ->
                sendFileDialog.dismiss());

        sendFileDialog.show();
    }

    private void updateStatusRead(String chat_master_id) {
        HashMap hashMap = new HashMap();
        hashMap.putAll(defaultUserData);
        hashMap.put("chat_master_id", chat_master_id);

        apiInterface.MESSAGE_STATUS_READ(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MessageStatusUpdateResponse messageStatusUpdateResponse = (MessageStatusUpdateResponse) response.body();
                        if (messageStatusUpdateResponse.status) {
//                            FirebaseFCMResponse firebaseFCMResponse = new Gson().fromJson(messageStatusUpdateResponse.fcmResponse, FirebaseFCMResponse.class);

                            Log.d(LogTag.CHECK_DEBUG, "Message Read Status : " + messageStatusUpdateResponse.status);
                        }
                    }
                }
            }
        });

    }

    private void ViewerProfileDetailsApiCall() {
        userMaster.getUserClmData(new UserMaster.CallBack() {
            @Override
            public void DataCallBack(Response response) {
                UserRowResponse userRowResponse = (UserRowResponse) response.body();
                if (userRowResponse.status) {
                    userDt = userRowResponse.dt;
                    imgPath = userRowResponse.imgPath;
                    try {
                        Glide.with(context).load(imgPath + userDt.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(profile_pic);
                        name.setText(userDt.name);
                        statusTxt.setText(userDt.chatStatus);
                        if (userDt.chatStatus.equalsIgnoreCase("Offline") && userDt.chatStatusTime != null) {
                            Date statusDatetime = DateUtils.getObjectDate("yyyy-MM-dd HH:mm:ss", userDt.chatStatusTime);
                            String stTxt = "last seen ";
                            if (DateUtils.getStringDate("yyyy-MM-dd", new Date()).equals(DateUtils.getStringDate("yyyy-MM-dd", statusDatetime))) {
                                stTxt += DateUtils.getStringDate("hh:mm a", statusDatetime);
                            } else {
                                stTxt += DateUtils.getStringDate("dd MMM, hh:mm a", statusDatetime);
                            }
                            statusTxt.setText(stTxt);
                        }
                        if (userDt.blockedUser != null && !userDt.blockedUser.equalsIgnoreCase(""))
                            iAmBlock = UserMaster.findIdInIds(sessionPref.getUserMasterId(), userDt.blockedUser);
                    } catch (Exception e) {
                        Log.e(LogTag.EXCEPTION, "Profile set Exception", e);
                    }
                }
            }
        }, "name,user_name,profile_link,profile_pic,position,chat_status,chat_status_time,blocked_user", false, user_master_id);
    }

    //if require cancel
    private void cancelUploadFile(String chatMasterId) {
        workManager.cancelAllWorkByTag(chatMasterId);
        notificationManager.cancel(Integer.parseInt(chatMasterId));
        //call api message error
    }
}