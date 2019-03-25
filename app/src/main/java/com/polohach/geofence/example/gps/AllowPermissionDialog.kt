package com.polohach.geofence.example.gps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.polohach.geofence.example.BuildConfig
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.R


class AllowPermissionDialog : DialogFragment() {

    companion object {
        private const val PACKAGE_CONST = "package:"

        fun newInstance() = AllowPermissionDialog().apply {
            arguments = Bundle()
            isCancelable = false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog =
            AlertDialog.Builder(context ?: GFApp.instance)
                    .setTitle(R.string.permission_denied)
                    .setPositiveButton(getString(R.string.allow)) { _, _ ->
                        dismiss()
                        activity?.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("$PACKAGE_CONST${BuildConfig.APPLICATION_ID}")))
                    }
                    .create()
}
