package com.example.notificationbridge;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.SharedPreferences;

public class NotificationCaptureService extends NotificationListenerService {
    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            Notification n = sbn.getNotification();
            JSONObject obj = new JSONObject();
            obj.put("time", System.currentTimeMillis());
            obj.put("app", sbn.getPackageName());
            obj.put("title", String.valueOf(n.extras.getCharSequence(Notification.EXTRA_TITLE, "")));
            obj.put("text", String.valueOf(n.extras.getCharSequence(Notification.EXTRA_TEXT, "")));
            SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
            JSONArray old = new JSONArray(prefs.getString("items", "[]"));
            JSONArray updated = new JSONArray();
            updated.put(obj);
            for (int i=0; i<old.length() && i<499; i++) updated.put(old.getJSONObject(i));
            prefs.edit().putString("items", updated.toString()).apply();
        } catch (Exception ignored) { }
    }
}
