package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSuggestedListResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class MyNetworkActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    LinearLayout main_ll;
    LayoutInflater layoutInflater;
    FrameLayout progressBar;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_network);
        context = MyNetworkActivity.this;
        activity = MyNetworkActivity.this;
        sessionPref = new SessionPref(context);
        layoutInflater = LayoutInflater.from(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        main_ll = findViewById(R.id.main_ll);main_ll.removeAllViews();
        progressBar = findViewById(R.id.progressBar);

        MaterialButton manage_nt_btn = findViewById(R.id.manage_nt_btn);
        manage_nt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show full screen dialog for list network
            }
        });

        setTopBar();
        setBottomNavBar();

        loadDefaultView();
    }

    private void setBottomNavBar() {
        ImageView navHome_btn = findViewById(R.id.navHome_btn),
                navNetwork_btn = findViewById(R.id.navNetwork_btn),
                navAddFeeds_btn = findViewById(R.id.navAddFeeds_btn),
                navNotification_btn = findViewById(R.id.navNotification_btn),
                navJob_btn = findViewById(R.id.navJob_btn);
        navHome_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });
        navAddFeeds_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddFeedsActivity.class);
                intent.putExtra("feed_for", "USER");//USER/COMMUNITY/BUSINESS
                startActivity(intent);
            }
        });
        navNotification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, NotificationActivity.class));
            }
        });
        navJob_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, JobsAndEventsActivity.class));
            }
        });
        navNetwork_btn.setColorFilter(ContextCompat.getColor(context, R.color.primary));

    }

    private void setTopBar() {
        ImageView user_pic = findViewById(R.id.user_pic);
        Glide.with(context).load(sessionPref.getUserProfilePic()).centerCrop().placeholder(R.drawable.default_user_pic).into(user_pic);

        SearchView search_master_sv = findViewById(R.id.search_master_sv);
        search_master_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView open_message_iv = findViewById(R.id.open_message_iv);
        open_message_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MessageActivity.class));
            }
        });
    }


    private void loadDefaultView() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        apiInterface.NETWORK_SUGGESTED_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NetworkSuggestedListResponse networkSuggestedListResponse = (NetworkSuggestedListResponse) response.body();
                        if (networkSuggestedListResponse.status) {
                            if (networkSuggestedListResponse.jsonDt.connReq.dt.size() > 0) {
                                View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                                TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                                nt_list_title.setText("Invitation Request");
                                MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                                nt_list_seeall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //load all invitation request
                                        loadAllInvitationRequest();
                                    }
                                });
                                String imgPath = networkSuggestedListResponse.jsonDt.connReq.imgPath;
                                GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
//                                GridLayout grid_lt = blankGridSt.findViewById(R.id.grid_lt);
//                                grid_lt.setColumnCount(2);
//                                grid_lt.removeAllViews();
                                BaseAdapter baseAdapter =new BaseAdapter() {
                                    @Override
                                    public int getCount() {
                                        return networkSuggestedListResponse.jsonDt.connReq.dt.size() + 2;
                                    }

                                    @Override
                                    public NetworkSuggestedListResponse.JsonDt.ConnReq.Dt getItem(int position) {
                                        return networkSuggestedListResponse.jsonDt.connReq.dt.get(position);
                                    }

                                    @Override
                                    public long getItemId(int position) {
                                        return position;
                                    }

                                    @Override
                                    public View getView(int position, View inv_rq_view, ViewGroup parent) {
                                        if (inv_rq_view == null)
                                            inv_rq_view = LayoutInflater.from(context).inflate(R.layout.network_card_invitation, parent, false);

                                        NetworkSuggestedListResponse.JsonDt.ConnReq.Dt row = getItem(0);
                                        ImageView req_decline_iv = inv_rq_view.findViewById(R.id.req_decline_iv);
                                        MaterialButton req_decline_mbtn = inv_rq_view.findViewById(R.id.req_decline_mbtn);
                                        View finalInv_rq_view1 = inv_rq_view;
                                        View.OnClickListener req_decline = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //call req_decline api and remove from gridlayout
                                                invitationReqDecline(grid_lt, finalInv_rq_view1, row.userMasterId);
                                            }
                                        };
                                        req_decline_iv.setOnClickListener(req_decline);
                                        req_decline_mbtn.setOnClickListener(req_decline);

                                        TextView member_tv = inv_rq_view.findViewById(R.id.member_tv);
                                        member_tv.setText(row.name);
                                        TextView member_pos_tv = inv_rq_view.findViewById(R.id.member_pos_tv);
                                        member_pos_tv.setText(row.position);
                                        ImageView member_profile_pic = inv_rq_view.findViewById(R.id.member_profile_pic);
                                        Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                                        TextView member_mutual_conn_tv = inv_rq_view.findViewById(R.id.member_mutual_conn_tv);
                                        member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, networkSuggestedListResponse.jsonDt.connReq.myDt.connectUser).size() + " Mutual Connections");
                                        MaterialButton req_accept_mbtn = inv_rq_view.findViewById(R.id.req_accept_mbtn);
                                        View finalInv_rq_view = inv_rq_view;
                                        req_accept_mbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //call req_accept api and remove view from gridlayout
                                                invitationReqAccept(grid_lt, finalInv_rq_view, row.userMasterId);
                                            }
                                        });
                                        return inv_rq_view;
                                    }
                                };
                                grid_lt.setAdapter(baseAdapter);
                                CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                              /*  for (NetworkSuggestedListResponse.JsonDt.ConnReq.Dt row : networkSuggestedListResponse.jsonDt.connReq.dt) {
                                    View inv_rq_view = layoutInflater.inflate(R.layout.network_card_invitation, null);
                                    View inv_rq_view2 = layoutInflater.inflate(R.layout.network_card_invitation, null);
                                    View inv_rq_view3 = layoutInflater.inflate(R.layout.network_card_invitation, null);
                                    //layout param set for grid layout
//                                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(new ViewGroup.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.FILL_PARENT));
//                                    params.setGravity(Gravity.FILL_HORIZONTAL);

                                    GridLayout.LayoutParams param= new GridLayout.LayoutParams(GridLayout.spec(
                                            GridLayout.UNDEFINED, 1f),
                                            GridLayout.spec(GridLayout.UNDEFINED, 1f));
//                                    param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                                    param.width = 0;
//                                    param.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                                    param.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                                    param.setGravity(Gravity.FILL_HORIZONTAL);
//                                    inv_rq_view.setLayoutParams(param);

                                    inv_rq_view2.setLayoutParams(param);
                                    inv_rq_view3.setLayoutParams(param);

                                    ImageView req_decline_iv = inv_rq_view.findViewById(R.id.req_decline_iv);
                                    MaterialButton req_decline_mbtn = inv_rq_view.findViewById(R.id.req_decline_mbtn);
                                    View.OnClickListener req_decline = new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //call req_decline api and remove from gridlayout
                                            invitationReqDecline(grid_lt, inv_rq_view, row.userMasterId);
                                        }
                                    };
                                    req_decline_iv.setOnClickListener(req_decline);
                                    req_decline_mbtn.setOnClickListener(req_decline);

                                    TextView member_tv = inv_rq_view.findViewById(R.id.member_tv);
                                    member_tv.setText(row.name);
                                    TextView member_pos_tv = inv_rq_view.findViewById(R.id.member_pos_tv);
                                    member_pos_tv.setText(row.position);
                                    ImageView member_profile_pic = inv_rq_view.findViewById(R.id.member_profile_pic);
                                    Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                                    TextView member_mutual_conn_tv = inv_rq_view.findViewById(R.id.member_mutual_conn_tv);
                                    member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, networkSuggestedListResponse.jsonDt.connReq.myDt.connectUser).size() + " Mutual Connections");
                                    MaterialButton req_accept_mbtn = inv_rq_view.findViewById(R.id.req_accept_mbtn);
                                    req_accept_mbtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //call req_accept api and remove view from gridlayout
                                            invitationReqAccept(grid_lt, inv_rq_view, row.userMasterId);
                                        }
                                    });
                                    grid_lt.addView(inv_rq_view3);
                                    grid_lt.addView(inv_rq_view2);
                                }*/
                                main_ll.addView(blankGridSt);
                            }
                            progressBar.setVisibility(View.GONE);
                        } else
                            Toast.makeText(context, networkSuggestedListResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void invitationReqAccept(GridView gridLt, View invRqView, String userMasterId) {

    }

    private void invitationReqDecline(GridView gridLt, View invRqView, String userMasterId) {
    }

    private void loadAllInvitationRequest() {
    }
}