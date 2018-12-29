package com.transvision.mbc.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.transvision.mbc.other.Approval_Notification;
import com.transvision.mbc.values.FunctionsCall;

public class MyService extends Service {
    FunctionsCall functionCalls;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionCalls = new FunctionsCall();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start_notification_check();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop_notification_check();
    }

    private void start_notification_check() {
        functionCalls.logStatus("Approval_Notification Checking..");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Approval_Notification.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmRunning) {
            functionCalls.logStatus("Approval_Notification Started..");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (20000), pendingIntent);
        } else functionCalls.logStatus("Approval_Notification Already running..");
    }

    private void stop_notification_check() {
        functionCalls.logStatus("Approval_Notification Checking..");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), Approval_Notification.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmRunning) {
            functionCalls.logStatus("Approval_Notification Stopping..");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        } else functionCalls.logStatus("Approval_Notification Not yet Started..");
    }
}
