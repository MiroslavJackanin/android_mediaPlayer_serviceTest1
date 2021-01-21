package sk.it.android.myapplication_servicetest1.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("NOTIFICATION_ACTION")
                .putExtra("ACTION", intent.getAction()));
    }
}
