package com.polohach.geofence.example.network.modules

import com.polohach.geofence.example.models.GeoFence
import com.polohach.geofence.example.network.NetworkErrorUtils
import com.polohach.geofence.example.network.api.GeoFenceApi
import com.polohach.geofence.example.network.bean.GeoFenceBean
import com.polohach.geofence.example.network.converters.GeoFenceBeanConverterImpl
import io.reactivex.Single


interface GeoFenceModule {

    fun getGeoFences(): Single<List<GeoFence>>
}

class GeoFenceModuleImpl(geoFenceApi: GeoFenceApi) :
        BaseRxModule<GeoFenceApi, GeoFenceBean, GeoFence>(geoFenceApi, GeoFenceBeanConverterImpl()), GeoFenceModule {

    override fun getGeoFences(): Single<List<GeoFence>> =
            api.getGeoFences()
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { mapGeoFenceBean ->
                        mutableListOf<GeoFenceBean>().apply {
                            mapGeoFenceBean.values.forEach {
                                add(it)
                            }
                        }
                    }
                    .map { converter.convertListInToOut(it) }
}
