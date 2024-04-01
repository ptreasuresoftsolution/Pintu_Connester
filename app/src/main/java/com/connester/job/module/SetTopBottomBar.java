package com.connester.job.module;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.AllInOneSearchResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.NotificationJsonData;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.activity.FeedFullViewActivity;
import com.connester.job.activity.HomeActivity;
import com.connester.job.activity.JobsEvents_Activity;
import com.connester.job.activity.NetworkActivity;
import com.connester.job.activity.NotificationActivity;
import com.connester.job.activity.ProfileActivity;
import com.connester.job.activity.UserMenuActivity;
import com.connester.job.activity.business.BusinessActivity;
import com.connester.job.activity.community.CommunityActivity;
import com.connester.job.activity.message.ChatHistoryUsersActivity;
import com.connester.job.activity.nonslug.AddFeedsActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.notification_message.ChatModule;
import com.connester.job.module.notification_message.TypingOnlineListener;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class SetTopBottomBar {
    Context context;
    Activity activity;
    SessionPref sessionPref;

    public SetTopBottomBar(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        sessionPref = new SessionPref(context);
    }

    private ImageView navHome_btn, navNetwork_btn, navAddFeeds_btn, navNotification_btn, navJob_btn;
    TextView notification_dot;

    public void setBottomNavBar(MenuItem activeItem) {
        navHome_btn = activity.findViewById(R.id.navHome_btn);
        navNetwork_btn = activity.findViewById(R.id.navNetwork_btn);
        navAddFeeds_btn = activity.findViewById(R.id.navAddFeeds_btn);
        navNotification_btn = activity.findViewById(R.id.navNotification_btn);
        notification_dot = activity.findViewById(R.id.notification_dot);
        navJob_btn = activity.findViewById(R.id.navJob_btn);
        if (!activeItem.equals(MenuItem.navHome_btn))
            navHome_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, HomeActivity.class));
                }
            });
        if (!activeItem.equals(MenuItem.navNetwork_btn))
            navNetwork_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, NetworkActivity.class));
                }
            });
        if (!activeItem.equals(MenuItem.navAddFeeds_btn))
            navAddFeeds_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddFeedsActivity.class);
                    intent.putExtra("feed_for", "USER");//USER/COMMUNITY/BUSINESS
                    context.startActivity(intent);
                }
            });
        if (!activeItem.equals(MenuItem.navNotification_btn))
            navNotification_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, NotificationActivity.class));
                    notification_dot.setVisibility(View.GONE);
                }
            });
        if (!activeItem.equals(MenuItem.navJob_btn))
            navJob_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, JobsEvents_Activity.class));
                }
            });

        setBottomActiveItem(activeItem);
    }

    public enum MenuItem {navHome_btn, navNetwork_btn, navAddFeeds_btn, navNotification_btn, navJob_btn}

    public void setBottomActiveItem(MenuItem itemNm) {
        if (itemNm.equals(MenuItem.navHome_btn)) {
            navHome_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navNetwork_btn)) {
            navNetwork_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navAddFeeds_btn)) {
            navAddFeeds_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navNotification_btn)) {
            navNotification_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
        if (itemNm.equals(MenuItem.navJob_btn)) {
            navJob_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        }
    }

    TextView message_dot;

    public void setTopBar() {
        ImageView user_pic = activity.findViewById(R.id.user_pic);
        Glide.with(context).load(sessionPref.getUserProfilePic()).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);
        user_pic.setOnClickListener(v -> {
            context.startActivity(new Intent(context, UserMenuActivity.class));
        });

        SearchView search_master_sv = activity.findViewById(R.id.search_master_sv);
        search_master_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                openSearchDialog(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView open_message_iv = activity.findViewById(R.id.open_message_iv);
        message_dot = activity.findViewById(R.id.message_dot);
        open_message_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatHistoryUsersActivity.class));
                message_dot.setVisibility(View.GONE);
            }
        });
    }

    ListView search_list;
    EditText search_et;
    TextView no_item_tv;

    TypingOnlineListener typingOnlineListener = new TypingOnlineListener() {
        @Override
        public void online() {
            if (search_et != null && search_list != null && no_item_tv != null) {
                dataLoad(search_list, no_item_tv, search_et.getText().toString());
            }
        }

        @Override
        public void typing() {
            super.typing();
        }
    };

    private void openSearchDialog(String query) {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.all_in_one_search_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        search_et = dialog.findViewById(R.id.search_et);
        search_et.setText(query);
        search_et.addTextChangedListener(new TextWatcher() {
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
        search_list = dialog.findViewById(R.id.search_list);
        no_item_tv = dialog.findViewById(R.id.no_item_tv);
        dialog.show();
        CommonFunction.PleaseWaitShow(context);
        dataLoad(search_list, no_item_tv, query);
    }

    private void dataLoad(ListView searchList, TextView no_item_tv, String query) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        hashMap.put("searchQuery", query);
        //if use pagination hashMap.put("start", 0);//0 10 20
        ApiClient.getClient().create(ApiInterface.class).SEARCH_ALL_IN_ONE(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                AllInOneSearchResponse allInOneSearchResponse = (AllInOneSearchResponse) response.body();
                if (allInOneSearchResponse.status) {
                    if (allInOneSearchResponse.dt.size() > 0) {
                        searchList.setVisibility(View.VISIBLE);
                        no_item_tv.setVisibility(View.GONE);
                        searchList.setAdapter(getSearchItemAdapter(allInOneSearchResponse));
                    } else {
                        searchList.setVisibility(View.GONE);
                        no_item_tv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private ListAdapter getSearchItemAdapter(AllInOneSearchResponse allInOneSearchResponse) {
        String imgPath = allInOneSearchResponse.imgPath;
        String feedImgPath = allInOneSearchResponse.feedImgPath;
        ;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return allInOneSearchResponse.dt.size();
            }

            @Override
            public AllInOneSearchResponse.Dt getItem(int position) {
                return allInOneSearchResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.job_application_list_item, null);

                AllInOneSearchResponse.Dt row = getItem(position);

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);


                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.searchText);

                TextView first_tv = view.findViewById(R.id.first_tv);
                first_tv.setVisibility(View.GONE);
                TextView second_tv = view.findViewById(R.id.second_tv);
                second_tv.setVisibility(View.GONE);
                TextView dot_tv = view.findViewById(R.id.dot_tv);
                dot_tv.setVisibility(View.GONE);
                TextView address_tv = view.findViewById(R.id.address_tv);
                address_tv.setVisibility(View.GONE);

                LinearLayout second_line_ll = view.findViewById(R.id.second_line_ll);
                second_line_ll.setVisibility(View.GONE);
                TextView job_profile_tv = view.findViewById(R.id.job_profile_tv);

                View.OnClickListener openFeedFullView = v -> {
                    Intent intent = new Intent(context, FeedFullViewActivity.class);
                    intent.putExtra("feed_master_id", row.tblId);
                    context.startActivity(intent);
                };
                int placeHolder = R.drawable.default_user_pic;
                if (row.tblName != null && !row.tblName.equalsIgnoreCase("")) {
                    switch (row.tblName) {
                        case "user_master":
                            view.setOnClickListener(v -> {
                                Intent intent = new Intent(context, ProfileActivity.class);
                                intent.putExtra("user_master_id", row.tblId);
                                context.startActivity(intent);
                            });
                            break;
                        case "community_master":
                            placeHolder = R.drawable.default_groups_pic;
                            view.setOnClickListener(v -> {
                                Intent intent = new Intent(context, CommunityActivity.class);
                                intent.putExtra("community_master_id", row.tblId);
                                context.startActivity(intent);
                            });
                            break;
                        case "business_page":
                            placeHolder = R.drawable.default_business_pic;
                            view.setOnClickListener(v -> {
                                Intent intent = new Intent(context, BusinessActivity.class);
                                intent.putExtra("business_page_id", row.tblId);
                                context.startActivity(intent);
                            });
                            break;
                        case "job_post":
                            second_line_ll.setVisibility(View.VISIBLE);
                            job_profile_tv.setText("Jobs");
                            view.setOnClickListener(openFeedFullView);
                            break;
                        case "job_event":
                            second_line_ll.setVisibility(View.VISIBLE);
                            job_profile_tv.setText("Events");
                            view.setOnClickListener(openFeedFullView);
                            break;
                        case "media_post":
                            second_line_ll.setVisibility(View.VISIBLE);
                            job_profile_tv.setText("Image/Video");
                            view.setOnClickListener(openFeedFullView);
                            break;
                        case "text_post":
                            second_line_ll.setVisibility(View.VISIBLE);
                            job_profile_tv.setText("Text/Link");
                            view.setOnClickListener(openFeedFullView);
                            break;
                    }
                } else view.setVisibility(View.GONE);
                if (CommonFunction.getFileExtension(row.pic).equalsIgnoreCase("svg")) {
                    LoadOnlySvgImage(row.pic, member_profile_pic);
                } else {
                    Glide.with(context).load(row.pic).placeholder(placeHolder).into(member_profile_pic);
                }
                return view;
            }
        };
        return baseAdapter;
    }

    private void LoadOnlySvgImage(String pic, ImageView memberProfilePic) {
        GlideToVectorYou
                .init()
                .with(context)
                .withListener(new GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {
                        Log.d(LogTag.CHECK_DEBUG, "Load file fail");
                    }

                    @Override
                    public void onResourceReady() {
                    }
                })
                .load(Uri.parse(pic), memberProfilePic);
    }


    BroadcastReceiver allNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String jsonData = intent.getExtras().getString("jsonData");
            NotificationJsonData notificationJsonData = new Gson().fromJson(jsonData, NotificationJsonData.class);
            if (notificationJsonData.notificationId != null && !notificationJsonData.notificationId.equalsIgnoreCase("")) {
                if (notification_dot != null)
                    notification_dot.setVisibility(View.VISIBLE);
            }
        }
    };
    BroadcastReceiver msgReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String chat_master_id = intent.getExtras().getString("chat_master_id");
            String rec_user_master_id = intent.getExtras().getString("rec_user_master_id");
            String send_user_master_id = intent.getExtras().getString("send_user_master_id");
            String pushJsonString = intent.getExtras().getString("pushJson");//normal format
            Log.e(LogTag.CHECK_DEBUG, "Message received call Broadcast : " + chat_master_id);
            if (sessionPref.getUserMasterId().equals(rec_user_master_id)) {
                if (pushJsonString != null) {
                    if (message_dot != null)
                        message_dot.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    public void onResume() {
        IntentFilter intentFilterReadDelivered = new IntentFilter();
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_CONNECT_REQ);
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_FOLLOW_REQ);
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_MESSAGE);
        intentFilterReadDelivered.addAction(NotificationActivity.BROADCAST_RECOMMENDED_JOB);
        activity.registerReceiver(allNotificationReceiver, intentFilterReadDelivered);

        IntentFilter intentFilterReceived = new IntentFilter(ChatModule.MSG_RECEIVED_FILTER);
        activity.registerReceiver(msgReceived, intentFilterReceived);

        setCountingNewMessageNotification(sessionPref.getUserMasterId(), sessionPref.getApiKey());
    }

    public void onPause() {
        activity.unregisterReceiver(allNotificationReceiver);

        activity.unregisterReceiver(msgReceived);
    }

    private void setCountingNewMessageNotification(String userMasterId, String apiKey) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", userMasterId);
        hashMap.put("apiKey", apiKey);
        ApiClient.getClient().create(ApiInterface.class).NEW_MESSAGE_NOTIFICATION_COUNT(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            if (normalCommonResponse.totalMessage != null && !normalCommonResponse.totalMessage.equalsIgnoreCase("") && !normalCommonResponse.totalMessage.equalsIgnoreCase("0")) {
                                if (message_dot != null)
                                    message_dot.setVisibility(View.VISIBLE);
                            }
                            if (normalCommonResponse.totalNotification != null && !normalCommonResponse.totalNotification.equalsIgnoreCase("") && !normalCommonResponse.totalNotification.equalsIgnoreCase("0")) {
                                if (notification_dot != null)
                                    notification_dot.setVisibility(View.VISIBLE);
                            }
                        }
                        Log.d(LogTag.CHECK_DEBUG, "New Message Notification counter message : " + normalCommonResponse.msg);
                    }
                }
            }
        });
    }
}
