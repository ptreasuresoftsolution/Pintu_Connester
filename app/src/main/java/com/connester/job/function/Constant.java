package com.connester.job.function;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Constant {

    public static String DOMAIN = "http://192.168.29.235";

    public static String getStorageDirectoryPath(Context context) {
        return Environment.getExternalStorageDirectory()
                + File.separator + "Android" + File.separator + "media" + File.separator + context.getPackageName();
    }

    public static String[] ON = {"Splash", "Intro", "Login"};
}
