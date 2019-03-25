package com.polohach.geofence.example.models

enum class RequestCode(private val requestCode: Int) {
    REQUEST_GEOFENCE_SERVICE(0),
    REQUEST_GPS_SERVICE(5),
    REQUEST_PERMISSION_GEOPOSITION_MAP(6),
    REQUEST_PERMISSION_GEOPOSITION_GPS(7);

    operator fun invoke() = requestCode
}