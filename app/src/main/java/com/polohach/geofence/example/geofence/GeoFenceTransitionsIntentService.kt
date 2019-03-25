package com.polohach.geofence.example.geofence

import android.app.IntentService
import android.content.Intent
import com.cleveroad.bootstrap.kotlin_core.utils.ioToMainSingle
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.NAME_GEO_FENCE_SERVICE
import com.polohach.geofence.example.models.MessageModel
import com.polohach.geofence.example.providers.Providers
import com.polohach.geofence.example.utils.LOG
import org.joda.time.DateTime


class GeoFenceTransitionsIntentService : IntentService(NAME_GEO_FENCE_SERVICE) {

    companion object {
        private var TAG = GeoFenceTransitionsIntentService::class.java.simpleName
    }

    override fun onHandleIntent(intent: Intent?) {
        val geoFencingEvent = GeofencingEvent.fromIntent(intent)
        geoFencingEvent.takeIf { it.hasError() }
                ?.apply { LOG.e(TAG, getErrorString(errorCode)) }
                ?: apply {
                    geoFencingEvent.geofenceTransition
                            .takeIf {
                                it == Geofence.GEOFENCE_TRANSITION_ENTER
                                        || it == Geofence.GEOFENCE_TRANSITION_EXIT
                            }
                            ?.apply { sendMessage(geoFencingEvent) }
                }
    }

    private fun sendMessage(geoFencingEvent: GeofencingEvent) {
        GFApp.firebaseUser?.run {
            val currentTime = DateTime.now().millis
            val message = MessageModel(email,
                    geoFencingEvent.triggeringLocation.latitude,
                    geoFencingEvent.triggeringLocation.longitude,
                    currentTime,
                    geoFencingEvent.geofenceTransition)
            Providers.message.sendMessage(uid, currentTime.toString(), message)
                    .compose(ioToMainSingle())
                    .subscribe({ LOG.d(TAG, "Send message") }, { LOG.e(TAG, throwable = it) })
        }
    }

    private fun getErrorString(errorCode: Int) =
            when (errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GeoFence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many GeoFences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many pending intents"
                else -> "Unknown error."
            }
}
