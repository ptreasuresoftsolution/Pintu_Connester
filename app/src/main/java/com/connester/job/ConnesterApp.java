package com.connester.job;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class ConnesterApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
