package com.polohach.geofence.example

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.polohach.geofence.example.BuildConfig.SECURE_PREF_NAME
import com.polohach.geofence.example.BuildConfig.SECURE_PREF_PASSWORD
import com.polohach.geofence.example.gps.GpsTrackerService
import com.polohach.geofence.example.models.UserModel
import com.polohach.geofence.example.preferences.PreferencesProvider
import com.polohach.geofence.example.ui.Location
import com.securepreferences.SecurePreferences


class GFApp : Application() {

    companion object {
        lateinit var instance: GFApp
            private set

        lateinit var securePrefs: SecurePreferences
            private set

        val firebaseUser
            get() = FirebaseAuth.getInstance().currentUser


    }

    lateinit var myUser: UserModel

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        securePrefs = getSharedPreferences()
        FirebaseApp.initializeApp(this)
        myUser = UserModel(firebaseUser?.uid,
                Location.DEFAULT_LOCATION.latitude,
                Location.DEFAULT_LOCATION.longitude)
    }

    fun switchGpsTracker(isTrackLocation: Boolean) {
        PreferencesProvider.trackLocation = isTrackLocation
        if (isTrackLocation) startGpsTracker() else stopGpsTrackerService()
    }

    private fun startGpsTracker() {
        GpsTrackerService.startTracking(this)
    }

    private fun stopGpsTrackerService() {
        GpsTrackerService.stopService(this)
    }

    private fun getSharedPreferences() =
            SecurePreferences(this, SECURE_PREF_PASSWORD, SECURE_PREF_NAME)
}
