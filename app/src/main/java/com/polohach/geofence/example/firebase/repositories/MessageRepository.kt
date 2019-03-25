package com.polohach.geofence.example.firebase.repositories

import com.google.firebase.database.FirebaseDatabase
import com.polohach.geofence.example.EMPTY_STRING
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.firebase.DBContract
import com.polohach.geofence.example.firebase.repositories.base.BaseRepository
import com.polohach.geofence.example.models.ActionType
import com.polohach.geofence.example.models.MessageModel

class MessageRepository : BaseRepository<MessageModel>(MessageModel::class.java) {

    override val reference = FirebaseDatabase.getInstance()
            .reference
            .child(DBContract.CHATS_TABLE)
            .child(GFApp.firebaseUser?.uid ?: EMPTY_STRING)

    fun getMessageLD(actionType: ActionType) = listLiveData
            .also { if (actionType == ActionType.SUBSCRIBE) onStart() else onStop() }
}
