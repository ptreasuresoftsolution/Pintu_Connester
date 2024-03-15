package com.connester.job.activity.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.connester.job.R;

public class BusinessPeopleFragment extends Fragment {


    public BusinessPeopleFragment(ScrollView scrollView, String businessPageId, FrameLayout progressBar) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_people, container, false);
    }
}