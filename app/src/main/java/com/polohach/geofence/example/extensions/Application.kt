package com.polohach.geofence.example.extensions

import androidx.annotation.StringRes
import com.polohach.geofence.example.GFApp

fun getStringApp(@StringRes stringRes: Int, vararg args: Any?): String =
        GFApp.instance.getString(stringRes, *args)
