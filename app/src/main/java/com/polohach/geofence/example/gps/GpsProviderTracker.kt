package com.polohach.geofence.example.gps


interface GpsProviderTracker {
    /**
     * Start track
     */
    fun startTrack()

    /**
     * Check gps state
     * @return true if gps is turn on
     */
    fun isGpsOn(): Boolean

    /**
     * Stop track
     */
    fun stopTrack()
}
