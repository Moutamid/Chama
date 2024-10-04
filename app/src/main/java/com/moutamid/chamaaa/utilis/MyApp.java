package com.moutamid.chamaaa.utilis;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.stash.Stash;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "MyApp";
    @Override
    public void onCreate() {
        super.onCreate();
        Stash.init(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d(TAG, "onActivityStarted: ");
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.d(TAG, "onActivityResumed: ");
        updateStatus(true);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d(TAG, "onActivityPaused: ");
        updateStatus(false);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState: ");
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    private void updateStatus(boolean status) {
        Log.d(TAG, "updateStatus: " + status);
        String value = status ? "online" : "offline";
        if (Constants.auth().getCurrentUser() != null) {
            Constants.databaseReference().child(Constants.STATUS).child(Constants.auth().getCurrentUser().getUid())
                    .setValue(value);
        }
    }

}
