package com.example.notificationbridge;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.content.ComponentName;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    EditText token, chat;
    LinearLayout history;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,36,32,24);
        TextView title = new TextView(this); title.setText("Notification Bridge"); title.setTextSize(25); root.addView(title);
        TextView desc = new TextView(this); desc.setText("Private on-device notification history. Choose an entry to send to Telegram."); root.addView(desc);
        Button access = new Button(this); access.setText("Grant notification access"); root.addView(access);
        access.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        token = new EditText(this); token.setHint("Telegram Bot Token"); token.setSingleLine(true); root.addView(token);
        chat = new EditText(this); chat.setHint("Telegram Chat ID"); chat.setSingleLine(true); root.addView(chat);
        token.setText(getPreferences(0).getString("token", "")); chat.setText(getPreferences(0).getString("chat", ""));
        Button save = new Button(this); save.setText("Save Telegram settings"); root.addView(save);
        save.setOnClickListener(v -> { getPreferences(0).edit().putString("token",token.getText().toString().trim()).putString("chat",chat.getText().toString().trim()).apply(); Toast.makeText(this,"Saved on device",Toast.LENGTH_SHORT).show(); });
        Button refresh = new Button(this); refresh.setText("Refresh history"); root.addView(refresh);
        ScrollView scroll = new ScrollView(this); history = new LinearLayout(this); history.setOrientation(LinearLayout.VERTICAL); scroll.addView(history); root.addView(scroll);
        refresh.setOnClickListener(v -> renderHistory()); renderHistory(); setContentView(root);
    }
    void renderHistory() {
        history.removeAllViews();
        try {
            JSONArray items = new JSONArray(getSharedPreferences("history",MODE_PRIVATE).getString("items","[]"));
            if(items.length()==0) { TextView empty=new TextView(this); empty.setText("No notifications recorded yet."); history.addView(empty); }
            for(int i=0;i<items.length();i++) {
                JSONObject item=items.getJSONObject(i);
                String message=item.optString("app")+"\n"+item.optString("title")+"\n"+item.optString("text");
                Button b=new Button(this); b.setAllCaps(false); b.setText(message+"\nTap to send to Telegram");
                history.addView(b);
                b.setOnClickListener(v -> sendTelegram(message));
            }
        } catch(Exception e) { Toast.makeText(this,"Unable to read history",Toast.LENGTH_SHORT).show(); }
    }
    void sendTelegram(String message) {
        String bot=token.getText().toString().trim(), id=chat.getText().toString().trim();
        if(TextUtils.isEmpty(bot)||TextUtils.isEmpty(id)) { Toast.makeText(this,"Enter Bot Token and Chat ID",Toast.LENGTH_SHORT).show(); return; }
        new Thread(() -> {
            boolean ok=false;
            try {
                URL url=new URL("https://api.telegram.org/bot"+bot+"/sendMessage");
                HttpURLConnection c=(HttpURLConnection)url.openConnection(); c.setRequestMethod("POST"); c.setConnectTimeout(12000); c.setReadTimeout(12000); c.setDoOutput(true); c.setRequestProperty("Content-Type","application/json; charset=utf-8");
                JSONObject payload=new JSONObject(); payload.put("chat_id",id); payload.put("text",message);
                try(OutputStream out=c.getOutputStream()) { out.write(payload.toString().getBytes(StandardCharsets.UTF_8)); }
                ok=c.getResponseCode()==200; c.disconnect();
            } catch(Exception ignored) { }
            boolean result=ok;
            runOnUiThread(() -> Toast.makeText(this,result?"Sent to Telegram":"Send failed: check bot and chat settings",Toast.LENGTH_LONG).show());
        }).start();
    }
}
