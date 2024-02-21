package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.function.Constant;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;
import com.connester.job.module.FeedsMaster;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    SessionPref sessionPref;
    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        activity = MainActivity.this;
        sessionPref = new SessionPref(context);
        redirectSettings();

        LinearLayout feeds_mainList = findViewById(R.id.feeds_mainList);
        ScrollView scrollView = findViewById(R.id.scrollView);
        FeedsMaster feedsMaster = new FeedsMaster(context,activity);
        feedsMaster.setNeedCloseBtn(true);
        feedsMaster.loadHomeFeeds(feeds_mainList,scrollView);
//        check();
    }

    LinearLayout feeds_mainList;
    ScrollView scrollView;

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.release();
//            cache.release();
////            player = null;
//        }
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        initializePlayer(6);
//    }

    ExoPlayer player;
    Cache cache;

    private void initializePlayer(int index) {
        View vv = feeds_mainList.getChildAt(index);
//                http://localhost/JobPortal/upload/images/feeds/202401130901293609.mp4;
        String fileName = "202401130901293609.mp4";
        String video_url = Constant.DOMAIN + ApiInterface.OFFLINE_FOLDER + "/upload/images/feeds/" + fileName;
        StyledPlayerView video = vv.findViewById(R.id.feed_video);
        player = new ExoPlayer.Builder(context).build();

//        player.setPlayWhenReady(true);
        cache = new SimpleCache(new File(getCacheDir(), "random" + fileName), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 50));// new okhttp3.Cache(new File("temp"),100*1024*1024);

//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(new CacheDataSource.Factory(cache, new DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(this, getPackageName())), CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)).createMediaSource(Uri.parse(video_url));
        DefaultHttpDataSource.Factory factory = new DefaultHttpDataSource.Factory();
        factory.setUserAgent(Util.getUserAgent(this, getPackageName()));

        CacheDataSource.Factory factoryCache = new CacheDataSource.Factory();
        factoryCache.setCache(cache);
        factoryCache.setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        factoryCache.setUpstreamDataSourceFactory(factory);

        MediaItem mediaItem = MediaItem.fromUri(video_url);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(factoryCache).createMediaSource(mediaItem);


        player.addMediaSource(mediaSource);
        player.prepare();
        video.setPlayer(player);
//        player.addListener(new Player.Listener() {
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                switch (playbackState) {
//                    case Player.STATE_READY:
//                        if (playWhenReady) {
//                            Log.i("Video Player", "State Ready " + fileName);
//                        }
//                        break;
//                    case Player.STATE_BUFFERING:
//                        Log.i("Video Player", "Buffering " + fileName);
//                        break;
//                }
//            }
//        });
    }

    private void check() {
        feeds_mainList = findViewById(R.id.feeds_mainList);
        scrollView = findViewById(R.id.scrollView);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View vv[] = new View[12];
        for (int i = 0; i < 12; i++) {
            vv[i] = layoutInflater.inflate(R.layout.feeds_photos_layout, null);
            if (i == 6) {
                vv[i] = layoutInflater.inflate(R.layout.feeds_video_layout, null);
            }
            feeds_mainList.addView(vv[i], i);
        }
        View vx = layoutInflater.inflate(R.layout.feeds_content_layout, null);
        feeds_mainList.addView(vx, -1);
        View find = feeds_mainList.getChildAt(6);
        TextView fullname_txt = find.findViewById(R.id.fullname_txt);
        fullname_txt.setText("Check Layout 6");
        fullname_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogDemo();
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (videoVerticallyInView(find, scrollView.getScrollY())) {
                    Log.e(LogTag.TMP_LOG, "PLAY");
                    if (!player.isPlaying() && !player.isLoading()) {
                        player.play();
                    }
                } else {
                    Log.e(LogTag.TMP_LOG, "PAUSE");
                    if (player.isPlaying()) {
                        player.pause();
                    }
                }
            }
        });
    }

    void openDialogDemo() {
        // view set with fit content
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
//        bottomSheetDialog.setContentView(R.layout.feeds_title_common_layout);
//        bottomSheetDialog.show();

//        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //view set with fit screen set comment dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
//        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // bottomSheetDialog.getBehavior().setFitToContents(false); // not add this line then fit to content(not show in full screen)
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetDialog.getBehavior().setPeekHeight(getScreenResolution(context)[1]);
        View view = LayoutInflater.from(context).inflate(R.layout.feeds_comment_list_dialog_layout, null);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getScreenResolution(context)[1]);
//        view.setLayoutParams(layoutParams);

//        EditText comment_input = view.findViewById(R.id.comment_input);
//        InputMethodManager inputMethodManager = (InputMethodManager) context
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(comment_input, 0);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public boolean videoVerticallyInView(View videoView, int scrollY) {
        int maintainPix = 30;
        int displayHeight = getScreenResolution(context)[1];
//        int location[] = new int[2];
//        videoView.getLocationOnScreen(location);
//        int viewX = location[0];
//        int viewY = location[1];
        int viewHeight = videoView.getHeight();
        float positionOnPage = videoView.getY();
        boolean topIn = false;
        boolean bottomIn = false;
//        Log.e(LogTag.TMP_LOG, "ScrollY:" + scrollY + " & location Y:" + viewY + " & View ON Y:" + positionOnPage + " & height:" + viewHeight + " & D-height:" + displayHeight);
        if ((scrollY - maintainPix) > (positionOnPage - displayHeight) && scrollY < (positionOnPage - maintainPix)) {
//            Log.e(LogTag.TMP_LOG, "VIEW TOP INNN....");
            topIn = true;
        }
        if ((scrollY - maintainPix) > (positionOnPage - displayHeight + viewHeight) && scrollY < (positionOnPage - maintainPix + viewHeight)) {
//            Log.e(LogTag.TMP_LOG, "VIEW BOTTOM INNN....");
            bottomIn = true;
        }
        return topIn && bottomIn;
    }

    private int[] getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return new int[]{width, height};
    }

    private void redirectSettings() {
        Intent intent = getIntent();
        if (sessionPref.getUserName().isEmpty() || sessionPref.getUserName().equalsIgnoreCase("")) {//check setupOne
            startActivity(new Intent(context, StepActivity.class));
            finish();
        }
        if (!sessionPref.isLogin()) { // check is notLogin
            startActivity(new Intent(context, SignInActivity.class));
            finish();
        }
        //redirect triggers Click from notification
        if (intent != null) {
            if (intent.getStringExtra("trigger") != null &&
                    intent.getStringExtra("trigger").equalsIgnoreCase("EditProfileActivity")) {
                startActivity(new Intent(context, EditProfileActivity.class));
            }
        }
    }
}