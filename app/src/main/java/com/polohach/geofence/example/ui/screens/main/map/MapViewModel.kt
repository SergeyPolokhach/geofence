package com.polohach.geofence.example.ui.screens.main.map

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.polohach.geofence.example.firebase.repositories.UsersRepository
import com.polohach.geofence.example.models.ActionType
import com.polohach.geofence.example.models.GeoFence
import com.polohach.geofence.example.models.User
import com.polohach.geofence.example.providers.Providers
import com.polohach.geofence.example.ui.base.BaseViewModel
import io.reactivex.functions.Consumer

class MapViewModel(application: Application) : BaseViewModel(application) {

    val usersLiveData = MutableLiveData<List<User>>()
    val geoFenceLiveData = MutableLiveData<List<GeoFence>>()

    private val userRepository = UsersRepository()

    fun getGeoFence() {
        Providers.geoFence.getGeoFences()
                .doAsync(Consumer { geoFenceLiveData.value = it })
    }

    fun getUsers(actionType: ActionType) =
            userRepository.getUsersLD(actionType)
}
