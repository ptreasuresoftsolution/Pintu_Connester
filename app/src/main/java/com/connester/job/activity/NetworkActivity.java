package com.connester.job.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NetworkMenuListCounter;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSeeAllCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSuggestedListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.activity.business.BusinessActivity;
import com.connester.job.activity.community.CommunityActivity;
import com.connester.job.activity.message.ChatHistoryUsersActivity;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.SetTopBottomBar;
import com.connester.job.module.UserMaster;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class NetworkActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;
    LinearLayout main_ll;
    LayoutInflater layoutInflater;
    FrameLayout progressBar;
    ApiInterface apiInterface;
    SetTopBottomBar setTopBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_network);
        context = NetworkActivity.this;
        activity = NetworkActivity.this;
        sessionPref = new SessionPref(context);
        setTopBottomBar = new SetTopBottomBar(context, activity);
        layoutInflater = LayoutInflater.from(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        main_ll = findViewById(R.id.main_ll);
        progressBar = findViewById(R.id.progressBar);

        MaterialButton manage_nt_btn = findViewById(R.id.manage_nt_btn);
        manage_nt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show full screen dialog for list network
                openNetworkListMenu();
            }
        });

        setTopBottomBar.setTopBar();
        setTopBottomBar.setBottomNavBar(SetTopBottomBar.MenuItem.navNetwork_btn);

        loadDefaultView();
    }


    private void loadDefaultView() {
        main_ll.removeAllViews();
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
                            View divider_v = null;
                            //invitation request
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
                                GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                                grid_lt.setAdapter(getInvitationReqAdapter(networkSuggestedListResponse.jsonDt.connReq));
                                CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                                divider_v = blankGridSt.findViewById(R.id.divider_v);
                                main_ll.addView(blankGridSt);
                            }
                            if (networkSuggestedListResponse.jsonDt.sugUserCity.dt.size() > 0) {
                                View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                                TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                                nt_list_title.setText("People same city are");
                                MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                                nt_list_seeall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //load all invitation request
                                        loadAllSuggestedCityUser();
                                    }
                                });
                                GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                                grid_lt.setAdapter(getSuggestedCityUserAdapter(networkSuggestedListResponse.jsonDt.sugUserCity));
                                CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                                divider_v = blankGridSt.findViewById(R.id.divider_v);
                                main_ll.addView(blankGridSt);
                            }
                            if (networkSuggestedListResponse.jsonDt.sugUserIndustry.dt.size() > 0) {
                                View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                                TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                                nt_list_title.setText("People same industry are");
                                MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                                nt_list_seeall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //load all invitation request
                                        loadAllSuggestedIndustryUser();
                                    }
                                });
                                GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                                grid_lt.setAdapter(getSuggestedIndustryUserAdapter(networkSuggestedListResponse.jsonDt.sugUserIndustry));
                                CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                                divider_v = blankGridSt.findViewById(R.id.divider_v);
                                main_ll.addView(blankGridSt);
                            }
                            if (networkSuggestedListResponse.jsonDt.sugGroup.dt.size() > 0) {
                                View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                                TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                                nt_list_title.setText("Groups you may be interested in");
                                MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                                nt_list_seeall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //load all invitation request
                                        loadAllSuggestedGroup();
                                    }
                                });
                                GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                                grid_lt.setAdapter(getSuggestedGroupAdapter(networkSuggestedListResponse.jsonDt.sugGroup));
                                CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                                divider_v = blankGridSt.findViewById(R.id.divider_v);
                                main_ll.addView(blankGridSt);
                            }
                            if (networkSuggestedListResponse.jsonDt.sugBusPages.dt.size() > 0) {
                                View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                                TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                                nt_list_title.setText("Suggested pages");
                                MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                                nt_list_seeall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //load all invitation request
                                        loadAllSuggestedPages();
                                    }
                                });
                                GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                                grid_lt.setAdapter(getSuggestedPagesAdapter(networkSuggestedListResponse.jsonDt.sugBusPages));
                                CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                                divider_v = blankGridSt.findViewById(R.id.divider_v);
                                main_ll.addView(blankGridSt);
                            }
                            if (divider_v != null)
                                divider_v.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else
                            Toast.makeText(context, networkSuggestedListResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private BaseAdapter getSuggestedPagesAdapter(NetworkSuggestedListResponse.JsonDt.SugBusPages sugBusPages) {
        String imgPath = sugBusPages.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sugBusPages.dt.size();
            }

            @Override
            public NetworkSuggestedListResponse.JsonDt.SugBusPages.Dt getItem(int position) {
                return sugBusPages.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_pages, parent, false);

                NetworkSuggestedListResponse.JsonDt.SugBusPages.Dt row = getItem(position);
                View.OnClickListener openBusinessPage = v -> {
                    Intent intent = new Intent(context, BusinessActivity.class);
                    intent.putExtra("business_page_id", row.businessPageId);
                    startActivity(intent);
                };

                ImageView page_logo_iv = view.findViewById(R.id.page_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_groups_pic).into(page_logo_iv);
                page_logo_iv.setOnClickListener(openBusinessPage);
                TextView page_name_tv = view.findViewById(R.id.page_name_tv);
                page_name_tv.setText(row.busName);
                page_name_tv.setOnClickListener(openBusinessPage);
                TextView page_member_tv = view.findViewById(R.id.page_member_tv);
                page_member_tv.setText(row.members + " members");

                MaterialButton page_follow_btn = view.findViewById(R.id.page_follow_btn);
                page_follow_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // call follow page request api
                        progressBar.setVisibility(View.VISIBLE);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("business_page_id", row.businessPageId);
                        apiInterface.PAGE_FOLLOW_REQUEST(hashMap).enqueue(new MyApiCallback(progressBar) {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status)
                                            removeItem(position);
                                        else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                sugBusPages.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private void loadAllSuggestedPages() {
        main_ll.removeAllViews();
        networkSeeAllList(new NetworkSeeAllCallback() {
            @Override
            public void apiCallBack(Object responseBody) {
                NetworkSuggestedListResponse.JsonDt.SugBusPages sugBusPages = (NetworkSuggestedListResponse.JsonDt.SugBusPages) responseBody;
                if (sugBusPages.status) {
                    View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                    TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                    nt_list_title.setText("Suggested pages");
                    MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                    nt_list_seeall.setVisibility(View.GONE);
                    GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                    grid_lt.setAdapter(getSuggestedPagesAdapter(sugBusPages));
                    CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                    main_ll.addView(blankGridSt);
                } else Toast.makeText(context, sugBusPages.msg, Toast.LENGTH_SHORT).show();
            }
        }, SeeAllFnName.suggestedBusPages);
    }

    private BaseAdapter getSuggestedGroupAdapter(NetworkSuggestedListResponse.JsonDt.SugGroup sugGroup) {
        String imgPath = sugGroup.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sugGroup.dt.size();
            }

            @Override
            public NetworkSuggestedListResponse.JsonDt.SugGroup.Dt getItem(int position) {
                return sugGroup.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_group, parent, false);

                NetworkSuggestedListResponse.JsonDt.SugGroup.Dt row = getItem(position);
                View.OnClickListener openGroup = v -> {
                    Intent intent = new Intent(context, CommunityActivity.class);
                    intent.putExtra("community_master_id", row.communityMasterId);
                    startActivity(intent);
                };

                ImageView group_logo_iv = view.findViewById(R.id.group_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_groups_pic).into(group_logo_iv);
                group_logo_iv.setOnClickListener(openGroup);
                TextView group_name_tv = view.findViewById(R.id.group_name_tv);
                group_name_tv.setText(row.name);
                group_name_tv.setOnClickListener(openGroup);
                TextView group_members = view.findViewById(R.id.group_members);
                group_members.setText(row.members + " members");

                MaterialButton join_btn = view.findViewById(R.id.join_btn);
                join_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // call join group request api
                        progressBar.setVisibility(View.VISIBLE);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("community_master_id", row.communityMasterId);
                        apiInterface.GROUP_JOIN_REQUEST(hashMap).enqueue(new MyApiCallback(progressBar) {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status)
                                            removeItem(position);
                                        else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                sugGroup.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private void loadAllSuggestedGroup() {
        main_ll.removeAllViews();
        networkSeeAllList(new NetworkSeeAllCallback() {
            @Override
            public void apiCallBack(Object responseBody) {
                NetworkSuggestedListResponse.JsonDt.SugGroup sugGroup = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSuggestedListResponse.JsonDt.SugGroup.class);
                if (sugGroup.status) {
                    View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                    TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                    nt_list_title.setText("Groups you may be interested in");
                    MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                    nt_list_seeall.setVisibility(View.GONE);
                    GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                    grid_lt.setAdapter(getSuggestedGroupAdapter(sugGroup));
                    CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                    main_ll.addView(blankGridSt);
                } else Toast.makeText(context, sugGroup.msg, Toast.LENGTH_SHORT).show();
            }
        }, SeeAllFnName.suggestedGroup);
    }

    private BaseAdapter getSuggestedIndustryUserAdapter(NetworkSuggestedListResponse.JsonDt.SugUserIndustry sugUserIndustry) {
        String imgPath = sugUserIndustry.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sugUserIndustry.dt.size();
            }

            @Override
            public NetworkSuggestedListResponse.JsonDt.SugUserIndustry.Dt getItem(int position) {
                return sugUserIndustry.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_connect, parent, false);

                NetworkSuggestedListResponse.JsonDt.SugUserIndustry.Dt row = getItem(position);
                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", row.userMasterId);
                    startActivity(intent);
                };

                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUserProfile);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUserProfile);
                TextView member_mutual_conn_tv = view.findViewById(R.id.member_mutual_conn_tv);
                member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, sugUserIndustry.myDt.connectUser).size() + " Mutual Connections");
                MaterialButton connect_btn = view.findViewById(R.id.connect_btn);
                connect_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkActionMange(new NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) removeItem(position);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, ActionName.SendInvReq, row.userMasterId);
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                sugUserIndustry.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private void loadAllSuggestedIndustryUser() {
        main_ll.removeAllViews();
        networkSeeAllList(new NetworkSeeAllCallback() {
            @Override
            public void apiCallBack(Object responseBody) {
                NetworkSuggestedListResponse.JsonDt.SugUserIndustry sugUserIndustry = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSuggestedListResponse.JsonDt.SugUserIndustry.class);
                if (sugUserIndustry.status) {
                    View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                    TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                    nt_list_title.setText("People same industry are");
                    MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                    nt_list_seeall.setVisibility(View.GONE);
                    GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                    grid_lt.setAdapter(getSuggestedIndustryUserAdapter(sugUserIndustry));
                    CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                    main_ll.addView(blankGridSt);
                } else Toast.makeText(context, sugUserIndustry.msg, Toast.LENGTH_SHORT).show();
            }
        }, SeeAllFnName.suggestedCityUser);
    }


    private BaseAdapter getSuggestedCityUserAdapter(NetworkSuggestedListResponse.JsonDt.SugUserCity sugUserCity) {
        String imgPath = sugUserCity.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sugUserCity.dt.size();
            }

            @Override
            public NetworkSuggestedListResponse.JsonDt.SugUserCity.Dt getItem(int position) {
                return sugUserCity.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_connect, parent, false);

                NetworkSuggestedListResponse.JsonDt.SugUserCity.Dt row = getItem(position);
                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", row.userMasterId);
                    startActivity(intent);
                };

                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUserProfile);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUserProfile);
                TextView member_mutual_conn_tv = view.findViewById(R.id.member_mutual_conn_tv);
                member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, sugUserCity.myDt.connectUser).size() + " Mutual Connections");
                MaterialButton connect_btn = view.findViewById(R.id.connect_btn);
                connect_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkActionMange(new NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) removeItem(position);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, ActionName.SendInvReq, row.userMasterId);
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                sugUserCity.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private void loadAllSuggestedCityUser() {
        main_ll.removeAllViews();
        networkSeeAllList(new NetworkSeeAllCallback() {
            @Override
            public void apiCallBack(Object responseBody) {
                NetworkSuggestedListResponse.JsonDt.SugUserCity sugUserCity = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSuggestedListResponse.JsonDt.SugUserCity.class);
                if (sugUserCity.status) {
                    View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                    TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                    nt_list_title.setText("People same city are");
                    MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                    nt_list_seeall.setVisibility(View.GONE);
                    GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                    grid_lt.setAdapter(getSuggestedCityUserAdapter(sugUserCity));
                    CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                    main_ll.addView(blankGridSt);
                } else Toast.makeText(context, sugUserCity.msg, Toast.LENGTH_SHORT).show();
            }
        }, SeeAllFnName.suggestedCityUser);
    }

    //list option dialog open full screen

    private void openNetworkListMenu() {
        CommonFunction.PleaseWaitShow(context);
        Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.network_list_menu_dialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());

        ImageView back_iv = dialog.findViewById(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LinearLayout see_all_connection_ll = dialog.findViewById(R.id.see_all_connection_ll);
        see_all_connection_ll.setOnClickListener(v -> {
            dialog.dismiss();
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.ConnectionListResponse connectionListResponse = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSeeAllCommonResponse.ConnectionListResponse.class);
                    if (connectionListResponse.status) {
                        View blankGridSt = layoutInflater.inflate(R.layout.network_list_st, null);
                        TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                        nt_list_title.setText("My Connection");
                        MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                        nt_list_seeall.setVisibility(View.GONE);
                        ListView list_lt = blankGridSt.findViewById(R.id.list_lt);
                        list_lt.setAdapter(getConnectionAdapter(connectionListResponse));
                        CommonFunction.setViewHeightBasedOnChildren(list_lt);
                        main_ll.addView(blankGridSt);
                    } else
                        Toast.makeText(context, connectionListResponse.msg, Toast.LENGTH_SHORT).show();
                }
            }, SeeAllFnName.connectUsers);
        });
        TextView connection_count_tv = dialog.findViewById(R.id.connection_count_tv);

        LinearLayout see_all_inv_req_ll = dialog.findViewById(R.id.see_all_inv_req_ll);
        see_all_inv_req_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadAllInvitationRequest();
            }
        });
        TextView inv_rq_count_tv = dialog.findViewById(R.id.inv_rq_count_tv);

        LinearLayout see_all_follow_rq_ll = dialog.findViewById(R.id.see_all_follow_rq_ll);
        see_all_follow_rq_ll.setOnClickListener(v -> {
            dialog.dismiss();
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.FollowReqListResponse followReqListResponse = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSeeAllCommonResponse.FollowReqListResponse.class);
                    if (followReqListResponse.status) {
                        View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                        TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                        nt_list_title.setText("Follow Request");
                        MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                        nt_list_seeall.setVisibility(View.GONE);
                        GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                        grid_lt.setAdapter(getFollowReqAdapter(followReqListResponse));
                        CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                        main_ll.addView(blankGridSt);
                    } else
                        Toast.makeText(context, followReqListResponse.msg, Toast.LENGTH_SHORT).show();
                }
            }, SeeAllFnName.followReqUsers);
        });
        TextView follow_rq_count_tv = dialog.findViewById(R.id.follow_rq_count_tv);

        LinearLayout see_all_followers_ll = dialog.findViewById(R.id.see_all_followers_ll);
        see_all_followers_ll.setOnClickListener(v -> {
            dialog.dismiss();
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.FollowerListResponse followerListResponse = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSeeAllCommonResponse.FollowerListResponse.class);
                    if (followerListResponse.status) {
                        View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                        TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                        nt_list_title.setText("My Followers");
                        MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                        nt_list_seeall.setVisibility(View.GONE);
                        GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                        grid_lt.setAdapter(getFollowerAdapter(followerListResponse));
                        CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                        main_ll.addView(blankGridSt);
                    } else
                        Toast.makeText(context, followerListResponse.msg, Toast.LENGTH_SHORT).show();
                }
            }, SeeAllFnName.followerUsers);
        });
        TextView followers_count_tv = dialog.findViewById(R.id.followers_count_tv);

        LinearLayout see_all_following_ll = dialog.findViewById(R.id.see_all_following_ll);
        see_all_following_ll.setOnClickListener(v -> {
            dialog.dismiss();
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.FollowingsListResponse followingsListResponse = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSeeAllCommonResponse.FollowingsListResponse.class);
                    if (followingsListResponse.status) {
                        View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                        TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                        nt_list_title.setText("My Followings");
                        MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                        nt_list_seeall.setVisibility(View.GONE);
                        GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                        grid_lt.setAdapter(getFollowingsAdapter(followingsListResponse));
                        CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                        main_ll.addView(blankGridSt);
                    } else
                        Toast.makeText(context, followingsListResponse.msg, Toast.LENGTH_SHORT).show();
                }
            }, SeeAllFnName.followingUsers);
        });
        TextView following_count_tv = dialog.findViewById(R.id.following_count_tv);

        LinearLayout see_all_group_ll = dialog.findViewById(R.id.see_all_group_ll);
        see_all_group_ll.setOnClickListener(v -> {
            dialog.dismiss();
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.CommunitysListResponse communitysListResponse = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSeeAllCommonResponse.CommunitysListResponse.class);
                    if (communitysListResponse.status) {
                        View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                        TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                        nt_list_title.setText("My Community/Group");
                        MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                        nt_list_seeall.setVisibility(View.GONE);
                        GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                        grid_lt.setAdapter(getCommunityGroupAdapter(communitysListResponse));
                        CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                        main_ll.addView(blankGridSt);
                    } else
                        Toast.makeText(context, communitysListResponse.msg, Toast.LENGTH_SHORT).show();
                }
            }, SeeAllFnName.userCommunitys);
        });
        TextView group_count_tv = dialog.findViewById(R.id.group_count_tv);

        LinearLayout see_all_business_page_ll = dialog.findViewById(R.id.see_all_business_page_ll);
        see_all_business_page_ll.setOnClickListener(v -> {
            dialog.dismiss();
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.BusinessPagesListResponse businessPagesListResponse = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSeeAllCommonResponse.BusinessPagesListResponse.class);
                    if (businessPagesListResponse.status) {
                        View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                        TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                        nt_list_title.setText("Business Pages");
                        MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                        nt_list_seeall.setVisibility(View.GONE);
                        GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                        grid_lt.setAdapter(getBusPageAdapter(businessPagesListResponse));
                        CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                        main_ll.addView(blankGridSt);
                    } else
                        Toast.makeText(context, businessPagesListResponse.msg, Toast.LENGTH_SHORT).show();
                }
            }, SeeAllFnName.userBusinessPages);
        });
        TextView business_page_count_tv = dialog.findViewById(R.id.business_page_count_tv);
        apiInterface.NETWORK_SIDE_COUNTER(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NetworkMenuListCounter networkMenuListCounter = (NetworkMenuListCounter) response.body();
                        if (networkMenuListCounter.status) {
                            connection_count_tv.setText(networkMenuListCounter.dt.totalConnection);
                            inv_rq_count_tv.setText(networkMenuListCounter.dt.totalInvatation);
                            follow_rq_count_tv.setText(networkMenuListCounter.dt.totalFollowReq);
                            followers_count_tv.setText(networkMenuListCounter.dt.totalFollowers);
                            following_count_tv.setText(networkMenuListCounter.dt.totalFollowing);
                            group_count_tv.setText(networkMenuListCounter.dt.totalGroups);
                            business_page_count_tv.setText(networkMenuListCounter.dt.totalPages);
                        } else
                            Toast.makeText(context, networkMenuListCounter.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    private BaseAdapter getBusPageAdapter(NetworkSeeAllCommonResponse.BusinessPagesListResponse businessPagesListResponse) {
        String imgPath = businessPagesListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return businessPagesListResponse.dt.size();
            }

            @Override
            public NetworkSeeAllCommonResponse.BusinessPagesListResponse.Dt getItem(int position) {
                return businessPagesListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_network_pages, parent, false);

                NetworkSeeAllCommonResponse.BusinessPagesListResponse.Dt row = getItem(position);
                View.OnClickListener openBusinessPage = v -> {
                    Intent intent = new Intent(context, BusinessActivity.class);
                    intent.putExtra("business_page_id", row.businessPageId);
                    startActivity(intent);
                };

                ImageView page_logo_iv = view.findViewById(R.id.page_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_user_pic).into(page_logo_iv);
                page_logo_iv.setOnClickListener(openBusinessPage);
                TextView page_name_tv = view.findViewById(R.id.page_name_tv);
                page_name_tv.setText(row.busName);
                page_name_tv.setOnClickListener(openBusinessPage);
                TextView page_member_tv = view.findViewById(R.id.page_member_tv);
                page_member_tv.setText(row.members + " Members");

                MaterialButton page_unfollow_mbtn = view.findViewById(R.id.page_unfollow_mbtn);
                page_unfollow_mbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("business_page_id", row.businessPageId);
                        apiInterface.PAGE_UNFOLLOW_CALL(hashMap).enqueue(new MyApiCallback(progressBar) {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status)
                                            removeItem(position);
                                        else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                businessPagesListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private BaseAdapter getCommunityGroupAdapter(NetworkSeeAllCommonResponse.CommunitysListResponse communitysListResponse) {
        String imgPath = communitysListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return communitysListResponse.dt.size();
            }

            @Override
            public NetworkSeeAllCommonResponse.CommunitysListResponse.Dt getItem(int position) {
                return communitysListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_network_group, parent, false);

                NetworkSeeAllCommonResponse.CommunitysListResponse.Dt row = getItem(position);
                View.OnClickListener openGroup = v -> {
                    Intent intent = new Intent(context, CommunityActivity.class);
                    intent.putExtra("community_master_id", row.communityMasterId);
                    startActivity(intent);
                };

                ImageView group_logo_iv = view.findViewById(R.id.group_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_user_pic).into(group_logo_iv);
                group_logo_iv.setOnClickListener(openGroup);
                TextView group_name_tv = view.findViewById(R.id.group_name_tv);
                group_name_tv.setText(row.name);
                group_name_tv.setOnClickListener(openGroup);
                TextView group_members = view.findViewById(R.id.group_members);
                group_members.setText(row.members + " Members");

                MaterialButton group_exit_mbtn = view.findViewById(R.id.group_exit_mbtn);
                group_exit_mbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        HashMap hashMap = new HashMap();
                        hashMap.put("user_master_id", sessionPref.getUserMasterId());
                        hashMap.put("apiKey", sessionPref.getApiKey());
                        hashMap.put("community_master_id", row.communityMasterId);
                        hashMap.put("exit_user_master_id", sessionPref.getUserMasterId());
                        apiInterface.GROUP_EXIT_CALL(hashMap).enqueue(new MyApiCallback(progressBar) {
                            @Override
                            public void onResponse(Call call, Response response) {
                                super.onResponse(call, response);
                                progressBar.setVisibility(View.GONE);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                                        if (normalCommonResponse.status)
                                            removeItem(position);
                                        else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                communitysListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private BaseAdapter getFollowingsAdapter(NetworkSeeAllCommonResponse.FollowingsListResponse followingsListResponse) {
        String imgPath = followingsListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return followingsListResponse.dt.size();
            }

            @Override
            public NetworkSeeAllCommonResponse.FollowingsListResponse.Dt getItem(int position) {
                return followingsListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_following, parent, false);

                NetworkSeeAllCommonResponse.FollowingsListResponse.Dt row = getItem(position);
                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", row.userMasterId);
                    startActivity(intent);
                };

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUserProfile);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUserProfile);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                TextView member_mutual_conn_tv = view.findViewById(R.id.member_mutual_conn_tv);
                member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, followingsListResponse.myDt.connectUser).size() + " Mutual Connections");

                MaterialButton unfollow_following_mbtn = view.findViewById(R.id.unfollow_following_mbtn);
                unfollow_following_mbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkActionMange(new NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) removeItem(position);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, ActionName.UnFollowFollowing, row.userMasterId);
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                followingsListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private BaseAdapter getFollowerAdapter(NetworkSeeAllCommonResponse.FollowerListResponse followerListResponse) {
        String imgPath = followerListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return followerListResponse.dt.size();
            }

            @Override
            public NetworkSeeAllCommonResponse.FollowerListResponse.Dt getItem(int position) {
                return followerListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_follower, parent, false);

                NetworkSeeAllCommonResponse.FollowerListResponse.Dt row = getItem(position);
                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", row.userMasterId);
                    startActivity(intent);
                };

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUserProfile);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUserProfile);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                TextView member_mutual_conn_tv = view.findViewById(R.id.member_mutual_conn_tv);
                member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, followerListResponse.myDt.connectUser).size() + " Mutual Connections");

                MaterialButton remove_follower_mbtn = view.findViewById(R.id.remove_follower_mbtn);
                remove_follower_mbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        networkActionMange(new NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) removeItem(position);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, ActionName.RemoveFollower, row.userMasterId);
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                followerListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private BaseAdapter getFollowReqAdapter(NetworkSeeAllCommonResponse.FollowReqListResponse followReqListResponse) {
        String imgPath = followReqListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return followReqListResponse.dt.size();
            }

            @Override
            public NetworkSeeAllCommonResponse.FollowReqListResponse.Dt getItem(int position) {
                return followReqListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_follow_rq, parent, false);

                NetworkSeeAllCommonResponse.FollowReqListResponse.Dt row = getItem(position);
                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", row.userMasterId);
                    startActivity(intent);
                };

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUserProfile);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUserProfile);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);

                TextView member_mutual_conn_tv = view.findViewById(R.id.member_mutual_conn_tv);
                member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, followReqListResponse.myDt.connectUser).size() + " Mutual Connections");
                MaterialButton req_accept_mbtn = view.findViewById(R.id.req_accept_mbtn);
                req_accept_mbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call req_accept api and remove view from gridlayout
                        networkActionMange(new NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) removeItem(position);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, ActionName.FollowReqAccept, row.userMasterId);
                    }
                });
                MaterialButton req_reject_mbtn = view.findViewById(R.id.req_reject_mbtn);
                req_reject_mbtn.setOnClickListener(v -> {
                    networkActionMange(new NetworkActionCallback() {
                        @Override
                        public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                            if (normalCommonResponse.status) removeItem(position);
                            else
                                Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                        }
                    }, ActionName.FollowReqReject, row.userMasterId);
                });
                return view;
            }

            private void removeItem(int position) {
                followReqListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private BaseAdapter getConnectionAdapter(NetworkSeeAllCommonResponse.ConnectionListResponse connectionListResponse) {
        String imgPath = connectionListResponse.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return connectionListResponse.dt.size();
            }

            @Override
            public NetworkSeeAllCommonResponse.ConnectionListResponse.Dt getItem(int position) {
                return connectionListResponse.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                if (view == null)
                    view = LayoutInflater.from(context).inflate(R.layout.network_card_connection, parent, false);

                NetworkSeeAllCommonResponse.ConnectionListResponse.Dt row = getItem(position);
                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", row.userMasterId);
                    startActivity(intent);
                };

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUserProfile);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUserProfile);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);

                ImageView members_start_chat_iv = view.findViewById(R.id.members_start_chat_iv);
                members_start_chat_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ChatHistoryUsersActivity.class);
                        intent.putExtra("action", "startChat");
                        intent.putExtra("userId", row.userMasterId);
                        startActivity(intent);
                    }
                });

                ImageView connection_option = view.findViewById(R.id.connection_option);
                connection_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog optionDialog = new BottomSheetDialog(context);
                        optionDialog.setContentView(R.layout.common_option_dialog_layout);
                        LinearLayout remove_option_LL = optionDialog.findViewById(R.id.remove_option_LL);
                        remove_option_LL.setVisibility(View.VISIBLE);
                        remove_option_LL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                networkActionMange(new NetworkActionCallback() {
                                    @Override
                                    public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                        if (normalCommonResponse.status) removeItem(position);
                                        else
                                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                                    }
                                }, ActionName.RemoveConnection, row.userMasterId);
                            }
                        });
                        optionDialog.show();
                    }
                });
                return view;
            }

            private void removeItem(int position) {
                connectionListResponse.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    private BaseAdapter getInvitationReqAdapter(NetworkSuggestedListResponse.JsonDt.ConnReq connReq) {
        String imgPath = connReq.imgPath;
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return connReq.dt.size();
            }

            @Override
            public NetworkSuggestedListResponse.JsonDt.ConnReq.Dt getItem(int position) {
                return connReq.dt.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View inv_rq_view, ViewGroup parent) {
                if (inv_rq_view == null)
                    inv_rq_view = LayoutInflater.from(context).inflate(R.layout.network_card_invitation, parent, false);

                NetworkSuggestedListResponse.JsonDt.ConnReq.Dt row = getItem(position);
                View.OnClickListener openUserProfile = v -> {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("user_master_id", row.userMasterId);
                    startActivity(intent);
                };

                ImageView req_decline_iv = inv_rq_view.findViewById(R.id.req_decline_iv);
                MaterialButton req_decline_mbtn = inv_rq_view.findViewById(R.id.req_decline_mbtn);
                View.OnClickListener req_decline = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call req_decline api and remove from gridlayout
                        networkActionMange(new NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) removeItem(position);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, ActionName.InvReqDecline, row.userMasterId);
                    }
                };
                req_decline_iv.setOnClickListener(req_decline);
                req_decline_mbtn.setOnClickListener(req_decline);

                TextView member_tv = inv_rq_view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                member_tv.setOnClickListener(openUserProfile);
                TextView member_pos_tv = inv_rq_view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                ImageView member_profile_pic = inv_rq_view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                member_profile_pic.setOnClickListener(openUserProfile);
                TextView member_mutual_conn_tv = inv_rq_view.findViewById(R.id.member_mutual_conn_tv);
                member_mutual_conn_tv.setText(UserMaster.findMutualIds(row.connectUser, connReq.myDt.connectUser).size() + " Mutual Connections");
                MaterialButton req_accept_mbtn = inv_rq_view.findViewById(R.id.req_accept_mbtn);
                req_accept_mbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call req_accept api and remove view from gridlayout
                        networkActionMange(new NetworkActionCallback() {
                            @Override
                            public void apiCallBack(NormalCommonResponse normalCommonResponse) {
                                if (normalCommonResponse.status) removeItem(position);
                                else
                                    Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                            }
                        }, ActionName.InvReqAccept, row.userMasterId);
                    }
                });
                return inv_rq_view;
            }

            private void removeItem(int position) {
                connReq.dt.remove(position);
                notifyDataSetChanged();
            }
        };
        return baseAdapter;
    }

    void loadAllInvitationRequest() {
        main_ll.removeAllViews();
        networkSeeAllList(new NetworkSeeAllCallback() {
            @Override
            public void apiCallBack(Object responseBody) {
                NetworkSuggestedListResponse.JsonDt.ConnReq connReq = new Gson().fromJson(new Gson().toJson(responseBody), NetworkSuggestedListResponse.JsonDt.ConnReq.class);
                if (connReq.status) {
                    View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                    TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                    nt_list_title.setText("Invitation Request");
                    MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                    nt_list_seeall.setVisibility(View.GONE);
                    GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                    grid_lt.setAdapter(getInvitationReqAdapter(connReq));
                    CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
                    main_ll.addView(blankGridSt);
                } else Toast.makeText(context, connReq.msg, Toast.LENGTH_SHORT).show();
            }
        }, SeeAllFnName.connectReqUsersMaster);
    }

    interface NetworkSeeAllCallback {
        void apiCallBack(Object responseBody);
    }

    //connectReqUsersMaster / connectUsers / followReqUsers / followerUsers / followingUsers / userCommunitys / userBusinessPages / userEvents /
    //suggestedCityUser / suggestedIndustryUser / suggestedGroup / suggestedBusPages
    public static enum SeeAllFnName {
        connectReqUsersMaster("connectReqUsersMaster"), connectUsers("connectUsers"), followReqUsers("followReqUsers"), followerUsers("followerUsers"), followingUsers("followingUsers"), userCommunitys("userCommunitys"), userBusinessPages("userBusinessPages"), userEvents("userEvents"), suggestedCityUser("suggestedCityUser"), suggestedIndustryUser("suggestedIndustryUser"), suggestedGroup("suggestedGroup"), suggestedBusPages("suggestedBusPages");
        String val;

        SeeAllFnName(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }


    private void networkSeeAllList(NetworkSeeAllCallback networkSeeAllCallback, SeeAllFnName fnName) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("device", "ANDROID");
        //connectReqUsersMaster / connectUsers / followReqUsers / followerUsers / followingUsers / userCommunitys / userBusinessPages / userEvents /
        //suggestedCityUser / suggestedIndustryUser / suggestedGroup / suggestedBusPages
        hashMap.put("fnName", fnName.getVal());
        apiInterface.NETWORK_SEE_ALL_LIST(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
//                        Log.e(LogTag.TMP_LOG, response.message());
//                        Log.e(LogTag.TMP_LOG, new Gson().toJson(response.body()));
                        networkSeeAllCallback.apiCallBack(response.body());
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    interface NetworkActionCallback {
        void apiCallBack(NormalCommonResponse normalCommonResponse);
    }

    //InvReqAccept / InvReqDecline / SendInvReq / RemoveConnection / RemoveFollower / UnFollowFollowing / ReqFollow / FollowReqAccept / FollowReqReject
    enum ActionName {
        InvReqAccept("InvReqAccept"), InvReqDecline("InvReqDecline"), SendInvReq("SendInvReq"), RemoveConnection("RemoveConnection"), RemoveFollower("RemoveFollower"), UnFollowFollowing("UnFollowFollowing"), ReqFollow("ReqFollow"), FollowReqAccept("FollowReqAccept"), FollowReqReject("FollowReqReject");
        private String val;

        ActionName(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }


    void networkActionMange(NetworkActionCallback networkActionCallback, ActionName action, String userOpponentsId) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
//      InvReqAccept / InvReqDecline / SendInvReq / RemoveConnection / RemoveFollower / UnFollowFollowing / ReqFollow / FollowReqAccept / FollowReqReject
        hashMap.put("action", action.getVal());
        hashMap.put("opponentsId", userOpponentsId);
        apiInterface.NETWORK_ACTION_MANGE(hashMap).enqueue(new MyApiCallback(progressBar) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        networkActionCallback.apiCallBack(normalCommonResponse);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}