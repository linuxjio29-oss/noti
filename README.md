# Notification Bridge

Android 13+ Java project. Captures notifications into a **local, on-device** history (up to 500 recent entries), and lets the user **manually** send individual entries to their Telegram bot. No automatic Telegram exfiltration.

## Build with GitHub Actions
1. Create a GitHub repository and upload all project files, keeping directory structure.
2. Open **Actions** > **Build APK** > **Run workflow**.
3. Download `notification-bridge-debug-apk` from the completed run's Artifacts section.

## Use
1. Install the debug APK, open it, tap **Grant notification access**, and enable Notification Bridge.
2. Create a bot using Telegram's BotFather. Start a chat with your bot and obtain your chat ID.
3. Enter Bot Token and Chat ID, save, and tap **Refresh history**.
4. Tap a notification to send it manually. Avoid forwarding authentication codes or confidential content.

## Compatibility
Minimum SDK 33 (Android 13), compile/target SDK 35. Android 16 and 17 require device testing and may impose newer behavior restrictions. Some notification contents may be redacted by Android.

## Privacy
The app keeps up to 500 recent notifications in app-private SharedPreferences, including sensitive content. This local store is not encrypted. Telegram credentials are also stored in app-private preferences, not encrypted. Do not use on shared or compromised devices. Uninstalling the app removes its local data. No filtering is performed on local history; sending to Telegram requires an explicit tap for each entry.
