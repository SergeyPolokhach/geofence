package com.polohach.geofence.example.ui.screens.main.chat

import android.app.Application
import com.polohach.geofence.example.firebase.repositories.MessageRepository
import com.polohach.geofence.example.models.ActionType
import com.polohach.geofence.example.ui.base.BaseViewModel

class ChatViewModel(application: Application) : BaseViewModel(application) {

    private val messageRepository = MessageRepository()

    fun getMessageLD(actionType: ActionType) =
            messageRepository.getMessageLD(actionType)
}
