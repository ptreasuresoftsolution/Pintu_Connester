package com.connester.job.activity.nonslug;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.connester.job.RetrofitConnection.ApiClient;
import com.connester.job.RetrofitConnection.ApiInterface;
import com.connester.job.RetrofitConnection.jsontogson.GetLinkMetaDataResponse;
import com.connester.job.RetrofitConnection.jsontogson.NormalCommonResponse;
import com.connester.job.function.CommonFunction;
import com.connester.job.function.Constant;
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
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    String business_page_id = "0";
    String community_master_id = "0";

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
            feedFor = getIntent().getStringExtra("feed_for") != null ? getIntent().getStringExtra("feed_for") : "USER";
            if (feedFor.equalsIgnoreCase("BUSINESS")) {
                business_page_id = getIntent().getStringExtra("business_page_id");
            } else if (feedFor.equalsIgnoreCase("COMMUNITY")) {
                community_master_id = getIntent().getStringExtra("community_master_id");
            }
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
            if (result != null) {
                feed_video_uri = result;
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
            if (pt_title.getText().toString().trim().equalsIgnoreCase("")) {
                Toast.makeText(context, "Please post title/text/description...", Toast.LENGTH_SHORT).show();
                pt_title.setError("Please Enter some text!");
                return;
            }
            CommonFunction.PleaseWaitShow(context);
            CommonFunction.PleaseWaitShowMessage("Files is compressing...");
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
            CommonFunction.PleaseWaitShowMessage("Files is compressed completed");
            postSubmitPhoto(photoFiles, pt_title.getText().toString());
        });

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }

    //done testing completed
    private void postSubmitPhoto(List<File> photoFiles, String pt_title) {
        CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                .addFormDataPart("apiKey", sessionPref.getApiKey())
                .addFormDataPart("pt_title", pt_title);

        if (feedFor.equalsIgnoreCase("BUSINESS")) {
            builder.addFormDataPart("business_page_id", business_page_id);
        } else if (feedFor.equalsIgnoreCase("COMMUNITY")) {
            builder.addFormDataPart("community_master_id", community_master_id);
        }

        for (File file : photoFiles) {
            builder.addFormDataPart("images[]", file.getName(),
                    RequestBody.create(MediaType.parse(FilePath.getMimeType(file)), file));
        }
        RequestBody body = builder.build();
        apiInterface.FEED_ADD_PHOTOS_SUBMIT(body).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            for (File file : photoFiles) {
                                file.delete();
                            }
                            CommonFunction._LoadAlert(context, "Your Post is submited successfully").setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    onBackPressed();
                                }
                            });
                        } else
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
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
            if (pt_title.getText().toString().trim().equalsIgnoreCase("")) {
                Toast.makeText(context, "Please post title/text/description...", Toast.LENGTH_SHORT).show();
                pt_title.setError("Please Enter some text!");
                return;
            }
            String fp = FilePath.getPath2(context, feed_video_uri);
            String fileSize = CommonFunction.byteToScale(new File(fp).length());
            String size[] = fileSize.split(" ");
            float fileLength = Float.parseFloat(size[0]);
            String scale = size[1];

            if (fileSize.contains("GB")) {
                Toast.makeText(context, "Video is too long size, Please try with another video!", Toast.LENGTH_LONG).show();
                return;
            } else if (fileSize.contains("MB") && fileLength > 10) { //required compression video
                CommonFunction.PleaseWaitShow(context);
                CommonFunction.PleaseWaitShowMessage("Video is compressing...");
                Executors.newSingleThreadExecutor().execute(() -> {
                    //Background work here
                    try {
                        String filePath = SiliCompressor.with(context).compressVideo(feed_video_uri, Constant.getStorageDirectoryPath(context));
                        Log.e(LogTag.TMP_LOG, "pth: " + filePath);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                    CommonFunction.dismissDialog();
//                                    Toast.makeText(context, "Completed compression", Toast.LENGTH_SHORT).show();
                                CommonFunction.PleaseWaitShowMessage("Video is compressing completed...");
                                CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                                postSubmitVideo(new File(filePath), pt_title.getText().toString());
                            }
                        });
                    } catch (Exception e) {
                        CommonFunction.dismissDialog();
                        Toast.makeText(context, "Video compressing failed, try again/another video!", Toast.LENGTH_LONG).show();
                        Log.e(LogTag.EXCEPTION, "Video File Compressing Exception", e);
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        //UI Thread work here
                        //task is complete call
                    });
                });

            } else {
                CommonFunction.PleaseWaitShow(context);
                CommonFunction.PleaseWaitShowMessage("Please wait, data upload on server...");
                postSubmitVideo(new File(fp), pt_title.getText().toString());
            }
        });
        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }

    private void postSubmitVideo(File file, String pt_title) {
        Log.d(LogTag.CHECK_DEBUG, "File :" + file.getAbsolutePath());
        Log.d(LogTag.CHECK_DEBUG, "pt_title :" + pt_title);
//        Toast.makeText(context, "video submit", Toast.LENGTH_SHORT).show();
//        CommonFunction.dismissDialog();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("user_master_id", sessionPref.getUserMasterId())
                .addFormDataPart("apiKey", sessionPref.getApiKey())
                .addFormDataPart("pt_title", pt_title);

        if (feedFor.equalsIgnoreCase("BUSINESS")) {
            builder.addFormDataPart("business_page_id", business_page_id);
        } else if (feedFor.equalsIgnoreCase("COMMUNITY")) {
            builder.addFormDataPart("community_master_id", community_master_id);
        }
        builder.addFormDataPart("video", file.getName(),
                RequestBody.create(MediaType.parse(FilePath.getMimeType(file)), file));
        RequestBody body = builder.build();
        apiInterface.FEED_ADD_VIDEO_SUBMIT(body).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            file.delete();
                            CommonFunction._LoadAlert(context, "Your Post is submited successfully").setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    onBackPressed();
                                }
                            });
                        } else
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
                if (Patterns.WEB_URL.matcher(s).matches()) {
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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link_et.getText().toString().equalsIgnoreCase(""))
                    if (!Patterns.WEB_URL.matcher(link_et.getText().toString()).matches()) {
                        link_et.setError("Enter Valid URL!");
                        Toast.makeText(context, "Please enter valid url", Toast.LENGTH_SHORT).show();
                        return;
                    }
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
                Log.e(LogTag.TMP_LOG, "pt_title : " + pt_title.getText().toString());
                Log.e(LogTag.TMP_LOG, "linkJson : " + linkJson);
//                Toast.makeText(context, "TEXT LINK submit", Toast.LENGTH_SHORT).show();
                postSubmitTextLink(pt_title.getText().toString(), linkJson);
            }
        });

        feeds_add_ly.removeAllViews();
        feeds_add_ly.addView(view);
        return view;
    }

    private void postSubmitTextLink(String pt_title, String linkJson) {
        CommonFunction.PleaseWaitShow(context);
        HashMap hashMap = new HashMap();
        hashMap.put("user_master_id", sessionPref.getUserMasterId());
        hashMap.put("apiKey", sessionPref.getApiKey());
        hashMap.put("pt_title", pt_title);
        if (feedFor.equalsIgnoreCase("BUSINESS")) {
            hashMap.put("business_page_id", business_page_id);
        } else if (feedFor.equalsIgnoreCase("COMMUNITY")) {
            hashMap.put("community_master_id", community_master_id);
        }
        if (!linkJson.trim().equalsIgnoreCase("")) {
            hashMap.put("linkJson", linkJson);
        }
        apiInterface.FEED_ADD_TEXT_LINK_SUBMIT(hashMap).enqueue(new MyApiCallback() {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        NormalCommonResponse normalCommonResponse = (NormalCommonResponse) response.body();
                        if (normalCommonResponse.status) {
                            CommonFunction._LoadAlert(context, "Your Post is submited successfully").setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    onBackPressed();
                                }
                            });
                        } else
                            Toast.makeText(context, normalCommonResponse.msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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