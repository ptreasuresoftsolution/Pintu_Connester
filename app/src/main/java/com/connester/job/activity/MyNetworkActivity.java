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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.NetworkMenuListCounter;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSeeAllCommonResponse;
import com.connester.job.RetrofitConnection.jsontogson.NetworkSuggestedListResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

/****
 * Notes : member profile open click / group profile open click / business profile open click is remain********
 */
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

        setTopBar();
        setBottomNavBar();

        loadDefaultView();
    }


    private void setBottomNavBar() {
        ImageView navHome_btn = findViewById(R.id.navHome_btn), navNetwork_btn = findViewById(R.id.navNetwork_btn), navAddFeeds_btn = findViewById(R.id.navAddFeeds_btn), navNotification_btn = findViewById(R.id.navNotification_btn), navJob_btn = findViewById(R.id.navJob_btn);
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
                ImageView page_logo_iv = view.findViewById(R.id.page_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_groups_pic).into(page_logo_iv);
                TextView page_name_tv = view.findViewById(R.id.page_name_tv);
                page_name_tv.setText(row.busName);
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

    BaseAdapter getSuggestedGroupAdapter(NetworkSuggestedListResponse.JsonDt.SugGroup sugGroup) {
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
                ImageView group_logo_iv = view.findViewById(R.id.group_logo_iv);
                Glide.with(context).load(imgPath + row.logo).placeholder(R.drawable.default_groups_pic).into(group_logo_iv);
                TextView group_name_tv = view.findViewById(R.id.group_name_tv);
                group_name_tv.setText(row.name);
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
                NetworkSuggestedListResponse.JsonDt.SugGroup sugGroup = (NetworkSuggestedListResponse.JsonDt.SugGroup) responseBody;
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

    BaseAdapter getSuggestedIndustryUserAdapter(NetworkSuggestedListResponse.JsonDt.SugUserIndustry sugUserIndustry) {
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


                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
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
                NetworkSuggestedListResponse.JsonDt.SugUserIndustry sugUserIndustry = (NetworkSuggestedListResponse.JsonDt.SugUserIndustry) responseBody;
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


    BaseAdapter getSuggestedCityUserAdapter(NetworkSuggestedListResponse.JsonDt.SugUserCity sugUserCity) {
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


                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
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
                NetworkSuggestedListResponse.JsonDt.SugUserCity sugUserCity = (NetworkSuggestedListResponse.JsonDt.SugUserCity) responseBody;
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
                onBackPressed();
            }
        });

        LinearLayout see_all_connection_ll = dialog.findViewById(R.id.see_all_connection_ll);
        see_all_connection_ll.setOnClickListener(v -> {
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.ConnectionListResponse connectionListResponse = (NetworkSeeAllCommonResponse.ConnectionListResponse) responseBody;
                    if (connectionListResponse.status) {
                        View blankGridSt = layoutInflater.inflate(R.layout.network_grid_st, null);
                        TextView nt_list_title = blankGridSt.findViewById(R.id.nt_list_title);
                        nt_list_title.setText("My Connection");
                        MaterialButton nt_list_seeall = blankGridSt.findViewById(R.id.nt_list_seeall);
                        nt_list_seeall.setVisibility(View.GONE);
                        GridView grid_lt = blankGridSt.findViewById(R.id.grid_lt);
                        grid_lt.setAdapter(getConnectionAdapter(connectionListResponse));
                        CommonFunction.setGridViewHeightBasedOnChildren(grid_lt);
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
                loadAllInvitationRequest();
            }
        });
        TextView inv_rq_count_tv = dialog.findViewById(R.id.inv_rq_count_tv);

        LinearLayout see_all_follow_rq_ll = dialog.findViewById(R.id.see_all_follow_rq_ll);
        see_all_follow_rq_ll.setOnClickListener(v -> {
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.FollowReqListResponse followReqListResponse = (NetworkSeeAllCommonResponse.FollowReqListResponse) responseBody;
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
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.FollowerListResponse followerListResponse = (NetworkSeeAllCommonResponse.FollowerListResponse) responseBody;
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
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.FollowingsListResponse followingsListResponse = (NetworkSeeAllCommonResponse.FollowingsListResponse) responseBody;
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
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.CommunitysListResponse communitysListResponse = (NetworkSeeAllCommonResponse.CommunitysListResponse) responseBody;
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
            main_ll.removeAllViews();
            networkSeeAllList(new NetworkSeeAllCallback() {
                @Override
                public void apiCallBack(Object responseBody) {
                    NetworkSeeAllCommonResponse.BusinessPagesListResponse businessPagesListResponse = (NetworkSeeAllCommonResponse.BusinessPagesListResponse) responseBody;
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
        return null;
    }

    private BaseAdapter getCommunityGroupAdapter(NetworkSeeAllCommonResponse.CommunitysListResponse communitysListResponse) {
        return null;
    }

    private BaseAdapter getFollowingsAdapter(NetworkSeeAllCommonResponse.FollowingsListResponse followingsListResponse) {
        return null;
    }

    private BaseAdapter getFollowerAdapter(NetworkSeeAllCommonResponse.FollowerListResponse followerListResponse) {
        return null;
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

                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
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
                ImageView member_profile_pic = view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
                TextView member_tv = view.findViewById(R.id.member_tv);
                member_tv.setText(row.name);
                TextView member_pos_tv = view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);

                ImageView members_start_chat_iv = view.findViewById(R.id.members_start_chat_iv);
                members_start_chat_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //start chat with this user (**)
                    }
                });

                ImageView connection_option = view.findViewById(R.id.connection_option);
                connection_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //remove option show in bottom sheet menu (**)
                        removeItem(position);
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

    BaseAdapter getInvitationReqAdapter(NetworkSuggestedListResponse.JsonDt.ConnReq connReq) {
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
                TextView member_pos_tv = inv_rq_view.findViewById(R.id.member_pos_tv);
                member_pos_tv.setText(row.position);
                ImageView member_profile_pic = inv_rq_view.findViewById(R.id.member_profile_pic);
                Glide.with(context).load(imgPath + row.profilePic).placeholder(R.drawable.default_user_pic).into(member_profile_pic);
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
                NetworkSuggestedListResponse.JsonDt.ConnReq connReq = (NetworkSuggestedListResponse.JsonDt.ConnReq) responseBody;
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
    enum SeeAllFnName {
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