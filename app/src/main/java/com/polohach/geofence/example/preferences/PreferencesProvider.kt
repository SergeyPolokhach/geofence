package com.polohach.geofence.example.preferences

import com.polohach.geofence.example.GFApp

internal object PreferencesProvider {

    private val preferences = GFApp.securePrefs

    var isFirstOpen: Boolean
        get() = preferences.getBoolean(PreferencesContract.FIRST_OPEN, true)
        set(value) {
            preferences.edit()
                    .putBoolean(PreferencesContract.FIRST_OPEN, value)
                    .commit()
        }

    var trackLocation: Boolean
        get() = preferences.getBoolean(PreferencesContract.IS_TRACK_LOCATION, false)
        set(value) {
            preferences.edit()
                    .putBoolean(PreferencesContract.IS_TRACK_LOCATION, value)
                    .commit()
        }
}
