package com.polohach.geofence.example.providers

import com.polohach.geofence.example.models.User
import com.polohach.geofence.example.models.UserModel
import com.polohach.geofence.example.network.NetworkModule
import com.polohach.geofence.example.network.modules.UserModule
import com.polohach.geofence.example.providers.base.BaseOnlineProvider
import io.reactivex.Single


interface UserProvider {

    fun getUsers(): Single<List<User>>

    fun editUser(userUid: String, user: User): Single<User>

    fun saveUser(userUid: String, user: User): Single<User>
}

object UserProviderImpl : BaseOnlineProvider<UserModel, UserModule>(), UserProvider {

    override fun initNetworkModule() = NetworkModule.getUserModule()

    override fun getUsers(): Single<List<User>> =
            networkModule.getUsers()

    override fun editUser(userUid: String, user: User): Single<User> =
            networkModule.editUser(userUid, user)

    override fun saveUser(userUid: String, user: User): Single<User> =
            networkModule.saveUser(userUid, user)
}
