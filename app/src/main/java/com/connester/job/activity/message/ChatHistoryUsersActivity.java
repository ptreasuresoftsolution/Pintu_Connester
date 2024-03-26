package com.connester.job.activity.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.ChatUserListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSeeAllCommonResponse;
import com.connester.job.activity.EditProfileActivity;
import com.connester.job.activity.NetworkActivity;
import com.connester.job.function.ActionCallBack;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.DateUtils;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.notification_message.ChatModule;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChatHistoryUsersActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    ApiInterface apiInterface;
    SearchView search_user;
    RecyclerView chatUserList;
    TextView no_row_found;
    FrameLayout progressBar;
    ImageView back_iv;
    HashMap hashMapDefault = new HashMap();

    String action = null, //pick, startChat
            message = null,//if action=pick
            userId = null;//if action=startChat
    boolean isEncryMsg = false;//true,false

    BroadcastReceiver msgReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer pushJson = intent.getExtras().getInt("pushJson"); // encrypt Json string
            Log.e(LogTag.CHECK_DEBUG, "In ChatFragment Message received call Broadcast ");
            /*if (chatUserList != null && chatUserListAdapter != null && chatUserListResponse != null) {
                int index = findIndexOf(chatUserListResponse, cus_id);
                if (index > -1) {
                    chatUserListAdapter.notifyItemChanged(index);
                    Collections.swap(chatUserListResponse.data, index, 0);
                    chatUserListAdapter.notifyItemMoved(index, 0);
                } else {
                    ChatUserListApi();
                }
            }*/
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilterReceived = new IntentFilter(ChatModule.MSG_RECEIVED_FILTER);
        registerReceiver(msgReceived, intentFilterReceived);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(msgReceived);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history_users);

        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        search_user = findViewById(R.id.search_user);
        search_user.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (chatUserListAdapter != null)
                    chatUserListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        chatUserList = findViewById(R.id.chatUserList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chatUserList.setLayoutManager(layoutManager);
        no_row_found = findViewById(R.id.no_row_found);
        progressBar = findViewById(R.id.progressBar);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });

        manageRedirect = new ActionCallBack() {
            @Override
            public void callBack() {
                Intent intent = getIntent();
                if (intent != null) {
                    action = intent.getStringExtra("action");
                    userId = intent.getStringExtra("userId");
                    message = intent.getStringExtra("message");
                    isEncryMsg = intent.getBooleanExtra("isEncryMsg", false);
                    if (action != null && action.equalsIgnoreCase("pick")) {
                        openUserPicker(message, isEncryMsg);
                    } else if (action != null && action.equalsIgnoreCase("startChat")) {
                        Intent i = new Intent(context, ChatActivity.class);
                        if (userId != null)
                            i.putExtra("user_master_id", userId);
                        if (message != null)
                            i.putExtra("message", message);
                        if (isEncryMsg)
                            intent.putExtra("isEncryMsg", true);

                        startActivity(i);
                    }
                }
            }
        };
        setData();
    }

    ActionCallBack manageRedirect;

    private void openUserPicker(String message, boolean isEncryMsg) {
        Dialog dialog = new Dialog(activity, R.style.Base_Theme_Connester);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.list_view_dialog);
        EditProfileActivity.setDialogFullScreenSetting(dialog);

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            dialog.dismiss();
        });
        ListView list_lt = dialog.findViewById(R.id.list_lt);
        SearchView search_view = dialog.findViewById(R.id.search_view);
        search_view.setVisibility(View.VISIBLE);
        search_view.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (userPickListAdapter != null)
                    userPickListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        TextView title = dialog.findViewById(R.id.title);
        title.setVisibility(View.GONE);

        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        //connectReqUsersMaster / connectUsers / followReqUsers / followerUsers / followingUsers / userCommunitys / userBusinessPages / userEvents /
        //suggestedCityUser / suggestedIndustryUser / suggestedGroup / suggestedBusPages
        hashMap.put("fnName", NetworkActivity.SeeAllFnName.connectUsers.getVal());
        apiInterface.NETWORK_SEE_ALL_LIST(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NetworkSeeAllCommonResponse.ConnectionListResponse connectionListResponse = new Gson().fromJson(new Gson().toJson(response.body()), NetworkSeeAllCommonResponse.ConnectionListResponse.class);
                        if (connectionListResponse.status) {
                            userPickListAdapter = new UserPickListAdapter(connectionListResponse.dt, connectionListResponse.imgPath, message, isEncryMsg);
                            list_lt.setAdapter(userPickListAdapter);
                        } else
                            Toast.makeText(context, connectionListResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
        CommonFunction.PleaseWaitShow(context);
    }

    UserPickListAdapter userPickListAdapter;

    class UserPickListAdapter extends BaseAdapter {
        ValueFilter valueFilter;
        String pathimg;
        List<NetworkSeeAllCommonResponse.ConnectionListResponse.Dt> datumList = new ArrayList<>();
        List<NetworkSeeAllCommonResponse.ConnectionListResponse.Dt> mStringFilterList = new ArrayList<>();
        String message;
        boolean isEncryMsg;

        public UserPickListAdapter(List<NetworkSeeAllCommonResponse.ConnectionListResponse.Dt> datumList, String pathimg, String message, boolean isEncryMsg) {
            this.datumList = datumList;
            this.mStringFilterList = datumList;
            this.pathimg = pathimg;
            this.message = message;
            this.isEncryMsg = isEncryMsg;
        }

        public int getCount() {
            return datumList.size();
        }

        @Override
        public NetworkSeeAllCommonResponse.ConnectionListResponse.Dt getItem(int position) {
            return datumList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = LayoutInflater.from(context).inflate(R.layout.user_pic_two_btn_list_item, parent, false);

            NetworkSeeAllCommonResponse.ConnectionListResponse.Dt row = getItem(position);
            View.OnClickListener gotoChat = v -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_master_id", String.valueOf(row.userMasterId));
                if (message != null) {
                    intent.putExtra("message", message);
                }
                if (isEncryMsg)
                    intent.putExtra("isEncryMsg", isEncryMsg);
                startActivity(intent);
            };

            ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
            Glide.with(context).load(pathimg + row.profilePic).placeholder(R.drawable.default_groups_pic).into(member_profile_pic);
            member_profile_pic.setOnClickListener(gotoChat);
            TextView member_tv = view.findViewById(R.id.member_tv);
            member_tv.setText(row.name);
            member_tv.setOnClickListener(gotoChat);
            MaterialButton first_mbtn = view.findViewById(R.id.first_mbtn);
            first_mbtn.setVisibility(View.GONE);

            MaterialButton second_mbtn = view.findViewById(R.id.second_mbtn);
            second_mbtn.setVisibility(View.GONE);

            return view;
        }

        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    List<NetworkSeeAllCommonResponse.ConnectionListResponse.Dt> filterList = new ArrayList<>();
                    for (int i = 0; i < mStringFilterList.size(); i++) {
                        if ((mStringFilterList.get(i).name.toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(mStringFilterList.get(i));
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = mStringFilterList.size();
                    results.values = mStringFilterList;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                datumList = (List<NetworkSeeAllCommonResponse.ConnectionListResponse.Dt>) results.values;
                notifyDataSetChanged();
            }

        }
    }


    private void setData() {
        progressBar.setVisibility(View.VISIBLE);
        hashMapDefault.put("user_master_id", sessionPref.getUserMasterId());
        hashMapDefault.put("apiKey", sessionPref.getApiKey());
        hashMapDefault.put("device", "ANDROID");
        apiInterface.CHATTING_HISTORY_USER_LIST(hashMapDefault).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ChatUserListResponse chatUserListResponse = (ChatUserListResponse) response.body();
                        if (chatUserListResponse.status) {
                            if (chatUserListResponse.dt.size() > 0) {
                                no_row_found.setVisibility(View.GONE);
                                chatUserListAdapter = new ChatUserListAdapter(chatUserListResponse.dt, R.layout.chat_user_list_row_item, chatUserListResponse.imgPath);
                                chatUserList.setAdapter(chatUserListAdapter);
                            } else {
                                no_row_found.setVisibility(View.VISIBLE);
                            }
                        } else
                            Toast.makeText(context, chatUserListResponse.msg, Toast.LENGTH_SHORT).show();

                        if (manageRedirect != null)
                            manageRedirect.callBack();

                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    ChatUserListAdapter chatUserListAdapter;

    class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.ViewHolder> {
        String pathimg;
        int resourceFileName;
        List<ChatUserListResponse.Dt> datumList = new ArrayList<>();
        List<ChatUserListResponse.Dt> mStringFilterList = new ArrayList<>();
        ValueFilter valueFilter;

        public ChatUserListAdapter(List<ChatUserListResponse.Dt> datumList, int resourceFileName, String pathimg) {
            this.datumList = datumList;
            this.mStringFilterList = datumList;
            this.resourceFileName = resourceFileName;
            this.pathimg = pathimg;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(resourceFileName, parent, false);
            return new ChatUserListAdapter.ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String url = pathimg + datumList.get(position).profilePic;
            Glide.with(context).load(url).placeholder(R.drawable.default_user_pic).into(holder.user_profile_pic);

            holder.text_msg.setText(datumList.get(position).msg);
            if (datumList.get(position).msgType.equalsIgnoreCase("FILE")){
                holder.text_msg.setText(datumList.get(position).fileType+" : File");
            }
            holder.text_name.setText(datumList.get(position).name);
            String time = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "hh:mm a", datumList.get(position).msgSendTime);
            long dayInterval = DateUtils.dateDiff("d", datumList.get(position).msgSendTime, DateUtils.TODAYDATETIMEforDB());
            if (dayInterval > 1) {
                time = DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "dd MMM", datumList.get(position).msgSendTime);
            } else if (dayInterval > 0) {
                time = "Yesterday";
            }
            holder.text_time.setText(time);

            int countUnread = Integer.parseInt(datumList.get(position).unread);
            if (countUnread > 0) {
                holder.unread_count_card.setVisibility(View.VISIBLE);
                holder.count_unread.setText(String.valueOf(countUnread));
            }

            holder.profile_view.setOnClickListener(view -> {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_master_id", datumList.get(position).userMasterId);

                startActivity(intent);
                holder.unread_count_card.setVisibility(View.GONE);
            });
        }


        @Override
        public int getItemCount() {
            return datumList.size();
        }

        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
        }

        private class ValueFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    List<ChatUserListResponse.Dt> filterList = new ArrayList<>();
                    for (int i = 0; i < mStringFilterList.size(); i++) {
                        if ((mStringFilterList.get(i).name.toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(mStringFilterList.get(i));
                        }
                    }
                    results.count = filterList.size();
                    results.values = filterList;
                } else {
                    results.count = mStringFilterList.size();
                    results.values = mStringFilterList;
                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                datumList = (ArrayList<ChatUserListResponse.Dt>) results.values;
                notifyDataSetChanged();
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView user_profile_pic;
            TextView text_name, text_msg, text_time, count_unread;
            LinearLayout profile_view;
            MaterialCardView unread_count_card;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                user_profile_pic = itemView.findViewById(R.id.user_profile_pic);
                text_name = itemView.findViewById(R.id.text_name);
                text_msg = itemView.findViewById(R.id.text_msg);
                text_time = itemView.findViewById(R.id.text_time);
                profile_view = itemView.findViewById(R.id.profile_view);
                unread_count_card = itemView.findViewById(R.id.unread_count_card);
                count_unread = itemView.findViewById(R.id.count_unread);
            }
        }
    }
}