package com.connester.job.activity.mycommunity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.connester.job.R;

public class GroupMembersFragment extends Fragment {

    public GroupMembersFragment(String communityMasterId, FrameLayout progressBar) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_members, container, false);
    }
}