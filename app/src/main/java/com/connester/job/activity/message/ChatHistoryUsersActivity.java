package com.connester.job.activity.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.ChatUserListResponse;
import com.connester.job.function.DateUtils;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.google.android.material.card.MaterialCardView;

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
        no_row_found = findViewById(R.id.no_row_found);
        progressBar = findViewById(R.id.progressBar);
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });

        setData();
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
                                no_row_found.setVisibility(View.VISIBLE);
                                chatUserListAdapter = new ChatUserListAdapter(chatUserListResponse.dt, R.layout.chat_user_list_row_item, chatUserListResponse.imgPath);
                                chatUserList.setAdapter(chatUserListAdapter);
                            } else {
                                no_row_found.setVisibility(View.VISIBLE);
                            }
                        } else
                            Toast.makeText(context, chatUserListResponse.msg, Toast.LENGTH_SHORT).show();
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