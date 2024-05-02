package com.connester.job.function;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Constant {

    public static String DOMAIN = "https://connester.ptreasure.co.in";
//    public static String DOMAIN = "http://192.168.29.235";
    public static String userEmail = "user@connester.com";
    public static String channel_id = "Connester";

    public static String getStorageDirectoryPath(Context context) {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + "Android" + File.separator + "media" + File.separator + context.getPackageName();
        if (!new File(path).exists())
            new File(path).mkdirs();
        return path;
    }

    public static String[] ON = {"Splash", "Intro", "Login"};
}
