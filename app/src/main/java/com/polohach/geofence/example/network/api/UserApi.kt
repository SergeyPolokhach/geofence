package com.polohach.geofence.example.network.api

import com.polohach.geofence.example.firebase.DBContract
import com.polohach.geofence.example.network.bean.UserBean
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*


interface UserApi {
    @GET("${DBContract.USERS_TABLE}.json")
    fun getUsers(): Single<Map<String, UserBean>>

    @PATCH("${DBContract.USERS_TABLE}/{userId}.json")
    fun editUser(@Path("userId") userId: String, @Body userBean: UserBean): Single<ResponseBody>

    @PUT("${DBContract.USERS_TABLE}/{userId}.json")
    fun saveUser(@Path("userId") userId: String, @Body userBeanMap: UserBean): Single<UserBean>
}
