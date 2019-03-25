package com.polohach.geofence.example.network

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.polohach.geofence.example.BuildConfig
import com.polohach.geofence.example.network.api.GeoFenceApi
import com.polohach.geofence.example.network.api.MessageApi
import com.polohach.geofence.example.network.api.UserApi
import com.polohach.geofence.example.network.modules.GeoFenceModuleImpl
import com.polohach.geofence.example.network.modules.MessageModuleImpl
import com.polohach.geofence.example.network.modules.UserModuleImpl
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val TIMEOUT_IN_SECONDS = 30L
    private const val REQUEST_TAG = "Request>>>>"
    private const val RESPONSE_TAG = "Response<<<<"
    private const val API_ENDPOINT = "https://geofence-247c2.firebaseio.com/"

    val mapper: ObjectMapper = ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(JodaModule())

    private val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .baseUrl(API_ENDPOINT)
            .client(createHttpClient())
            .build()

    private fun log() = LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .request(REQUEST_TAG)
            .response(RESPONSE_TAG)
            .build()

    private fun createHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            addInterceptor(log())
        }
    }.build()

    fun getUserModule() = UserModuleImpl(retrofit.create(UserApi::class.java))
    fun getGeoFenceModule() = GeoFenceModuleImpl(retrofit.create(GeoFenceApi::class.java))
    fun getMessageModule() = MessageModuleImpl(retrofit.create(MessageApi::class.java))
}
