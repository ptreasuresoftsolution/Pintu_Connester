package com.connester.job.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.connester.job.R;
import com.connester.job.function.AppUtils;
import com.connester.job.function.SessionPref;

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


        activityResultLauncherForGallery = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), result -> {
            /*for (int i = 0; i < result.size(); i++) {
                Uri imageUri = result.get(i);
                //do what do you want to do
                Log.e(LogTag.TMP_LOG, "File name : " + FilePath.getPath2(context, imageUri));
            }*/
            //for compress image and create application local dir
            //File imageFile = new BraveFilePicker().setActivity(AddFeedsActivity.this).setIsCompressImage(false).setIsTrimVide(false).setFileType(BraveFileType.IMAGE).setDestinationFilePath(AppUtils.getRandomImageFileName(AddFeedsActivity.this)).setContentUri(imageUri).getSourceFile();
            grid_img.setAdapter(getPhotosBaseAdapter(result));
        });
        /*activityResultLauncherForGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.e(LogTag.TMP_LOG, "CHECK A");
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                try {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            //do what do you want to do
                            File imageFile = new BraveFilePicker().setActivity(AddFeedsActivity.this).setIsCompressImage(false).setIsTrimVide(false).setFileType(BraveFileType.IMAGE).setDestinationFilePath(AppUtils.getRandomImageFileName(AddFeedsActivity.this)).setContentUri(imageUri).getSourceFile();
                            Log.e(LogTag.TMP_LOG, "File name : " + imageFile.getAbsolutePath());
                        }
                    } else if (data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        //do what do you want to do
                        File imageFile = new BraveFilePicker().setActivity(AddFeedsActivity.this).setIsCompressImage(false).setIsTrimVide(false).setFileType(BraveFileType.IMAGE).setDestinationFilePath(AppUtils.getRandomImageFileName(AddFeedsActivity.this)).setContentUri(selectedImageUri).getSourceFile();
                        Log.e(LogTag.TMP_LOG, "File name : " + imageFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    Log.e(LogTag.TMP_LOG, "Exception A");
                    AppUtils.printLogConsole("activityResultLauncherForGallery", "Exception-------->" + e.getMessage());
                }
               */
        /* try {
                    Uri selectedMediaUri = result.getData().getData();
                    if (selectedMediaUri.toString().contains("image") || selectedMediaUri.toString().contains("jpg") || selectedMediaUri.toString().contains("jpeg") || selectedMediaUri.toString().contains("png")) {
                        File imageFile = new BraveFilePicker().setActivity(AddFeedsActivity.this).setIsCompressImage(false).setIsTrimVide(false).setFileType(BraveFileType.IMAGE).setDestinationFilePath(AppUtils.getRandomImageFileName(AddFeedsActivity.this)).setContentUri(selectedMediaUri).getSourceFile();
                        // Do something with the result...
                        Log.e(LogTag.TMP_LOG, "File name : " + imageFile.getAbsolutePath());
                    } else {
                        //For Video
                        // Do something with the result...
                        Log.e(LogTag.TMP_LOG, "File name : " + selectedMediaUri.getPath());
                    }
                } catch (Exception e) {
                    Log.e(LogTag.TMP_LOG, "Exception A");
                    AppUtils.printLogConsole("activityResultLauncherForGallery", "Exception-------->" + e.getMessage());
                }*/
        /*
            }
        });*/
    }


    ActivityResultLauncher activityResultLauncherForGallery;
    GridView grid_img;

    private BaseAdapter getPhotosBaseAdapter(List<Uri> uriList) {
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return uriList.size();
            }

            @Override
            public Uri getItem(int position) {
                return uriList.get(position);
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
                return view;
            }
        };
        return baseAdapter;
    }

    private View addPhotosFeed() {
        add_photos_feed_iv.setColorFilter(ContextCompat.getColor(context, R.color.primary));
        add_video_feed_iv.clearColorFilter();
        add_text_link_feed_iv.clearColorFilter();
        View view = layoutInflater.inflate(R.layout.feed_add_photos_layout, null);

        grid_img = view.findViewById(R.id.grid_img);
        ImageView add_feed_photos = view.findViewById(R.id.add_feed_photos);
        add_feed_photos.setOnClickListener(v -> {
            try {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*");
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                activityResultLauncherForGallery.launch(intent);
                activityResultLauncherForGallery.launch(("image/*"));
            } catch (Exception exception) {
                AppUtils.printLogConsole("openGalleryForImageAndVideo", "Exception-------->" + exception.getMessage());
            }
        });


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