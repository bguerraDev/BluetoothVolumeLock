# 🎧 Bluetooth Volume Lock

**Bluetooth Volume Lock** is an Android service that **keeps your media volume steady** when you connect Bluetooth headphones. On Android 15+ (API 34), the system automatically reduces the volume to a "safe level" when using headphones at high volume for a prolonged time.

> This app solves that issue by restoring your original volume automatically — ideal for gym sessions 🏋️‍♂️ or extended headphone use.

---

## 🔧 Features

- ✅ Monitors Bluetooth connections
- 🔊 Restores original media volume if Android lowers it for safety
- 📲 Runs as a **foreground service**
- 🧪 Currently compatible with **Android 15 (API 34)** only

---

## 📸 Screenshot

*(TODO)*

---

## 📦 Installation

1. Clone the project:

```bash
git clone https://github.com/bguerraDev/BluetoothVolumeLock.git
```

2. Open it in Android Studio  
3. Replace `targetMacAddress` and `device.name` with your own  
4. Connect your device  
5. Run the project  
6. Manually grant the **Nearby Devices** permission on your phone

---

## 🧪 Start the service manually using ADB

Use this command:

```bash
adb shell am start-foreground-service -n com.bryanguerra.bluetoothvolumelock/.BluetoothVolumeService
```

---

## 💡 Recommendations

- Make sure you have permission to modify system volume (API 34+ needs additional permission)
- Requires access to nearby devices (`BLUETOOTH_CONNECT` on Android 15)

---

## 📃 License

This project is licensed under the **MIT License** – see the [LICENSE](./LICENSE) file for details.

---

### 🤖 Built with Kotlin + Android Studio

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?logo=kotlin&logoColor=fff&style=flat)
![Android](https://img.shields.io/badge/Android_Studio-3DDC84?logo=android-studio&logoColor=white&style=flat)
