package com.polohach.geofence.example.gps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils.getExtra
import com.cleveroad.bootstrap.kotlin_ext.safeLet
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.polohach.geofence.example.*
import com.polohach.geofence.example.extensions.checkIsGrantedPermission
import com.polohach.geofence.example.extensions.checkNoGrantedPermission
import com.polohach.geofence.example.extensions.getStringApp
import com.polohach.geofence.example.models.RequestCode
import com.polohach.geofence.example.ui.screens.splash.SplashActivity
import com.polohach.geofence.example.utils.LOG


class GpsTrackerService : LifecycleService(),
        LifecycleOwner,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    companion object {
        private const val ON = 1
        private const val OFF = 2

        // 5 minutes
        private const val LOCATION_INTERVAL_MILLISECONDS = 1000L * 60 * 5
        // 1 minute
        private const val FASTER_INTERVAL_MILLISECONDS = 1_000L * 60

        private val TAG = GpsTrackerService::class.java.simpleName

        private val EXTRA_IS_ON = getExtra("is_on", GpsTrackerService::class.java)

        /**
         * Start gps tracking
         *
         * @param context - [Context]
         */
        fun startTracking(context: Context) {
            val intent = Intent(context, GpsTrackerService::class.java)
                    .apply { putExtra(EXTRA_IS_ON, ON) }
            with(context) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
        }

        /**
         * Stop gps service
         *
         * @param context - [Context]
         */
        fun stopService(context: Context) {
            context.stopService(Intent(context, GpsTrackerService::class.java))
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult?) {
            safeLet(location, locationProcessor) { currentLocation, locationProcessor ->
                locationProcessor.locationUpdated(currentLocation.lastLocation)
            }
        }
    }

    private var locationProcessor: LocationProcessor? = null

    private val apiClient by lazy {
        GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onCreate() {
        startForeground()
        super.onCreate()
    }

    private fun startForeground() {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel()
                } else {
                    EMPTY_STRING
                }

        val contentIntent = PendingIntent.getActivity(
                this,
                RequestCode.REQUEST_GPS_SERVICE.invoke(),
                Intent(this, SplashActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

        NotificationCompat.Builder(this, channelId)
                .apply {
                    startForeground(SERVICE_ID, setOngoing(true)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setPriority(PRIORITY_MIN)
                            .setContentText(getStringApp(R.string.track_location_notification))
                            .setCategory(Notification.CATEGORY_SERVICE)
                            .setContentIntent(contentIntent)
                            .build())
                }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() =
            NotificationChannel(CHANNEL_ID_GPS_SERVICE, CHANNEL_ID_GPS_SERVICE,
                    NotificationManager.IMPORTANCE_HIGH).run {
                importance = NotificationManager.IMPORTANCE_NONE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                        .createNotificationChannel(this)
                CHANNEL_ID_GPS_SERVICE
            }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
            if (intent?.hasExtra(EXTRA_IS_ON) == true) {
                when (intent.getIntExtra(EXTRA_IS_ON, OFF)) {
                    ON -> Service.START_STICKY.also { connectGoogleApiClient() }
                    OFF -> super.onStartCommand(intent, flags, startId).also { disconnect() }
                    else -> super.onStartCommand(intent, flags, startId)
                }
            } else {
                super.onStartCommand(intent, flags, startId)
            }

    private fun connectGoogleApiClient() {
        apiClient.takeIf {
            checkIsGrantedPermission(GFApp.instance, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
        }?.apply { if (!isConnected && !isConnecting) connect() else reconnect() }
    }

    private fun disconnect() {
        locationProcessor = null
        apiClient.disconnect()
        stopForeground(true)
    }

    override fun onConnected(bundle: Bundle?) {
        if (checkNoGrantedPermission(GFApp.instance, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)) {
            return
        }
        locationProcessor = LocationProcessorImpl
        val locationRequest = createRequest()
        LocationServices
                .getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    override fun onConnectionSuspended(i: Int) {
        apiClient.reconnect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        LOG.e(TAG, "Failed")
    }

    private fun createRequest() = LocationRequest().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = FASTER_INTERVAL_MILLISECONDS
    }

    override fun onDestroy() {
        disconnect()
        super.onDestroy()
    }
}
