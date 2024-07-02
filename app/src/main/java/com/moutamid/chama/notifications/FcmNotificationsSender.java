package com.moutamid.chama.notifications;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fxn.stash.Stash;
import com.moutamid.chama.utilis.Constants;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class FcmNotificationsSender {
    private static final String TAG = "FcmNotificationsSender";
    String body;
    private String fcmServerKey = "";
    Context mContext;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private RequestQueue requestQueue;
    String title;
    String CHAT_ID;
    String[] userFcmToken;

    public FcmNotificationsSender(String[] userFcmToken2, String title2, String body2, Context mContext, String CHAT_ID) {
        fcmServerKey = Stash.getString(Constants.KEY);
        this.userFcmToken = userFcmToken2;
        this.title = title2;
        this.body = body2;
        this.mContext = mContext;
        this.CHAT_ID = CHAT_ID;
    }

    public void SendNotifications() {
        this.requestQueue = Volley.newRequestQueue(this.mContext);
        for (String id : userFcmToken){
            Log.d(TAG, "SendNotifications: " + id);
            JSONObject mainObj = new JSONObject();
            try {
                mainObj.put("to", "/topics/" + id);
                JSONObject notiObject = new JSONObject();
                notiObject.put("title", this.title);
                notiObject.put("body", this.body);
                notiObject.put("icon", "icon");
                mainObj.put("notification", notiObject);
                this.requestQueue.add(new JsonObjectRequest(1, "https://fcm.googleapis.com/fcm/send", mainObj, new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Stash.clear(Constants.LAST_TIME);
                        Map<String, Object> reminder = new HashMap<>();
                        reminder.put("isReminder", false);
                        Constants.databaseReference().child(Constants.REMINDERS).child(CHAT_ID).updateChildren(reminder);
                        Log.e(TAG, "onResponse: response: " + response.toString());
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ERROR: " + error.getMessage());
                    }
                }) {
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("Content-Type", "application/json");
                        header.put("Authorization", "key=" + fcmServerKey);
                        return header;
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "SendNotifications: ERROR:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

