package com.polohach.geofence.example.models


interface Model : KParcelable {
    var id: String?
    var latitude: Double?
    var longitude: Double?
}
