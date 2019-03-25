package com.polohach.geofence.example.gps

import android.location.Location
import com.cleveroad.bootstrap.kotlin_core.utils.ioToMainSingle
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.models.UserModel
import com.polohach.geofence.example.providers.Providers
import com.polohach.geofence.example.ui.ACCURACY_RADIUS_IN_METERS
import com.polohach.geofence.example.utils.LOG


object LocationProcessorImpl : LocationProcessor {

    private var TAG = LocationProcessorImpl::class.java.simpleName

    override fun locationUpdated(location: Location) {
        GFApp.firebaseUser?.run {
            val user = UserModel(uid,
                    location.latitude,
                    location.longitude,
                    location.accuracy.takeIf { it <= ACCURACY_RADIUS_IN_METERS }
                            ?: ACCURACY_RADIUS_IN_METERS)
            Providers.user.editUser(uid, user)
                    .compose(ioToMainSingle())
                    .subscribe(
                            { LOG.d(TAG, "Track location success") },
                            { LOG.e(TAG, throwable = it) })
        }
    }
}
