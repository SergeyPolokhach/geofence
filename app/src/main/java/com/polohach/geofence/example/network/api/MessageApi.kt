package com.polohach.geofence.example.network.api

import com.polohach.geofence.example.firebase.DBContract
import com.polohach.geofence.example.network.bean.MessageBean
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path


interface MessageApi {
    @GET("${DBContract.CHATS_TABLE}/{chatId}.json")
    fun getMessages(@Path("chatId") chatId: String): Single<Map<String, MessageBean>>

    @PATCH("${DBContract.CHATS_TABLE}/{chatId}/{messageId}.json")
    fun sendMessage(@Path("chatId") chatId: String,
                    @Path("messageId") messageId: String,
                    @Body messageBean: MessageBean): Single<ResponseBody>
}
