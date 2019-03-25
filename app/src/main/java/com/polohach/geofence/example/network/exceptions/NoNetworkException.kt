package com.polohach.geofence.example.network.exceptions

import com.polohach.geofence.example.R
import com.polohach.geofence.example.extensions.getStringApp

class NoNetworkException : Exception() {

    companion object {
        private val ERROR_MESSAGE = getStringApp(R.string.no_internet_connection)
    }

    override val message: String = ERROR_MESSAGE
}
