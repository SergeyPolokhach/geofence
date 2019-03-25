package com.polohach.geofence.example.firebase.repositories

import com.google.firebase.database.FirebaseDatabase
import com.polohach.geofence.example.firebase.DBContract
import com.polohach.geofence.example.firebase.repositories.base.BaseRepository
import com.polohach.geofence.example.models.ActionType
import com.polohach.geofence.example.models.UserModel

class UsersRepository : BaseRepository<UserModel>(UserModel::class.java) {

    override val reference = FirebaseDatabase.getInstance()
            .reference
            .child(DBContract.USERS_TABLE)

    fun getUsersLD(actionType: ActionType) = listLiveData
            .also { if (actionType == ActionType.SUBSCRIBE) onStart() else onStop() }
}
