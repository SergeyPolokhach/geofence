package com.polohach.geofence.example.models

import android.os.Parcel
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.polohach.geofence.example.extensions.notNull


interface User : Model {
    var accuracy: Float?

    var circle: Circle?
    var marker: Marker?

    fun getPosition() = LatLng(latitude.notNull(), longitude.notNull())
}

data class UserModel(override var id: String? = null,
                     override var latitude: Double? = null,
                     override var longitude: Double? = null,
                     override var accuracy: Float? = null) : User {

    override var circle: Circle? = null
    override var marker: Marker? = null

    companion object {
        @JvmField
        val CREATOR = KParcelable.generateCreator {
            UserModel(it.read(), it.read(), it.read(), it.read())
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) =
            dest.write(id, latitude, longitude, accuracy)
}
