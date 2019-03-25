package com.polohach.geofence.example.network.exceptions


/**
 * Error from server.
 */
@Suppress("LeakingThis")
open class ApiException() : Exception() {

    open var statusCode: Int? = null
    var mMessage: String? = null
    var v: String? = null
    var errors: List<ValidationError>? = null
    var stacktrace: String? = null

    constructor(statusCode: Int?,
                v: String?,
                message: String?,
                errors: List<ValidationError>?,
                stacktrace: String? = null) : this() {
        this.statusCode = statusCode
        this.mMessage = message
        this.v = v
        this.errors = errors
        this.stacktrace = stacktrace
    }

    override val message = mMessage

    override fun toString(): String {
        return "ApiException(statusCode=$statusCode, mMessage=$mMessage, v=$v, errors=$errors, stacktrace=$stacktrace, message=$message)"
    }
}
