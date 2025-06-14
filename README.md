# RTSP TV Viewer  

A lightweight, native Android TV app that allows manual RTSP stream entry and playback using ExoPlayer. Designed for Android 9+ TVs, this app is remote-friendly, splash-free, and easy to use even for non-technical users.  

---

## 🎯 Features  

- Simple interface: manual RTSP URL entry with persistent storage  
- Auto-play saved stream on app launch  
- No splash screen — instant startup  
- Remote-friendly dialog for RTSP input  
- Uses ExoPlayer (RTSP extension) for reliable playback  

---

## 🛠 Tech stack  

- Kotlin  
- AndroidX + Fragment + Preference libraries  
- ExoPlayer 2.19+  
- Min SDK: 26 (Android 8.0 Oreo)  
- Target SDK: 33  

---

## 🚀 How to build  

1️⃣ Clone this repo  
```bash
git clone https://github.com/F-e-n-y-x/RTSP-TV-Viewer.git
```  

2️⃣ Open the project in Android Studio  

3️⃣ Let Gradle sync (Android Studio will handle dependencies automatically)  

4️⃣ Build APK  
> `Build > Build Bundle(s)/APK(s) > Build APK(s)`  

5️⃣ The APK will be in:  
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📺 How to install on your TV  

- Transfer the APK via USB, network, or adb  
- Enable installation from unknown sources on your TV  
- Install and run — the app will auto-play your saved RTSP URL  

---

## ⚡ License  

This project is provided as-is for personal and educational use. Feel free to modify or extend for your own setup.  
