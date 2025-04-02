package com.bryanguerra.bluetoothvolumelock

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.core.content.ContextCompat

class BluetoothReceiver : BroadcastReceiver() {

    private val targetMacAddress = "E8:EE:CC:AA:D5:8A"  // Auriculares Bluetooth
    override fun onReceive(context: Context?, intent: Intent?) {
        val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        when (intent?.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED,
            BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                if (device?.address == targetMacAddress && context != null) {
                    Log.d("BluetoothReceiver", "✅ Dispositivo conectado: ${device.address}")
                    if (!isServiceRunning(context, BluetoothVolumeService::class.java)) {
                        val serviceIntent = Intent(context, BluetoothVolumeService::class.java)
                        ContextCompat.startForegroundService(context, serviceIntent)
                    }
                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                if (device?.address == targetMacAddress && context != null) {
                    Log.d("BluetoothReceiver", "❌ Dispositivo desconectado: ${device.address}")
                    if (isServiceRunning(context, BluetoothVolumeService::class.java)) {
                        val serviceIntent = Intent(context, BluetoothVolumeService::class.java)
                        context.stopService(serviceIntent)
                    }
                }
            }
        }
    }

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}