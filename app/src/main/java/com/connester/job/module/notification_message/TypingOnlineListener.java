package com.connester.job.module.notification_message;

import java.util.Timer;
import java.util.TimerTask;

public abstract class TypingOnlineListener {
    protected boolean isTyping = false;
    private Timer timer = new Timer();

    public abstract void online();

    public void setCron() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isTyping = false;
                online();
            }
        }, 1000);
    }

    public void typing() {
        isTyping = true;
        setCron();
    }

}
