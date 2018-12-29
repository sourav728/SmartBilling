package com.transvision.mbc.other;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.transvision.mbc.service.MyService;
public class WakeUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*Intent tracking_service = new Intent(context, MyService.class);
        context.startService(tracking_service);*/
    }
}
