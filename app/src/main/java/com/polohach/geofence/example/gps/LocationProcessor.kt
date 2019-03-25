package com.polohach.geofence.example.gps

import android.location.Location


interface LocationProcessor {

    /**
     * Call when location updated
     * @param location - Instance of [Location]
     */
    fun locationUpdated(location: Location)
}
