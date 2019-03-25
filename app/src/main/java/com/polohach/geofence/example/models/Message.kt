package com.polohach.geofence.example.models

import android.os.Parcel
import org.joda.time.DateTime


interface Message : Model {
    var email: String?
    var time: Long?
    var transition: Int?

    fun getDateTime() = DateTime(time)
}

data class MessageModel(override var email: String? = null,
                        override var latitude: Double? = null,
                        override var longitude: Double? = null,
                        override var time: Long? = null,
                        override var transition: Int? = null,
                        override var id: String? = null) : Message {

    companion object {
        @JvmField
        val CREATOR = KParcelable.generateCreator {
            MessageModel(it.read(), it.read(), it.read(), it.read(), it.read(), it.read())
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) =
            dest.write(email, latitude, longitude, time, transition, id)
}
