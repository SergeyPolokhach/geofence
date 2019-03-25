package com.polohach.geofence.example.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.polohach.geofence.example.extensions.notNull
import com.polohach.geofence.example.models.GeoFence
import com.polohach.geofence.example.models.RequestCode
import com.polohach.geofence.example.ui.ACCURACY_RADIUS_IN_METERS


class GeoFenceUtil {

    @SuppressLint("MissingPermission")
    fun setGeoFences(context: Context, geoFences: List<GeoFence>) {
        takeIf { geoFences.isNotEmpty() }?.apply {
            with(getGeoFencesClient(context)) {
                removeGeoFences(this, context)
                        .addOnSuccessListener {
                            addGeofences(getGeoFencingRequest(populateGeoFenceList(geoFences)),
                                    getGeoFencePendingIntent(context))
                        }
            }
        }
    }

    private fun removeGeoFences(client: GeofencingClient, context: Context) =
            client.removeGeofences(getGeoFencePendingIntent(context))

    private fun getGeoFencesClient(context: Context) = LocationServices.getGeofencingClient(context)

    private fun getGeoFencePendingIntent(context: Context) =
            PendingIntent.getService(context,
                    RequestCode.REQUEST_GEOFENCE_SERVICE.invoke(),
                    Intent(context, GeoFenceTransitionsIntentService::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT)

    private fun populateGeoFenceList(geoFences: List<GeoFence>) = mutableListOf<Geofence>().apply {
        geoFences.forEach {
            it.run {
                add(Geofence.Builder()
                        .setRequestId(id)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setCircularRegion(latitude.notNull(), longitude.notNull(), radius
                                ?: ACCURACY_RADIUS_IN_METERS)
                        .build())
            }
        }
    }

    private fun getGeoFencingRequest(geoFences: MutableList<Geofence>) =
            GeofencingRequest.Builder()
                    .addGeofences(geoFences)
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .build()
}
