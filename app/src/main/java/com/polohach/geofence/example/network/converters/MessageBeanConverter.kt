package com.polohach.geofence.example.network.converters

import com.polohach.geofence.example.models.Message
import com.polohach.geofence.example.models.MessageModel
import com.polohach.geofence.example.models.converters.BaseConverter
import com.polohach.geofence.example.network.bean.MessageBean


interface MessageBeanConverter

class MessageBeanConverterImpl : BaseConverter<MessageBean, Message>(), MessageBeanConverter {

    override fun processConvertInToOut(inObject: MessageBean) = inObject.run {
        MessageModel(email, latitude, longitude, time, transition)
    }

    override fun processConvertOutToIn(outObject: Message) = outObject.run {
        MessageBean(email, latitude, longitude, time, transition, id)
    }
}
