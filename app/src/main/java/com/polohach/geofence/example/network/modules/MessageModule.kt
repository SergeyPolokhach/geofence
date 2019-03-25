package com.polohach.geofence.example.network.modules

import com.polohach.geofence.example.models.Message
import com.polohach.geofence.example.network.NetworkErrorUtils
import com.polohach.geofence.example.network.api.MessageApi
import com.polohach.geofence.example.network.bean.MessageBean
import com.polohach.geofence.example.network.converters.MessageBeanConverterImpl
import io.reactivex.Single


interface MessageModule {

    fun getMessages(chatId: String): Single<List<Message>>

    fun sendMessage(chatId: String, messageId: String, message: Message): Single<Message>
}

class MessageModuleImpl(messageApi: MessageApi) :
        BaseRxModule<MessageApi, MessageBean, Message>(messageApi, MessageBeanConverterImpl()), MessageModule {

    override fun getMessages(chatId: String): Single<List<Message>> =
            api.getMessages(chatId)
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { mapMessageBean ->
                        mutableListOf<MessageBean>().apply {
                            mapMessageBean.values.forEach {
                                add(it)
                            }
                        }
                    }
                    .map { converter.convertListInToOut(it) }

    override fun sendMessage(chatId: String, messageId: String, message: Message): Single<Message> =
            Single.just(message)
                    .map { converter.convertOutToIn(it) }
                    .flatMap { api.sendMessage(chatId, messageId, it) }
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { _ -> message }
}
