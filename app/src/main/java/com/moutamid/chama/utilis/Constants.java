package com.moutamid.chama.utilis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.chama.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Constants {
    public static Dialog dialog;
    public static final String STASH_USER = "STASH_USER";
    public static final String MAIN_ADMIN = "1TBhJA8LGCf4CG60I3lX2Am0Qwo1";
    public static final String RUNNING_TOPICS = "RUNNING_TOPICS";
    public static final String USER = "USER";
    public static final String STATUS = "STATUS";
    public static final String SOCO = "SOCO";
    public static final String KEY = "KEY";
    public static final String CHATS = "CHATS";
    public static final String SAVING = "SAVING";
    public static final String REPORT = "REPORT";
    public static final String MESSAGES = "MESSAGES";
    public static final String NORMAL = "NORMAL";
    public static final String TRANSACTIONS = "TRANSACTIONS";
    public static final String LOCK = "LOCK";
    public static final String TIMELINE = "TIMELINE";
    public static final String LAST_TIME = "LAST_TIME";
    public static final String WITHDRAW = "WITHDRAW";
    public static final String REMINDERS = "REMINDERS";
    public static final String TOPIC_KEY = "topic";
    public static final String TITLE_KEY = "title";
    public static final String BODY_KEY = "body";
    public static final String CHAT_ID = "CHAT_ID";
    public static final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static String getCurrentMonth() {
        return new SimpleDateFormat("MMM", Locale.getDefault()).format(new Date());
    }

    public static void initDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
    }

    public static void showDialog() {
        dialog.show();
    }

    public static void dismissDialog() {
        dialog.dismiss();
    }

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("chama");
        db.keepSynced(true);
        return db;
    }

    public static StorageReference storageReference() {
        return FirebaseStorage.getInstance().getReference().child("chama");
    }

    public static String getTime(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = System.currentTimeMillis();
        calendar.setTimeInMillis(timestamp);
        if (isSameWeek(currentTime, timestamp)) {
            return isSameDay(currentTime, timestamp) ?
                    new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.getTime()) :
                    new SimpleDateFormat("dd MMM", Locale.getDefault()).format(calendar.getTime());
        } else {
            return new SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.getTime());
        }
    }

    public static boolean isSameDay(long timestamp1, long timestamp2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(timestamp1).equals(sdf.format(timestamp2));
    }

    public static boolean isSameWeek(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR);
    }

    public static void checkApp(Activity activity) {
        String appName = "chama";

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if ((input = in != null ? in.readLine() : null) == null) break;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> {
                        new AlertDialog.Builder(activity)
                                .setMessage(msg)
                                .setCancelable(false)
                                .show();
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

}
