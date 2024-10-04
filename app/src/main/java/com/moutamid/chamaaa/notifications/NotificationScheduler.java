package com.moutamid.chamaaa.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fxn.stash.Stash;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.Calendar;
import java.util.Random;

public class NotificationScheduler {
    private static final String TAG = "NotificationScheduler";

    public static void scheduleAlarmNotification(Context context, Calendar calendar, String[] topic, String title, String body, String chatID) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "scheduleAlarmNotification: ");
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(Constants.TOPIC_KEY, topic);
        intent.putExtra(Constants.TITLE_KEY, title);
        intent.putExtra(Constants.BODY_KEY, body);
        intent.putExtra(Constants.CHAT_ID, chatID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                new Random().nextInt(100), intent,//NOTIFICATION_ID
                PendingIntent.FLAG_IMMUTABLE);//FLAG_UPDATE_CURRENT
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Stash.put(Constants.LAST_TIME, calendar.getTimeInMillis());
    }


}