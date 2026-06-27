# 📺 RTSP TV Viewer

A simple, free Android **TV** app that plays an **RTSP** stream (like an IP camera or NVR) full screen.

You just type in your `rtsp://...` link once, and the app remembers it and plays it automatically every time you open it. It is built for Android TV remotes — no touchscreen needed.

---

## ✨ What it does

- ▶️ Plays any RTSP stream full screen
- 💾 Remembers your stream URL (auto-plays on the next launch)
- ⚡ **Low-latency / near real-time** playback (small buffer + TCP transport)
- 🔁 **Auto-reconnects** if the stream drops (up to 5 tries)
- 🎮 **Remote-friendly menu** — change URL, reconnect, or exit using just your TV remote
- 🟢 On-screen status messages (Connecting / Buffering / Errors) so you are never stuck on a black screen

---

## 📥 Install on your TV (easiest way)

You don't need to build anything. Just grab the ready-made app:

1. Go to the **[Releases](../../releases)** page of this repo.
2. Download the latest **`app-debug.apk`**.
3. Copy it to your Android TV (USB drive, a file-sharing app, or `adb`).
4. On your TV, allow **"Install from unknown sources"** if it asks.
5. Open the APK to install, then launch **RTSP TV Viewer**.

> Requires **Android 8.0 (Oreo) or newer**.

### Install with adb (optional, for advanced users)
If your TV and PC are on the same network:
```bash
adb connect <TV-IP-ADDRESS>:5555
adb install -r app-debug.apk
```

---

## 🎮 How to use it (TV remote)

1. **First launch:** a box appears — type your stream link, e.g.
   `rtsp://username:password@192.168.1.10:554/stream`
   Tick **"Force TCP"** if the video stutters, then press **Save**.
2. The stream starts playing full screen.
3. Press **OK** (center) or **BACK** on the remote to open the **menu**:

| Remote button     | What it does                              |
|-------------------|-------------------------------------------|
| **OK / Center**   | Open the options menu                     |
| **BACK**          | Open / close the options menu             |
| **D-pad ↑ / ↓**   | Move between menu buttons                  |

The menu has three options:
- **Change URL** – enter a different stream
- **Reconnect** – restart the current stream
- **Exit** – close the app

---

## 🩹 Troubleshooting

| Problem | Fix |
|---------|-----|
| Black screen / nothing plays | Double-check the `rtsp://` link is correct and the camera is on the same network. |
| Video is laggy or breaks up | Open the menu → **Change URL** → tick **Force TCP** → Save. |
| "Playback failed" message | Open the menu and tap **Reconnect**, or fix the URL. |
| App not visible on TV | Make sure it installed correctly; it appears in the TV apps row as "RTSP TV Viewer". |

---

## 🛠 For developers (build it yourself)

### Tech stack
- **Kotlin**
- **AndroidX Media3 1.4.1** (ExoPlayer + RTSP) for playback
- **Android Gradle Plugin 8.7.2**, **Gradle 8.11.1**, **Kotlin 2.0.21**
- `compileSdk 35` · `targetSdk 34` · `minSdk 26 (Android 8.0)`

### Build steps
1. Clone the repo:
   ```bash
   git clone https://github.com/F-e-n-y-x/RTSP-TV-Viewer.git
   ```
2. Open the folder in **Android Studio** and let Gradle sync (it downloads everything automatically).
3. Build the APK from the menu:
   `Build > Build App Bundle(s) / APK(s) > Build APK(s)`
4. The APK will be here:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

Or build from the command line:
```bash
./gradlew assembleDebug
```

---

## 📁 Project structure

```
app/src/main/java/com/example/rtspviewer/
├── MainActivity.kt            # Launcher entry point (routes to the player)
├── PlayerActivity.kt          # Full-screen player + on-screen menu + reconnect
└── RtspUrlDialogFragment.kt   # Dialog to enter / edit the RTSP URL
app/src/main/res/              # Layouts, icons, strings, theme
app/src/main/AndroidManifest.xml
```

---

## ⚖️ License

Provided **as-is** for personal and educational use. Feel free to modify or extend it for your own setup.
