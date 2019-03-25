package com.polohach.geofence.example.network.converters

import com.polohach.geofence.example.models.GeoFence
import com.polohach.geofence.example.models.GeoFenceModel
import com.polohach.geofence.example.models.converters.BaseConverter
import com.polohach.geofence.example.network.bean.GeoFenceBean


interface GeoFenceBeanConverter

class GeoFenceBeanConverterImpl : BaseConverter<GeoFenceBean, GeoFence>(), GeoFenceBeanConverter {

    override fun processConvertInToOut(inObject: GeoFenceBean) = inObject.run {
        GeoFenceModel(id, latitude, longitude, radius)
    }

    override fun processConvertOutToIn(outObject: GeoFence) = outObject.run {
        GeoFenceBean(id, latitude, longitude, radius)
    }
}
