package com.transvision.mbc.other;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.transvision.mbc.MainActivity;
import com.transvision.mbc.R;
import com.transvision.mbc.SplashActivity;
import com.transvision.mbc.posting.SendingData;
import com.transvision.mbc.values.FunctionsCall;

import org.apache.commons.lang.StringUtils;

import static com.transvision.mbc.values.Constants.APPROVAL_NOTIFICATION;
import static com.transvision.mbc.values.Constants.PREF_NAME;
import static com.transvision.mbc.values.Constants.sPref_ROLE;
import static com.transvision.mbc.values.Constants.sPref_SUBDIVISION;

public class Approval_Notification extends BroadcastReceiver {
    Context Notification_context;
    FunctionsCall functionCalls;
    SendingData sendingData;
    SharedPreferences sPref;
    String subdivision = "";
    private static Handler handler = null;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case APPROVAL_NOTIFICATION:
                        if (StringUtils.startsWithIgnoreCase(sPref.getString(sPref_ROLE, ""), "AAO")) {
                            notification(getContext().getApplicationContext());
                        }
                        break;

                }
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification_context = context;
        functionCalls = new FunctionsCall();
        sendingData = new SendingData(context);
        sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        subdivision = (sPref.getString(sPref_SUBDIVISION, ""));
        Log.d("debug", "Subdivision in Approval_Notification " + subdivision);
        functionCalls.logStatus("Approval Notification Current Time: " + functionCalls.currentRecpttime());
        if (functionCalls.checkInternetConnection(context)) {
            SendingData.Approval_Details_Notification approval_details = sendingData.new Approval_Details_Notification(handler);
            approval_details.execute(subdivision, "0");
        } else functionCalls.logStatus("No Internet Connection...");

        if (functionCalls.checkInternetConnection(context)) {
            SendingData.Approval_Details_Notification approval_details = sendingData.new Approval_Details_Notification(handler);
            approval_details.execute(subdivision, "1");
        } else functionCalls.logStatus("No Internet Connection...");

    }
    private Context getContext() {
        return this.Notification_context;
    }
    private void notification(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        //loginActivity.showdialog(DLG_APK_UPDATE_SUCCESS);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        //build notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.alert_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.mbc_icon))
                        .setContentTitle("Approval Request")
                        .setContentText("New Approval Request Came..")
                        .setDefaults(Notification.DEFAULT_ALL) // must requires VIBRATE permission
                        .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                        .setAutoCancel(true);

        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //to post your notification to the notification bar with a id. If a notification with same id already exists, it will get replaced with updated information.
        notificationManager.notify(0, builder.build());
    }
}
