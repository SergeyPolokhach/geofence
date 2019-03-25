package com.polohach.geofence.example.utils.authorization_helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.IntDef

/**
 * Interface for authorization with social Networks
 */
interface AuthorizationHelper {

    /**
     * Starts authorization flow
     */
    fun auth()

    /**
     * Starts authorization flow with email and password
     */
    fun auth(email: String, password: String)

    /**
     * Delivers result from [Activity.onActivityResult]
     * or [Fragment.onActivityResult]
     * to Authorization SDK realization
     *
     * @param requestCode The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this
     * result came from.
     * @param resultCode  The integer result code returned by the child activity
     * through its setResult().
     * @param data        An Intent, which can return result data to the caller
     * (various data can be attached to Intent "extras").
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    /**
     * Must be called in [Activity.onResume] or [Fragment.onResume]
     */
    fun connect()

    /**
     * Must be called in [Activity.onPause] or [Fragment.onPause]
     */
    fun disconnect()

    /**
     * Callback interface from helper
     */
    interface AuthorizationCallback {

        /**
         * Returns instance of Context for using in implementation
         *
         * @return Instance of [Context]
         */
        val contextForHelper: Context?

        /**
         * Method called after successful authorization
         *
         * @param authType Type of social Network. Must be one
         * of [AuthorizationHelper.AuthType]
         * @param token    Authorization token from network
         */
        fun onSuccess(@AuthType authType: Int, token: String)

        /**
         * Called after failed authorization
         *
         * @param authType  - The Authorization token from network
         * @param throwable - The throwable from social authorization SDK
         */
        fun onFail(@AuthType authType: Int, throwable: Throwable?)
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(GOOGLE_PLUS_AUTH)
    annotation class AuthType

    companion object {
        const val TAG = "Authorization"
        const val GOOGLE_PLUS_AUTH = 1
        const val EMAIL_AUTH = 1
    }
}
