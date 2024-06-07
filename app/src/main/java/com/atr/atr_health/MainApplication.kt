package com.atr.atr_health

import android.app.Application
import androidx.health.services.client.data.DataType
import com.atr.atr_health.data.DataService

const val TAG = "ATR Health"

val PERMISSIONS = listOf(
    android.Manifest.permission.BODY_SENSORS,
    android.Manifest.permission.INTERNET,
    android.Manifest.permission.ACCESS_NETWORK_STATE,
    android.Manifest.permission.ACCESS_WIFI_STATE
)

val DATA_TYPES = listOf(
    DataType.HEART_RATE_BPM
)

class MainApplication : Application() {
    val dataService by lazy { DataService(this, DATA_TYPES) }
}