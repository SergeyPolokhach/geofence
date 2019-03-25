package com.polohach.geofence.example.network.modules

import com.polohach.geofence.example.models.User
import com.polohach.geofence.example.network.NetworkErrorUtils
import com.polohach.geofence.example.network.api.UserApi
import com.polohach.geofence.example.network.bean.UserBean
import com.polohach.geofence.example.network.converters.UserBeanConverterImpl
import io.reactivex.Single


interface UserModule {

    fun getUsers(): Single<List<User>>

    fun editUser(userUid: String, user: User): Single<User>

    fun saveUser(userUid: String, user: User): Single<User>
}

class UserModuleImpl(userApi: UserApi) :
        BaseRxModule<UserApi, UserBean, User>(userApi, UserBeanConverterImpl()), UserModule {

    override fun getUsers(): Single<List<User>> =
            api.getUsers()
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { mapUserBean ->
                        mutableListOf<UserBean>().apply {
                            mapUserBean.values.forEach {
                                add(it)
                            }
                        }
                    }
                    .map { converter.convertListInToOut(it) }

    override fun editUser(userUid: String, user: User): Single<User> =
            Single.just(user)
                    .map { converter.convertOutToIn(it) }
                    .flatMap { api.editUser(userUid, it) }
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { _ -> user }


    override fun saveUser(userUid: String, user: User): Single<User> =
            Single.just(user)
                    .map { converter.convertOutToIn(it) }
                    .flatMap { api.saveUser(userUid, it) }
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { converter.convertInToOut(it) }
}
