package com.polohach.geofence.example.network.exceptions

import com.polohach.geofence.example.R
import com.polohach.geofence.example.extensions.getStringApp


class ServerException : ApiException() {

    companion object {
        private val ERROR_MESSAGE = getStringApp(R.string.server_error)
        private const val STATUS_CODE = 500
    }

    override val message: String = ERROR_MESSAGE
    override var statusCode: Int? = STATUS_CODE
}
