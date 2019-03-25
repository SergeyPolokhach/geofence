package com.polohach.geofence.example.gps

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.R


class GpsConfirmDialog : DialogFragment() {

    companion object {
        fun newInstance() = GpsConfirmDialog().apply {
            arguments = Bundle()
            isCancelable = false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog =
            AlertDialog.Builder(context ?: GFApp.instance)
                    .setMessage(R.string.enable_gps)
                    .setTitle(R.string.gps)
                    .setPositiveButton(getString(R.string.allow)) { _, _ ->
                        dismiss()
                        activity?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .create()
}
