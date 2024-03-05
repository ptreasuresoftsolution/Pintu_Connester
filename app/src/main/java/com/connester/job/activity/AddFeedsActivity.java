package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.abedelazizshe.lightcompressorlibrary.config.Configuration;
import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.GetLinkMetaDataResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.DateUtils;
import com.connester.job.function.FilePath;
import com.connester.job.function.LogTag;
import com.connester.job.function.MyApiCallback;
import com.connester.job.function.SessionPref;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;
import retrofit2.Call;
import retrofit2.Response;

public class AddFeedsActivity extends AppCompatActivity {
    Context context;
    Activity activity;
    SessionPref sessionPref;
    LayoutInflater layoutInflater;
    ApiInterface apiInterface;


    LinearLayout feeds_add_ly;
    ImageView add_photos_feed_iv, add_video_feed_iv, add_text_link_feed_iv, back_iv;
    TextView submit_post;

    String feedFor = "USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feeds);
        context = this;
        activity = this;
        sessionPref = new SessionPref(context);
        layoutInflater = LayoutInflater.from(context);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if (getIntent() != null) {
            feedFor = getIntent().getStringExtra("feed_for");
        }

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
        TextView time_ago_txt = view.findViewById(R.id.time_ago_txt);
        time_ago_txt.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "EE, MMM dd, hh:mma", DateUtils.TODAYDATETIMEforDB()));
        ImageView feeds_title_img = view.findViewById(R.id.feeds_title_img);
        Glide.with(context).load(sessionPref.getUserProfilePic()).placeholder(R.drawable.default_user_pic).into(feeds_title_img);
        TextView fullname_txt = view.findViewById(R.id.fullname_txt);
        fullname_txt.setText(sessionPref.getUserFullName());

        EditText pt_title = view.findViewById(R.id.pt_title);
        grid_img = view.findViewById(R.id.grid_img);
        ImageView add_feed_photos = view.findViewById(R.id.add_feed_photos);
        add_feed_photos.setOnClickListener(v -> {
            activityResultLauncherForPhotos.launch(("image/*"));
        });
        submit_post.setOnClickListener(v -> {
            Toast.makeText(context, "Click img compress", Toast.LENGTH_LONG).show();
            List<File> photoFiles = new ArrayList<>();
            for (Uri photoUri : photosUri) {
                try {
                    File pFile = new File(FilePath.getPath2(context, photoUri));
                    File imgFile = new Compressor(context)
                            .setMaxWidth(1080)
                            .setMaxWidth(800)
                            .setQuality(50)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(context.getFilesDir().getAbsolutePath())
                            .compressToFile(pFile);
                    photoFiles.add(imgFile);
                    Log.e(LogTag.TMP_LOG, "Path :" + imgFile.getAbsolutePath());
                } catch (Exception e) {
                    Log.e(LogTag.EXCEPTION, "Image Compress Exception", e);
                }
            }
            Log.e(LogTag.TMP_LOG, "Image Compress Completed");
            postSubmitPhoto(photoFiles, pt_title.getText().toString());
        });

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }

    private void postSubmitPhoto(List<File> photoFiles, String string) {
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
        ImageView feeds_title_img = view.findViewById(R.id.feeds_title_img);
        Glide.with(context).load(sessionPref.getUserProfilePic()).placeholder(R.drawable.default_user_pic).into(feeds_title_img);
        TextView fullname_txt = view.findViewById(R.id.fullname_txt);
        fullname_txt.setText(sessionPref.getUserFullName());

        submit_post.setOnClickListener(v -> {
//            Log.e(LogTag.TMP_LOG, "start compression");
//            Toast.makeText(context, "start compression", Toast.LENGTH_SHORT).show();
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
//            Toast.makeText(context, "w :" + width + " & h :" + height + " & rotation :" + rotation, Toast.LENGTH_SHORT).show();
            Log.d(LogTag.CHECK_DEBUG, "w :" + width + " & h :" + height + " rotation :" + rotation + " & has :" + has + " & fCount :" + fCount + " & size :" + fileSize);
            boolean isLandscape = false;
            if (Integer.parseInt(rotation) == 0 && Integer.parseInt(width) > Integer.parseInt(height)) {
                isLandscape = true;
            }
            if (fileSize.contains("GB")) {
                Toast.makeText(context, "Video is too long size, Please try with another video!", Toast.LENGTH_LONG).show();
                return;
            } else if (fileSize.contains("MB") && fileLength > 10) { //required compression video
                //set fix size landscape(320 x 180, 640 x 360) and portrait (360 X 480)
                double videoWidth = 360.0;
                double videoHeight = 480.0;
                if (isLandscape) {
                    videoWidth = 640.0;
                    videoHeight = 360.0;
                }
                try {
                    List<Uri> list = new ArrayList<>();
                    list.add(feed_video_uri);
                    CommonFunction.PleaseWaitShow(context);
                    CommonFunction.PleaseWaitShowMessage("Video is compressing...");
                    VideoCompressor.start(context,
                            // => This is required
                            list,
                            // => Source can be provided as content uris
                            true,
                            // => isStreamable
                            context.getFilesDir().getAbsolutePath(),
                            // => the directory to save the compressed video(s)
                            new CompressionListener() {
                                @Override
                                public void onSuccess(int i, long l, @org.jetbrains.annotations.Nullable String filePath) {

                                    // On Compression success
                                    Log.d("TAG", "videoCompress i: " + i);
                                    Log.d("TAG", "videoCompress l: " + l);
                                    Log.d("TAG", "videoCompress s: " + filePath);
//                                    Toast.makeText(context, "Completed compression", Toast.LENGTH_SHORT).show();
                                    CommonFunction.PleaseWaitShowMessage("Video is compressing completed...");
                                    CommonFunction.PleaseWaitShowMessage("Please Wait... Data upload to server...");
                                    postSubmitVideo(new File(filePath), pt_title.getText().toString());
                                }

                                @Override
                                public void onStart(int i) {
                                    // Compression start
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            CommonFunction.PleaseWaitShowMessage("Video is compressing start...");
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int index, String failureMessage) {
                                    // On Failure
                                    CommonFunction.dismissDialog();
                                    Toast.makeText(context, "Video compressing failed, try again/another video!", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onProgress(int index, float progressPercent) {
                                    // Update UI with progress value
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            CommonFunction.PleaseWaitShowMessage("Video is compressing... " + progressPercent + "%");
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(int index) {
                                    // On Cancelled
                                    CommonFunction.dismissDialog();
                                    Toast.makeText(context, "Video compressing cancelled, try again/another video!", Toast.LENGTH_LONG).show();
                                }
                            },
                            new Configuration(VideoQuality.LOW,
                                    24, /*frameRate: int, or null*/
                                    false, /*isMinBitrateCheckEnabled*/
                                    null, /*videoBitrate: int, or null*/
                                    false, /*disableAudio: Boolean, or null*/
                                    false, /*keepOriginalResolution: Boolean, or null*/
                                    videoWidth, /*videoWidth: Double, or null*/
                                    videoHeight /*videoHeight: Double, or null*/));
                        /*
                        VideoQuality: VERY_HIGH (original-bitrate * 0.6) , HIGH (original-bitrate * 0.4), MEDIUM (original-bitrate * 0.3), LOW (original-bitrate * 0.2), OR VERY_LOW (original-bitrate * 0.1)
                        isMinBitrateCheckEnabled: this means, don't compress if bitrate is less than 2mbps
                        frameRate: any fps value
                        videoBitrate: any custom bitrate value
                        disableAudio: true/false to generate a video without audio. False by default.
                        keepOriginalResolution: true/false to tell the library not to change the resolution.
                        videoWidth: custom video width.
                        videoHeight: custom video height.
                         */
                } catch (Exception e) {
                    Toast.makeText(context, "This is video file is corrupted!, Try with another video!", Toast.LENGTH_SHORT).show();
                    Log.e(LogTag.EXCEPTION, "Compress Exception", e);
                }
            } else {
                CommonFunction.PleaseWaitShow(context);
                CommonFunction.PleaseWaitShowMessage("Please Wait... Data upload to server...");
                postSubmitVideo(new File(fp), pt_title.getText().toString());
            }
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
        TextView time_ago_txt = view.findViewById(R.id.time_ago_txt);
        time_ago_txt.setText(DateUtils.getStringDate("yyyy-MM-dd HH:mm:ss", "EE, MMM dd, hh:mma", DateUtils.TODAYDATETIMEforDB()));
        ImageView feeds_title_img = view.findViewById(R.id.feeds_title_img);
        Glide.with(context).load(sessionPref.getUserProfilePic()).placeholder(R.drawable.default_user_pic).into(feeds_title_img);
        TextView fullname_txt = view.findViewById(R.id.fullname_txt);
        fullname_txt.setText(sessionPref.getUserFullName());

        EditText pt_title = view.findViewById(R.id.pt_title);
        EditText link_et = view.findViewById(R.id.link_et);
        MaterialCardView link_details_cv = view.findViewById(R.id.link_details_cv);
        link_details_cv.setVisibility(View.GONE);
        SimpleDraweeView link_img = view.findViewById(R.id.link_img);
        TextView link_title = view.findViewById(R.id.link_title);
        TextView link_url = view.findViewById(R.id.link_url);
        link_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getLinkMetaData(s, new GetLinkMetaDataCallback() {
                    @Override
                    public void CallBack(GetLinkMetaDataResponse.Dt dt) {
                        boolean allNull = true;
                        link_details_cv.setVisibility(View.VISIBLE);
                        link_img.setVisibility(View.GONE);
                        if (dt != null && dt.img != null && !dt.img.trim().equalsIgnoreCase("")) {
                            link_img.setVisibility(View.VISIBLE);
                            link_img.setTag(dt.img);
                            Glide.with(context).load(dt.img).into(link_img);
                            allNull = false;
                        } else link_img.setTag("");
                        link_title.setVisibility(View.GONE);
                        if (dt != null && dt.title != null && !dt.title.trim().equalsIgnoreCase("")) {
                            link_title.setVisibility(View.VISIBLE);
                            link_title.setText(dt.title);
                            allNull = false;
                        } else link_title.setText("");
                        link_url.setVisibility(View.GONE);
                        if (dt != null && dt.url != null && !dt.url.trim().equalsIgnoreCase("")) {
                            link_url.setVisibility(View.VISIBLE);
                            link_url.setText(dt.url);
                            allNull = false;
                        } else link_url.setText("");
                        if (allNull)
                            link_details_cv.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkJson = "";
                if (link_details_cv.getVisibility() == View.VISIBLE &&
                        !link_url.getText().toString().trim().equalsIgnoreCase("") &&
                        link_url.getVisibility() == View.VISIBLE) {
                    //{"link":"https://www.linkedin.com/","dataSrc":"https://static.licdn.com/scds/common/u/images/logos/favicons/v1/favicon.ico","linkTitle":"LinkedIn: Log In or Sign Up"}
                    HashMap hashMap = new HashMap();
                    hashMap.put("link", link_url.getText().toString());
                    hashMap.put("dataSrc", link_img.getTag().toString());
                    hashMap.put("linkTitle", link_title.getText().toString());
                    linkJson = new Gson().toJson(hashMap);
                }
                postSubmitTextLink(pt_title.getText().toString(), linkJson);
            }
        });

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }

    private void postSubmitTextLink(String string, String linkJson) {
    }

    interface GetLinkMetaDataCallback {
        void CallBack(GetLinkMetaDataResponse.Dt dt);
    }

    private void getLinkMetaData(CharSequence s, GetLinkMetaDataCallback getLinkMetaDataCallback) {

        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("url", s.toString());
        apiInterface.GET_LINK_META_DATA_CALL(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        GetLinkMetaDataResponse getLinkMetaDataResponse = (GetLinkMetaDataResponse) response.body();
                        if (getLinkMetaDataResponse.status) {
                            getLinkMetaDataCallback.CallBack(getLinkMetaDataResponse.dt);
                        } else {
                            if (getLinkMetaDataResponse.errorMsg != null && !getLinkMetaDataResponse.errorMsg.contains("must not be empty"))
                                Toast.makeText(context, getLinkMetaDataResponse.msg, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "Invalid Link! URL", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }


}