# 🎧 Bluetooth Volume Lock

📘 [Read this in English](README.en.md)


**Bluetooth Volume Lock** es un servicio Android que **mantiene constante el volumen multimedia** cuando conectas unos auriculares Bluetooth. Debido a Android 15+ (API 34) donde el sistema reduce automáticamente el volumen a un “nivel seguro” cuando usas los auriculares con un volumen elevado durante equis tiempo.

> Esta app soluciona ese problema restaurando el volumen original de forma automática, ideal para el gimnasio 🏋️‍♂️ o uso intensivo con audífonos.

---

## 🔧 Características

- ✅ Monitorea conexiones Bluetooth
- 🔊 Restaura el volumen multimedia original si Android lo reduce por seguridad
- 📲 Se ejecuta como **servicio en segundo plano**
- 🧪 Actualmente este proyecto sólo es compatible con **Android 15 (API 34)**

---

## 📸 Captura de pantalla

*(TODO)*

---

## 📦 Instalación

1. Clona el proyecto:

```bash
git clone https://github.com/bguerraDev/BluetoothVolumeLock.git
```

2. Ábrelo en Android Studio
3. Modifica los valores de **targetMacAddress** y **device.name** por los tuyos
4. Conecta tu dispositivo
5. Ejecuta el proyecto
6. Activa el permiso de DISPOSITIVOS CERCANOS manualmente a través del dispositivo móvil

---

## 🧪 Iniciar el servicio manualmente por ADB

Usa este comando:

```bash
adb shell am start-foreground-service -n com.bryanguerra.bluetoothvolumelock/.BluetoothVolumeService
```

---

## 💡 Recomendaciones

- Asegúrate de tener permisos para modificar el volumen (API 34+ necesita permisos especiales)
- Requiere permiso de acceso a dispositivos cercanos (`BLUETOOTH_CONNECT` en Android 15)

---

## 📃 Licencia

Este proyecto está licenciado bajo la **Licencia MIT** – consulta el archivo [LICENSE](./LICENSE) para más detalles.

---

### 🤖 Hecho con Kotlin + Android Studio

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?logo=kotlin&logoColor=fff&style=flat)
![Android](https://img.shields.io/badge/Android_Studio-3DDC84?logo=android-studio&logoColor=white&style=flat)
