package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.connester.job.R;
import com.connester.job.function.SessionPref;

public class AddFeedsActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    LayoutInflater layoutInflater;
    LinearLayout feeds_add_ly;
    ImageView add_photos_feed_iv, add_video_feed_iv, add_text_link_feed_iv, back_iv;
    TextView submit_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feeds);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        layoutInflater = LayoutInflater.from(context);


        feeds_add_ly = findViewById(R.id.feeds_add_ly);
        add_photos_feed_iv = findViewById(R.id.add_photos_feed_iv);
        add_photos_feed_iv.setOnClickListener(v -> {
            addPhotosFeed();
        });
        add_video_feed_iv = findViewById(R.id.add_video_feed_iv);
        add_video_feed_iv.setOnClickListener(v -> {
            addVideoFeed();
        });
        add_text_link_feed_iv = findViewById(R.id.add_text_link_feed_iv);
        add_text_link_feed_iv.setOnClickListener(v -> {
            addTextLinkFeed();
        });
        back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(v -> {
            onBackPressed();
        });
        submit_post = findViewById(R.id.submit_post); // set direct in add*Feed Function


        addTextLinkFeed();
    }

    private View addPhotosFeed() {
        add_photos_feed_iv.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        add_video_feed_iv.clearColorFilter();
        add_text_link_feed_iv.clearColorFilter();
        View view = layoutInflater.inflate(R.layout.feed_add_photos_layout, null);

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }


    private View addVideoFeed() {
        add_video_feed_iv.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        add_photos_feed_iv.clearColorFilter();
        add_text_link_feed_iv.clearColorFilter();
        View view = layoutInflater.inflate(R.layout.feed_add_video_layout, null);

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }


    private View addTextLinkFeed() {
        add_text_link_feed_iv.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        add_video_feed_iv.clearColorFilter();
        add_photos_feed_iv.clearColorFilter();
        View view = layoutInflater.inflate(R.layout.feed_add_text_link_layout, null);

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }


}