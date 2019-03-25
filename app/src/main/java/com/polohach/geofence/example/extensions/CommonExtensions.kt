@file:Suppress("UNCHECKED_CAST")

package com.polohach.geofence.example.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkIsGrantedPermission(context: Context, permission: String) =
        ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

fun checkIsGrantedPermission(context: Context, p1: String, p2: String) =
        checkIsGrantedPermission(context, p1) && checkIsGrantedPermission(context, p2)

fun checkNoGrantedPermission(context: Context, permission: String) =
        ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED

fun checkNoGrantedPermission(context: Context, p1: String, p2: String) =
        checkNoGrantedPermission(context, p1) && checkNoGrantedPermission(context, p2)

fun <T : Number> T?.notNull() = this?.let { it } ?: (0 as T)