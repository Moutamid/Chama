package com.moutamid.chamaaa.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(Stash.getLong(Constants.LAST_TIME, System.currentTimeMillis()));
//            String title = "Meeting Reminder";
//            String body = "Reminder of impending meeting";
//            NotificationScheduler.scheduleAlarmNotification(context, calendar, new String[]{Constants.auth().getCurrentUser().getUid()}, title, body);
        }
    }
}
