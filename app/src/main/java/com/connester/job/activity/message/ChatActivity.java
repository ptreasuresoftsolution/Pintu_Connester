package com.connester.job.activity.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.MessageListResponse;
import com.connester.job.RetrofitConnection.jsontogson.UserRowResponse;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
import com.connester.job.function.DateUtils;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.connester.job.module.notification_message.ChatModule;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        userMaster = new UserMaster(context);
        chatModule = new ChatModule(context, activity);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

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

        initView();

    }

    EditText message_ed_txt;
    ImageView back, profile_pic, btnFileGallery, chatOption, send_message_btn;
    LinearLayout chat_profile;
    TextView name, statusTxt, txtBlock, date;
    MaterialCardView date_area;
    RecyclerView message_list;
    LinearLayout reply_layout;
    TextView textQuotedMessage;
    ImageButton cancelButton;

    BottomSheetDialog bottomSheetDialog;

    private void initView() {
        chat_profile = findViewById(R.id.btn_chat_profile);
        View.OnClickListener openProfile = v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user_master_id", user_master_id);
            startActivity(intent);
            if (bottomSheetDialog != null && bottomSheetDialog.isShowing())
                bottomSheetDialog.dismiss();
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
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.common_option_dialog_layout);

            LinearLayout profile_view_LL = bottomSheetDialog.findViewById(R.id.profile_view_LL),
                    delete_conversation_LL = bottomSheetDialog.findViewById(R.id.delete_conversation_LL),
                    block_user_LL = bottomSheetDialog.findViewById(R.id.block_user_LL),
                    report_LL = bottomSheetDialog.findViewById(R.id.report_LL);

            profile_view_LL.setVisibility(View.VISIBLE);
            delete_conversation_LL.setVisibility(View.VISIBLE);
            block_user_LL.setVisibility(View.VISIBLE);
            report_LL.setVisibility(View.VISIBLE);

            profile_view_LL.setOnClickListener(openProfile);
            delete_conversation_LL.setOnClickListener(v1 -> {
                //call chat clear api

                if (message_list != null) {
                    message_list.getAdapter().notifyDataSetChanged();
                }
                bottomSheetDialog.dismiss();
            });
            block_user_LL.setOnClickListener(v1 -> {
                //call block member api

                bottomSheetDialog.dismiss();
            });
            report_LL.setOnClickListener(v1 -> {
                //call user chat report

                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });
        userMaster.getUserClmData(new UserMaster.CallBack() {
            @Override
            public void DataCallBack(Response response) {
                UserRowResponse userRowResponse = (UserRowResponse) response.body();
                if (userRowResponse.status) {
                    userDt = userRowResponse.dt;
                    imgPath = userRowResponse.imgPath;

                    Glide.with(context).load(imgPath + userDt.profilePic).centerCrop().placeholder(R.drawable.default_user_pic).into(profile_pic);
                    name.setText(userDt.name);
                    statusTxt.setText(userDt.chatStatus);
                }
            }
        }, "name,user_name,profile_link,profile_pic,position,chat_status,blocked_user", true, user_master_id);

        //replay selection
        cancelButton = findViewById(R.id.cancelButton);
        reply_layout = findViewById(R.id.reply_layout);
        textQuotedMessage = findViewById(R.id.textQuotedMessage);

        //open attach & send message
        btnFileGallery = findViewById(R.id.btnFileGallery);
        message_ed_txt = findViewById(R.id.message_ed_txt);
        send_message_btn = findViewById(R.id.send_message_btn);
        txtBlock = findViewById(R.id.txtBlock);

        message_list = findViewById(R.id.message_list);
        date_area = findViewById(R.id.date_area);
        date = findViewById(R.id.date);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        message_list.setLayoutManager(layoutManager);
        tableChatData(0);
    }


    ArrayList<MessageListResponse.Dt> tableChatDatas = new ArrayList<>();
    int preArraySize = 0;

    long start = 0;

    private void tableChatData(long start) {
        if (start == 0) {
            tableChatDatas.clear();
        }
        preArraySize = tableChatDatas.size();
        chatModule.getCusMessage(user_master_id, start, new ChatModule.MessageListCallBack() {
            @Override
            public void callBack(MessageListResponse messageListResponse) {
                chatImgPath = messageListResponse.chatImgPath;
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
                                        if (tableChatData.msgFile != null && !tableChatData.msgFile.trim().equals("")) {
                                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.photo_thumb_layout.getLayoutParams();
                                            layoutParams.gravity = Gravity.RIGHT;
                                            holder.photo_thumb_layout.setLayoutParams(layoutParams);

                                            LinearLayout.LayoutParams layoutParamsVideo = (LinearLayout.LayoutParams) holder.video_file_area.getLayoutParams();
                                            layoutParamsVideo.gravity = Gravity.RIGHT;
                                            holder.video_file_area.setLayoutParams(layoutParamsVideo);


                                            Log.d(LogTag.CHECK_DEBUG, "fileType " + tableChatData.fileType);
                                            if (tableChatData.msgError.equals("wait") || tableChatData.msgError.contains("wait progress")) {
                                                //setting remain
                                                holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setVisibility(View.VISIBLE);
                                                holder.ic_upload.setVisibility(View.VISIBLE);
                                                holder.progress_circular.setVisibility(View.VISIBLE);
                                                String num = "0";
                                                if (!tableChatData.msgError.equals("wait")) {
                                                    String arr[] = tableChatData.msgError.split(" ");
                                                    num = arr[arr.length - 1];
                                                    holder.progress_circular.setProgress(Integer.parseInt(num));
                                                    Log.d(LogTag.CHECK_DEBUG, "FILE process" + arr);
                                                }
                                                Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).into(holder.photo_thumb);
                                            } else if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.VIDEO.getVal())) {
                                                holder.video_file_area.setVisibility(View.VISIBLE);

                                                long thumb = 150 * 150;
                                                RequestOptions options = new RequestOptions().frame(thumb);
                                                Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).apply(options).into(holder.video_thumb);
/*  //setting up open video file
                                                holder.video_file_area.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Dialog VideoDialog = new Dialog(ChatActivity.this);
                                                        VideoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        VideoDialog.setContentView(R.layout.view_video_full_screen);
                                                        VideoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                                                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        int height = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        VideoDialog.getWindow().setLayout(width, height);

                                                        video = VideoDialog.findViewById(R.id.video_full);

                                                        player = ExoPlayerFactory.newSimpleInstance(ChatActivity.this, new DefaultRenderersFactory(ChatActivity.this), new DefaultTrackSelector(), new DefaultLoadControl());
                                                        player.setPlayWhenReady(true);
                                                        cache = new SimpleCache(new File(getCacheDir(), "random" + tableChatData.file), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 50));// new okhttp3.Cache(new File("temp"),100*1024*1024);
                                                        MediaSource mediaSource = new ExtractorMediaSource.Factory(new CacheDataSourceFactory(cache, new DefaultHttpDataSourceFactory(Util.getUserAgent(ChatActivity.this, getPackageName())), CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)).createMediaSource(Uri.parse(tableChatData.file));
                                                        player.prepare(mediaSource);
                                                        video.setPlayer(player);
                                                        player.addListener(new Player.EventListener() {
                                                            @Override
                                                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                                                switch (playbackState) {
                                                                    case Player.STATE_READY:
                                                                        if (playWhenReady) {
                                                                            Log.i("Video Player", "State Ready " + tableChatData.file);
                                                                        }
                                                                        break;
                                                                    case Player.STATE_BUFFERING:
                                                                        Log.i("Video Player", "Buffering " + tableChatData.file);
                                                                        break;
                                                                }
                                                            }
                                                        });

                                                        VideoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                            @Override
                                                            public void onDismiss(DialogInterface dialog) {
                                                                if (player != null) {
                                                                    player.release();
                                                                    cache.release();
                                                                }
                                                            }
                                                        });

                                                        VideoDialog.show();
                                                    }
                                                });*/

                                            } else if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.IMAGE.getVal())) {
                                                holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setVisibility(View.VISIBLE);
                                                holder.overlay_img.setVisibility(View.GONE);
                                                Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).into(holder.photo_thumb);

                                                /*
                                                //setting up open image file
                                                holder.photo_thumb_layout.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Dialog ImageDialog = new Dialog(ChatActivity.this);
                                                        ImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        ImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                                                        ImageDialog.setContentView(R.layout.view_image_full_screen);
                                                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        int height = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        ImageDialog.getWindow().setLayout(width, height);

                                                        ImageView img = ImageDialog.findViewById(R.id.img);
                                                        Glide.with(ChatActivity.this).load(tableChatData.file).into(img);
                                                        ImageDialog.show();
                                                    }
                                                });*/
                                            } else {//DOC file

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
                                                /*
                                                //setting up open video file
                                                holder.video_file_area.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Dialog VideoDialog = new Dialog(ChatActivity.this);
                                                        VideoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        VideoDialog.setContentView(R.layout.view_video_full_screen);
                                                        VideoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                                                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        int height = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        VideoDialog.getWindow().setLayout(width, height);
                                                        video = VideoDialog.findViewById(R.id.video_full);

                                                        player = ExoPlayerFactory.newSimpleInstance(ChatActivity.this, new DefaultRenderersFactory(ChatActivity.this), new DefaultTrackSelector(), new DefaultLoadControl());
                                                        player.setPlayWhenReady(true);
                                                        cache = new SimpleCache(new File(getCacheDir(), "random" + tableChatData.file), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 50));// new okhttp3.Cache(new File("temp"),100*1024*1024);
                                                        MediaSource mediaSource = new ExtractorMediaSource.Factory(new CacheDataSourceFactory(cache, new DefaultHttpDataSourceFactory(Util.getUserAgent(ChatActivity.this, getPackageName())), CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)).createMediaSource(Uri.parse(tableChatData.file));
                                                        player.prepare(mediaSource);
                                                        video.setPlayer(player);
                                                        player.addListener(new Player.EventListener() {
                                                            @Override
                                                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                                                switch (playbackState) {
                                                                    case Player.STATE_READY:
                                                                        if (playWhenReady) {
                                                                            Log.i("Video Player", "State Ready " + tableChatData.file);
                                                                        }
                                                                        break;
                                                                    case Player.STATE_BUFFERING:
                                                                        Log.i("Video Player", "Buffering " + tableChatData.file);
                                                                        break;
                                                                }
                                                            }
                                                        });

                                                        VideoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                            @Override
                                                            public void onDismiss(DialogInterface dialog) {
                                                                if (player != null) {
                                                                    player.release();
                                                                    cache.release();
                                                                }
                                                            }
                                                        });

                                                        VideoDialog.show();
                                                    }
                                                });*/

                                            } else if (tableChatData.fileType.equalsIgnoreCase(ChatModule.FileType.IMAGE.getVal())) {
                                                holder.photo_thumb_layout.setVisibility(View.VISIBLE);
                                                holder.photo_thumb.setVisibility(View.VISIBLE);
                                                holder.overlay_img.setVisibility(View.GONE);
                                                Glide.with(ChatActivity.this).load(chatImgPath + tableChatData.msgFile).into(holder.photo_thumb);

                                                /*
                                                //setting up open image file
                                                holder.photo_thumb_layout.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Dialog ImageDialog = new Dialog(ChatActivity.this);
                                                        ImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        ImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                                                        ImageDialog.setContentView(R.layout.view_image_full_screen);
                                                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        int height = ViewGroup.LayoutParams.MATCH_PARENT;
                                                        ImageDialog.getWindow().setLayout(width, height);

                                                        ImageView img = ImageDialog.findViewById(R.id.img);
                                                        Glide.with(ChatActivity.this).load(tableChatData.file).into(img);
                                                        ImageDialog.show();
                                                    }
                                                });*/

                                            }else{//DOC

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



}