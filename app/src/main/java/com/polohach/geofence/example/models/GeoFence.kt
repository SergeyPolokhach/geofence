package com.polohach.geofence.example.models

import android.os.Parcel
import com.google.android.gms.maps.model.LatLng
import com.polohach.geofence.example.extensions.notNull


interface GeoFence : Model {
    var radius: Float?

    fun getPosition() = LatLng(latitude.notNull(), longitude.notNull())
}

data class GeoFenceModel(override var id: String? = null,
                         override var latitude: Double? = null,
                         override var longitude: Double? = null,
                         override var radius: Float? = null) : GeoFence {

    companion object {
        @JvmField
        val CREATOR = KParcelable.generateCreator {
            GeoFenceModel(it.read(), it.read(), it.read(), it.read())
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) =
            dest.write(id, latitude, longitude, radius)
}
