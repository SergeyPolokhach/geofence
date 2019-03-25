package com.polohach.geofence.example.gps

import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import com.polohach.geofence.example.GFApp


class GpsProviderTrackerImpl(private val gpsUiDelegate: GpsUiDelegate) : GpsProviderTracker,
        GpsProviderReceiver.GpsCallback {

    private val locationManager by lazy { GFApp.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    private var gpsProviderReceiver: GpsProviderReceiver? = null

    override fun startTrack() {
        with(GFApp.instance) app@{
            if (gpsProviderReceiver == null) {
                val gpsFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
                gpsProviderReceiver = GpsProviderReceiver(locationManager, this@GpsProviderTrackerImpl)
                registerReceiver(gpsProviderReceiver, gpsFilter)
            }
            switchGpsTracker(isGpsOn())
        }
    }

    override fun stopTrack() {
        gpsProviderReceiver?.let {
            with(GFApp.instance) {
                unregisterReceiver(it)
                gpsProviderReceiver = null
                switchGpsTracker(false)
            }
        }
    }

    override fun isGpsOn() = locationManager.run {
        isProviderEnabled(LocationManager.GPS_PROVIDER) || isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun gpsStateChanged(enabled: Boolean) {
        with(gpsUiDelegate) delegate@{
            if (!enabled) {
                showConfirmDialog()
            } else if (enabled) {
                checkPermission()
            }
        }
    }
}
