package com.moutamid.chamaaa.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.moutamid.chamaaa.utilis.Constants;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.d(TAG, "NotificationReceiver: ");
            String[] topics = intent.getStringArrayExtra(Constants.TOPIC_KEY);
            String title = intent.getStringExtra(Constants.TITLE_KEY);
            String body = intent.getStringExtra(Constants.BODY_KEY);
            String CHAT_ID = intent.getStringExtra(Constants.CHAT_ID);
            new FCMNotificationHelper(context, CHAT_ID, true).sendNotification(topics, title, body);
        } else {
            Log.d(TAG, "onReceive: NULL");
        }
    }
}