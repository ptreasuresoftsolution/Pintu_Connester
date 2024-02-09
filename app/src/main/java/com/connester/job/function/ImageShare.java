package com.connester.job.function;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageShare {


    public static void shareImages(ArrayList<Uri> imageUriArray, Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
            intent.setType("image/jpeg");
            intent.setPackage("com.whatsapp");
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
                intent.setType("image/jpeg");
                intent.setPackage("com.whatsapp.w4b");
                context.startActivity(intent);
            } catch (Exception secEx) {
                Toast.makeText(context, "WhatsApp Application Not install in your device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void shareVideo(ArrayList<Uri> imageUriArray, Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
            intent.setType("video/*");
            intent.setPackage("com.whatsapp");
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
                intent.setType("video/*");
                intent.setPackage("com.whatsapp.w4b");
                context.startActivity(intent);
            } catch (Exception secEx) {
                Toast.makeText(context, "WhatsApp Application Not install in your device", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void shareImagesInApps(Uri imageUri[], Context context) {
        try {
            Log.d(LogTag.CHECK_DEBUG, "img share : " + imageUri.toString());
            ArrayList<Uri> imageUriArray = new ArrayList<>();
            for (Uri uri : imageUri) {
                imageUriArray.add(uri);
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, "Post Img Share"));
        } catch (Exception e) {
            Log.e(LogTag.EXCEPTION, "Share Img", e);
        }
    }
    public static void shareVideoInApps(Uri imageUri[], Context context) {
        try {
            Log.d(LogTag.CHECK_DEBUG, "video share : " + imageUri.toString());
            ArrayList<Uri> imageUriArray = new ArrayList<>();
            for (Uri uri : imageUri) {
                imageUriArray.add(uri);
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray);
            intent.setType("video/*");
            context.startActivity(Intent.createChooser(intent, "Post video Share"));
        } catch (Exception e) {
            Log.e(LogTag.EXCEPTION, "Share video", e);
        }
    }

    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    static public Uri getLocalBitmapUri(Bitmap bmp, Context context,String packName,String stickerName) {
        Uri bmpUri = null;
        try {
            String folder = Constant.getStorageDirectoryPath(context);
//            Log.e(LogTag.CHECK_DEBUG,"folder Path : "+folder);
            File folderChk = new File(folder);
            if (!folderChk.exists()){
                folderChk.mkdirs();
            }
            File file = new File(folder, stickerName);
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
