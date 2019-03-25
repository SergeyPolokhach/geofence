package com.polohach.geofence.example.providers

import com.polohach.geofence.example.models.Message
import com.polohach.geofence.example.models.MessageModel
import com.polohach.geofence.example.network.NetworkModule
import com.polohach.geofence.example.network.modules.MessageModule
import com.polohach.geofence.example.providers.base.BaseOnlineProvider
import io.reactivex.Single


interface MessageProvider {

    fun getMessages(chatId: String): Single<List<Message>>

    fun sendMessage(chatId: String, messageId: String, message: Message): Single<Message>
}

object MessageProviderImpl : BaseOnlineProvider<MessageModel, MessageModule>(), MessageProvider {

    override fun initNetworkModule() = NetworkModule.getMessageModule()

    override fun getMessages(chatId: String): Single<List<Message>> =
            networkModule.getMessages(chatId)

    override fun sendMessage(chatId: String, messageId: String, message: Message): Single<Message> =
            networkModule.sendMessage(chatId, messageId, message)
}
