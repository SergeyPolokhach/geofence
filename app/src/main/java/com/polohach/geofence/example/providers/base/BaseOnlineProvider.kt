package com.polohach.geofence.example.providers.base

import com.polohach.geofence.example.models.Model


abstract class BaseOnlineProvider<M : Model, NetworkModule> : Provider<M> {

    val networkModule: NetworkModule = this.initNetworkModule()

    protected abstract fun initNetworkModule(): NetworkModule
}
