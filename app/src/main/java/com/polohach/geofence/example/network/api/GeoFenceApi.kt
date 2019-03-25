package com.polohach.geofence.example.network.api

import com.polohach.geofence.example.firebase.DBContract
import com.polohach.geofence.example.network.bean.GeoFenceBean
import io.reactivex.Single
import retrofit2.http.GET


interface GeoFenceApi {
    @GET("${DBContract.GEOFENCE_TABLE}.json")
    fun getGeoFences(): Single<Map<String, GeoFenceBean>>
}
