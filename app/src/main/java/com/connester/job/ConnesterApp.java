package com.connester.job;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.connester.job.function.Constant;
import com.facebook.drawee.backends.pipeline.Fresco;

public class ConnesterApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        createNotificationChangel();
    }

    private void createNotificationChangel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChanel = new NotificationChannel(Constant.channel_id, getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChanel);
        }
    }
}
