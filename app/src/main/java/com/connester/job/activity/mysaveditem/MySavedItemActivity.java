package com.connester.job.activity.mysaveditem;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.connester.job.R;
import com.connester.job.function.SessionPref;
import com.connester.job.module.UserMaster;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MySavedItemActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    ImageView back_iv;
    ViewPager view_pager;
    TabLayout tab_layout;
    List<Fragment> fragments = new ArrayList<>();
    List<String> fragmentsTitle = new ArrayList<>();
UserMaster userMaster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_saved_item);
        context = MySavedItemActivity.this;
        activity = MySavedItemActivity.this;
        userMaster = new UserMaster(context);
        sessionPref = new SessionPref(context);

        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });

        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        userMaster.getUserClmData(new UserMaster.CallBack() {
            @Override
            public void DataCallBack(Response response) {

                fragments.add(new PostsFragment());
                fragmentsTitle.add("Posts");
                fragments.add(new JobFragment());
                fragmentsTitle.add("Job");
                fragments.add(new EventFragment());
                fragmentsTitle.add("Event");
                view_pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                    @Nullable
                    @Override
                    public CharSequence getPageTitle(int position) {
                        return fragmentsTitle.get(position);
                    }

                    @Override
                    public int getCount() {
                        return fragments.size();
                    }

                    @NonNull
                    @Override
                    public Fragment getItem(int position) {
                        return fragments.get(position);
                    }
                });
            }
        },"*",true);
    }
}