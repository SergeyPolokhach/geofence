package com.polohach.geofence.example.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import java.lang.ref.WeakReference

class GpsProviderReceiver(private val locationManager: LocationManager,
                          callback: GpsCallback) : BroadcastReceiver() {

    private var gpsCallbackWeakReference = WeakReference<GpsCallback>(callback)

    override fun onReceive(context: Context, intent: Intent) {
        gpsCallbackWeakReference.get()?.apply {
            gpsStateChanged(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        }
    }

    @FunctionalInterface
    interface GpsCallback {
        fun gpsStateChanged(enabled: Boolean)
    }
}
