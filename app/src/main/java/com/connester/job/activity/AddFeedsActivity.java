package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.function.AppUtils;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.DateUtils;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.SessionPref;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        //for compress image and create application local dir
        activityResultLauncherForPhotos = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), result -> {
            /*for (int i = 0; i < result.size(); i++) {
                Uri imageUri = result.get(i);
                //do what do you want to do
                Log.e(LogTag.TMP_LOG, "File name : " + FilePath.getPath2(context, imageUri));
            }*/
            photosUri = result;
            grid_img.setAdapter(getPhotosBaseAdapter());
        });

        //for compress video and create application local dir
        activityResultLauncherForVideo = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            feed_video_uri = result;
            if (result != null) {
                if (feed_video != null) {
                    if (feed_video.getPlayer() != null && feed_video.getPlayer().isPlaying()) {
                        feed_video.getPlayer().stop();
                        feed_video.getPlayer().release();
                    }
                }
                ExoPlayer player = new ExoPlayer.Builder(context).build();
                feed_video.setPlayer(player);
                // Create and add media item
                MediaItem mediaItem = MediaItem.fromUri(feed_video_uri);
                player.addMediaItem(mediaItem);
                // Prepare exoplayer
                player.prepare();
            }
        });
    }

    ActivityResultLauncher activityResultLauncherForPhotos;
    ActivityResultLauncher activityResultLauncherForVideo;

    private View addPhotosFeed() {
        add_photos_feed_iv.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        add_video_feed_iv.clearColorFilter();
        add_text_link_feed_iv.clearColorFilter();
        stopVideoPlaying();
        View view = layoutInflater.inflate(R.layout.feed_add_photos_layout, null);

        grid_img = view.findViewById(R.id.grid_img);
        ImageView add_feed_photos = view.findViewById(R.id.add_feed_photos);
        add_feed_photos.setOnClickListener(v -> {
            try {
                activityResultLauncherForPhotos.launch(("image/*"));
            } catch (Exception exception) {
                AppUtils.printLogConsole("openGalleryForImageAndVideo", "Exception-------->" + exception.getMessage());
            }
        });


        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }

    GridView grid_img;
    List<Uri> photosUri;

    private BaseAdapter getPhotosBaseAdapter() {
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return photosUri.size();
            }

            @Override
            public Uri getItem(int position) {
                return photosUri.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = LayoutInflater.from(context).inflate(R.layout.feed_add_photos_grid_item, null);
                ImageView photo_iv = view.findViewById(R.id.photo_iv);
                Glide.with(context).load(getItem(position)).into(photo_iv);
                MaterialCardView remove_photos_cv = view.findViewById(R.id.remove_photos_cv);
                remove_photos_cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photosUri.remove(position);
                        notifyDataSetChanged();
                    }
                });
                return view;
            }
        };
        return baseAdapter;
    }

    private View addVideoFeed() {
        add_video_feed_iv.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        add_photos_feed_iv.clearColorFilter();
        add_text_link_feed_iv.clearColorFilter();
        View view = layoutInflater.inflate(R.layout.feed_add_video_layout, null);
        ImageView add_feed_video = view.findViewById(R.id.add_feed_video);
        add_feed_video.setOnClickListener(v -> {
            activityResultLauncherForVideo.launch(("video/*"));
        });
        feed_video = view.findViewById(R.id.feed_video);
        TextView time_ago_txt = view.findViewById(R.id.time_ago_txt);
        time_ago_txt.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "EE, MMM dd, hh:mma", DateUtils.TODAYDATETIMEforDB()));
        EditText pt_title = view.findViewById(R.id.pt_title);

        submit_post.setOnClickListener(v -> {
            Log.e(LogTag.TMP_LOG, "start compression");
//            Toast.makeText(context, "start compression", Toast.LENGTH_SHORT).show();

            List<Uri> list = new ArrayList<>();
            list.add(feed_video_uri);

            String fp = FilePath.getPath2(context, feed_video_uri);
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(fp);
            String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String rotation = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String has = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            String fCount = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
            String fileSize = CommonFunction.byteToScale(new File(fp).length());
            String size[] = fileSize.split(" ");
            float fileLength = Float.parseFloat(size[0]);
            String scale = size[1];
            Toast.makeText(context, "w :" + width + " & h :" + height + " & rotation :" + rotation, Toast.LENGTH_SHORT).show();
            Log.e(LogTag.TMP_LOG, "rotation :" + rotation + " & has :" + has + " & fCount :" + fCount + " & size :" + fileSize);
            boolean isLandscape = false;
            if (Integer.parseInt(rotation) == 0 && Integer.parseInt(width) > Integer.parseInt(height)) {
                isLandscape = true;
            }
            if (fileSize.contains("GB")) {
                Toast.makeText(context, "Video is too long size, Please try with another video!", Toast.LENGTH_LONG).show();
                return;
            } else if (fileSize.contains("MB") && fileLength > 10) { //required compression video
                //set fix size landscape(320 x 180, 640 x 360) and portrait (360 X 480)
            } else {
                postSubmitVideo(new File(fp), pt_title.getText().toString());
            }

//            try {
//                VideoCompressor.start(context,
//                        // => This is required
//                        list,
//                        // => Source can be provided as content uris
//                        true,
//                        // => isStreamable
//                        Environment.DIRECTORY_MOVIES,
//                        // => the directory to save the compressed video(s)
//                        new CompressionListener() {
//                            @Override
//                            public void onSuccess(int i, long l, @org.jetbrains.annotations.Nullable String s) {
//
//                                // On Compression success
//                                Log.d("TAG", "videoCompress i: " + i);
//
//                                Log.d("TAG", "videoCompress l: " + l);
//
//                                Log.d("TAG", "videoCompress s: " + s);
//                                Toast.makeText(context, "Completed compression", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onStart(int i) {
//                                // Compression start
//
//                            }
//
//                            @Override
//                            public void onFailure(int index, String failureMessage) {
//                                // On Failure
//                            }
//
//                            @Override
//                            public void onProgress(int index, float progressPercent) {
//                                // Update UI with progress value
//                                activity.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onCancelled(int index) {
//                                // On Cancelled
//                            }
//                        },
//                        new Configuration(VideoQuality.LOW,
//                                24, /*frameRate: int, or null*/
//                                false, /*isMinBitrateCheckEnabled*/
//                                null, /*videoBitrate: int, or null*/
//                                false, /*disableAudio: Boolean, or null*/
//                                false, /*keepOriginalResolution: Boolean, or null*/
//                                360.0, /*videoWidth: Double, or null*/
//                                480.0 /*videoHeight: Double, or null*/));
//                /*
//                VideoQuality: VERY_HIGH (original-bitrate * 0.6) , HIGH (original-bitrate * 0.4), MEDIUM (original-bitrate * 0.3), LOW (original-bitrate * 0.2), OR VERY_LOW (original-bitrate * 0.1)
//                isMinBitrateCheckEnabled: this means, don't compress if bitrate is less than 2mbps
//                frameRate: any fps value
//                videoBitrate: any custom bitrate value
//                disableAudio: true/false to generate a video without audio. False by default.
//                keepOriginalResolution: true/false to tell the library not to change the resolution.
//                videoWidth: custom video width.
//                videoHeight: custom video height.
//                 */
//            } catch (Exception e) {
////                throw new RuntimeException(e);
//                Log.e(LogTag.EXCEPTION, "Compress Exception", e);
//            }
        });
        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }

    private void postSubmitVideo(File file, String string) {
    }

    StyledPlayerView feed_video;
    Uri feed_video_uri;

    private void stopVideoPlaying() {
        if (feed_video != null) {
            if (feed_video.getPlayer() != null && feed_video.getPlayer().isPlaying()) {
                feed_video.getPlayer().stop();
                feed_video.getPlayer().release();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopVideoPlaying();
    }

    private View addTextLinkFeed() {
        add_text_link_feed_iv.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        add_video_feed_iv.clearColorFilter();
        add_photos_feed_iv.clearColorFilter();
        stopVideoPlaying();
        View view = layoutInflater.inflate(R.layout.feed_add_text_link_layout, null);

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }


}