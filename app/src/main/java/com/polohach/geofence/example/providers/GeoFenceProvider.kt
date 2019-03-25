package com.polohach.geofence.example.providers

import com.polohach.geofence.example.models.GeoFence
import com.polohach.geofence.example.models.GeoFenceModel
import com.polohach.geofence.example.network.NetworkModule
import com.polohach.geofence.example.network.modules.GeoFenceModule
import com.polohach.geofence.example.providers.base.BaseOnlineProvider
import io.reactivex.Single


interface GeoFenceProvider {

    fun getGeoFences(): Single<List<GeoFence>>
}

object GeoFenceProviderImpl : BaseOnlineProvider<GeoFenceModel, GeoFenceModule>(), GeoFenceProvider {

    override fun initNetworkModule() = NetworkModule.getGeoFenceModule()

    override fun getGeoFences(): Single<List<GeoFence>> =
            networkModule.getGeoFences()
}
