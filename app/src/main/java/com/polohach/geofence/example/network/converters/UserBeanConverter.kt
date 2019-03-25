package com.polohach.geofence.example.network.converters

import com.polohach.geofence.example.models.User
import com.polohach.geofence.example.models.UserModel
import com.polohach.geofence.example.models.converters.BaseConverter
import com.polohach.geofence.example.network.bean.UserBean


interface UserBeanConverter

class UserBeanConverterImpl : BaseConverter<UserBean, User>(), UserBeanConverter {

    override fun processConvertInToOut(inObject: UserBean) = inObject.run {
        UserModel(id, latitude, longitude, accuracy)
    }

    override fun processConvertOutToIn(outObject: User) = outObject.run {
        UserBean(id, latitude, longitude, accuracy)
    }
}
