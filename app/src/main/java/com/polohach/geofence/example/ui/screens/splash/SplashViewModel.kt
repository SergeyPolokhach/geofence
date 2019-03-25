package com.polohach.geofence.example.ui.screens.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.ui.base.BaseViewModel


class SplashViewModel(application: Application) : BaseViewModel(application) {

    val hasCurrentUser = MutableLiveData<Boolean>()

    fun checkCurrentUser() {
        hasCurrentUser.value =
                GFApp.firebaseUser != null
    }
}
