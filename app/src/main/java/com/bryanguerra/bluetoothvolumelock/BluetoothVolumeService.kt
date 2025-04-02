package com.bryanguerra.bluetoothvolumelock

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.Manifest
import android.bluetooth.BluetoothProfile
import android.database.ContentObserver
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.widget.TextView


class BluetoothVolumeService : Service() {

    private var audioManager: AudioManager? = null
    private var targetDeviceConnected = false
    private var previousVolume = -1
    private val targetMacAddress = "E8:EE:CC:AA:D5:8A"

    private var vibrator: Vibrator? = null
    private var volumeObserver: ContentObserver? = null


    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        startForegroundService()
        showToast("Servicio creado")
        Log.d("BluetoothVolumeService", "Servicio creado")

        registerVolumeObserver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BluetoothVolumeService", "Servicio iniciado")

        audioManager?.requestAudioFocus(
            audioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        targetDeviceConnected = isTargetDeviceConnected()

        // Agregar un pequeño retraso antes de verificar la conexión
        Handler(Looper.getMainLooper()).postDelayed({
            targetDeviceConnected = isTargetDeviceConnected()
            if (targetDeviceConnected) {
                showToast("✅ Dispositivo detectado al iniciar el servicio")
                Log.d(
                    "BluetoothVolumeService",
                    "✅ Dispositivo detectado al iniciar: $targetMacAddress"
                )
            } else {
                Log.d("BluetoothVolumeService", "❌ Dispositivo NO está conectado activamente")
            }
        }, 4000) // Retraso de 3 segundos

        monitorVolume()
        return START_STICKY
    }


    /**
     * 📌 Observa cambios en el volumen del sistema.
     */
    private fun registerVolumeObserver() {
        volumeObserver = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)

                if (targetDeviceConnected) {
                    val currentVolume =
                        audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0

                    // Si el volumen bajó significativamente, restaurarlo
                    if (previousVolume != -1 && currentVolume < previousVolume - 3) { // Detecta cambios drásticos
                        restoreVolume()
                    }

                    previousVolume = currentVolume
                }
            }
        }

        contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            volumeObserver!!
        )
    }

    /**
     * 📌 Restaura el volumen y vibra para confirmar.
     */
    private fun restoreVolume() {
        previousVolume.let {
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, it, 0)
            showToast("Volumen restaurado")
            vibrateOnVolumeRestore()
            Log.d("BluetoothVolumeService", "Volumen restaurado a $it")
        }
    }

    /**
     * 📌 Vibra cuando el volumen se restaura.
     */
    private fun vibrateOnVolumeRestore() {
        if (vibrator?.hasVibrator() == true) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(450, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    }

    /**
     * 📌 Monitoriza el volumen del dispositivo.
     */
    private fun monitorVolume() {
        if (!targetDeviceConnected) return

        val currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
        if (previousVolume != -1 && currentVolume < previousVolume) {
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0)
            //showToast("Volumen restaurado al nivel anterior")
            Log.d("BluetoothVolumeService", "Volumen restaurado a $previousVolume")
        }
        previousVolume = currentVolume
    }

    /**
     * 📌 Inicia el servicio en primer plano. // TODO NO LO HACE ACTUALMENTE
     */
    private fun startForegroundService() {
        val channelId = "BluetoothVolumeServiceChannel"
        val channel = NotificationChannel(
            channelId,
            "Bluetooth Volume Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Bluetooth Volume Service")
            .setContentText("Manteniendo el volumen estable")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    private fun showToast(message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showCustomToast(message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val toast = Toast(applicationContext)
            toast.duration = Toast.LENGTH_LONG

            val textView = TextView(applicationContext).apply {
                text = message
                textSize = 18f
                setPadding(30, 20, 30, 20)
                setBackgroundColor(Color.BLACK)
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
            }

            toast.view = textView
            toast.show()
        }
    }


    override fun onDestroy() {
        audioManager?.abandonAudioFocus(audioFocusChangeListener)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * 📌 Comprueba si el dispositivo objetivo está conectado.
     */
    private fun isTargetDeviceConnected(): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.d("BluetoothVolumeService", "❌ Bluetooth no disponible o desactivado")
            return false
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BluetoothVolumeService", "❌ Permiso BLUETOOTH_CONNECT no concedido")
            return false
        }

        // Comprobar dispositivos emparejados
        val pairedDevices = bluetoothAdapter.bondedDevices
        for (device in pairedDevices) {
            if (device.address == targetMacAddress || device.name == "Bryan's Soundcore Liberty 4NC") {
                Log.d(
                    "BluetoothVolumeService",
                    "🔍 Dispositivo emparejado detectado: ${device.name} - ${device.address}"
                )

                // Ahora comprobamos si está realmente conectado usando getProfileProxy()
                bluetoothAdapter.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
                    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                        val connectedDevices = proxy.connectedDevices
                        for (connectedDevice in connectedDevices) {
                            if (connectedDevice.address == targetMacAddress) {
                                Log.d(
                                    "BluetoothVolumeService",
                                    "✅ Dispositivo detectado activamente: ${connectedDevice.address}"
                                )
                                bluetoothAdapter.closeProfileProxy(
                                    profile,
                                    proxy
                                )  // Cerrar correctamente
                                return
                            }
                        }
                        bluetoothAdapter.closeProfileProxy(
                            profile,
                            proxy
                        )  // Cerrar aunque no haya coincidencias
                    }

                    override fun onServiceDisconnected(profile: Int) {
                        Log.d(
                            "BluetoothVolumeService",
                            "❌ Servicio de perfil Bluetooth desconectado"
                        )
                    }
                }, BluetoothProfile.HEADSET)
                return true
            }
        }

        Log.d("BluetoothVolumeService", "❌ Dispositivo NO está conectado activamente")
        return false
    }


    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        if (targetDeviceConnected) {
            val currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
            if (previousVolume != -1 && currentVolume < previousVolume) {
                audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0)
                showCustomToast("Volumen restaurado al nivel anterior")
                Log.d("BluetoothVolumeService", "Volumen restaurado a $previousVolume")
            }
            previousVolume = currentVolume
        }
    }


}