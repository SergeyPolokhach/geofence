package com.polohach.geofence.example.gps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.fragment.app.DialogFragment
import com.cleveroad.bootstrap.kotlin_permissionrequest.PermissionRequest
import com.cleveroad.bootstrap.kotlin_permissionrequest.PermissionResult
import com.polohach.geofence.example.extensions.checkNoGrantedPermission
import com.polohach.geofence.example.models.RequestCode
import com.polohach.geofence.example.preferences.PreferencesProvider


class GpsUiDelegateImpl(private val gpsUiCallback: GpsUiDelegate.GpsUiCallback) : GpsUiDelegate {

    private val gpsProviderTracker by lazy(LazyThreadSafetyMode.NONE) { GpsProviderTrackerImpl(this) }

    private val permissionRequest = PermissionRequest()
    private val permissionResult = object : PermissionResult {
        override fun onPermissionGranted() {
            if (gpsProviderTracker.isGpsOn()) {
                gpsProviderTracker.startTrack()
                dialog?.dismissAllowingStateLoss()
            } else {
                showConfirmDialog()
            }
        }

        override fun onPermissionDenied() {
            askForPermission = false
        }

        override fun onPermissionDeniedBySystem() {
            isDeniedBySystem = true
        }
    }

    private var resumed = false
    private var askForPermission = true
    private var isDeniedBySystem = false

    private var dialog: DialogFragment? = null

    override fun onResumeFragments() {
        resumed = true
        when {
            checkNoGrantedPermission(gpsUiCallback.getActivity(), ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION) &&
                    PreferencesProvider.trackLocation && !isDeniedBySystem -> {

                if (askForPermission) {
                    askForPermission = false
                    checkPermission()
                }
            }
            !gpsProviderTracker.isGpsOn() && PreferencesProvider.trackLocation -> {
                askForPermission = false
                checkPermission()
            }
            isDeniedBySystem -> {
                showPermissionDialog()
                isDeniedBySystem = false
            }
        }
    }

    override fun onPause() {
        resumed = false
    }

    override fun onDestroy() {
        gpsProviderTracker.stopTrack()
    }

    override fun checkPermission() {
        permissionRequest.request(gpsUiCallback.getActivity(),
                RequestCode.REQUEST_PERMISSION_GEOPOSITION_GPS.invoke(),
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                permissionResult)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPermissionDialog() {
        if (resumed) {
            gpsUiCallback.getActivity()
                    .supportFragmentManager
                    .findFragmentByTag(AllowPermissionDialog::class.java.name)
                    ?: run {
                        AllowPermissionDialog.newInstance()
                                .show(gpsUiCallback.getActivity().supportFragmentManager,
                                        AllowPermissionDialog::class.java.simpleName)
                    }
        }
    }

    override fun showConfirmDialog() {
        if (resumed) {
            dialog = gpsUiCallback.getActivity().supportFragmentManager.findFragmentByTag(GpsConfirmDialog::class.java.name) as? DialogFragment
                    ?: run {
                        GpsConfirmDialog.newInstance()
                    }.apply {
                        show(gpsUiCallback.getActivity().supportFragmentManager,
                                GpsConfirmDialog::class.java.name)
                    }
        }
    }
}