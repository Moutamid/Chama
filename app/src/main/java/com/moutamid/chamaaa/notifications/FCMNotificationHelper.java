package com.moutamid.chamaaa.notifications;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fxn.stash.Stash;
import com.moutamid.chamaaa.utilis.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FCMNotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String FCM_API_URL = "https://fcm.googleapis.com/v1/projects/recover-projects-2/messages:send";
    private static String AUTHORIZATION_HEADER = "Bearer ";

    String CHAT_ID;
    boolean isReminder;
    private Context context;

    public FCMNotificationHelper(Context context, String CHAT_ID, boolean isReminder) {
        this.CHAT_ID = CHAT_ID;
        this.isReminder = isReminder;
        this.context = context;
    }

    public void sendNotification(String[] receiverId, String title, String body) {
        TokenManager tokenManager = new TokenManager(context);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Submit the task to the executor service
        Future<String> futureToken = executorService.submit(() -> {
            try {
                return tokenManager.getAccessToken();
            } catch (Exception e) {
                Log.e(TAG, "Error getting access token", e);
                return null;
            }
        });

        // Use the token after completion
        executorService.submit(() -> {
            try {
                String accessToken = futureToken.get(); // This blocks until the token is available
                if (accessToken != null) {
                    Log.d(TAG, "Access Token: " + accessToken);
                    AUTHORIZATION_HEADER += accessToken;
                    for (String id : receiverId) {
                        getReceiverToken(id, token -> {
                            if (token != null) {
                                Log.d(TAG, "sendNotification: TOKEN " + token);
                                sendFCMNotification(token, title, body);
                            } else {
                                Log.e(TAG, "Failed to retrieve FCM token for user: " + receiverId);
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Failed to get access token");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving access token", e);
            }
        });

        // Shut down the executor service
        executorService.shutdown();
    }

    private void getReceiverToken(String userId, TokenCallback callback) {
        // Get the token of the receiver user from firebase realtime database
        Constants.databaseReference().child(Constants.USER).child(userId).child("fcmToken").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getValue(String.class);
                callback.onTokenRetrieved(token);
            } else {
                Log.e(TAG, "Error fetching FCM token", task.getException());
                callback.onTokenRetrieved(null);
            }
        });
    }

    private void sendFCMNotification(String token, String title, String body) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject jsonBody = new JSONObject();
        try {
            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            message.put("token", token);
            message.put("notification", notification);
            jsonBody.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_API_URL, jsonBody,
                response -> {
                    Log.d(TAG, "Notification sent successfully: " + response.toString());
                    if (isReminder) {
                        Stash.clear(Constants.LAST_TIME);
                        Map<String, Object> reminder = new HashMap<>();
                        reminder.put("isReminder", false);
                        Constants.databaseReference().child(Constants.REMINDERS).child(CHAT_ID).updateChildren(reminder);
                    }
                },
                error -> Log.e(TAG, "Error sending notification: " + error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", AUTHORIZATION_HEADER);
                headers.put("Content-Type", "application/json; UTF-8");
                return headers;
            }
        };

        queue.add(request);
    }

    // Callback interface for retrieving the token
    public interface TokenCallback {
        void onTokenRetrieved(String token);
    }
}

