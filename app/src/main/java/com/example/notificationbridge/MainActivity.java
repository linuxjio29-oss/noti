package com.example.notificationbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView status = findViewById(R.id.status);
        Button btn = findViewById(R.id.btn_grant);

        btn.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));

        // Show current listener status
        String listeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners");
        boolean enabled = listeners != null && listeners.contains(getPackageName());
        status.setText(enabled ? "✅ Access GRANTED — notifications are being sent to Telegram"
                               : "❌ Access NOT granted — tap the button below");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String listeners = Settings.Secure.getString(
                getContentResolver(), "enabled_notification_listeners");
        boolean enabled = listeners != null && listeners.contains(getPackageName());
        TextView status = findViewById(R.id.status);
        status.setText(enabled ? "✅ Access GRANTED — notifications are being sent to Telegram"
                               : "❌ Access NOT granted — tap the button below");
    }
}