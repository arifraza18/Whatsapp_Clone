package com.arif.jetpackcomposed.whatappclone.viewmodel.message

import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.model.Message
import com.arif.jetpackcomposed.whatappclone.data.repository.MessageRepository

class MessageViewModel : ViewModel() {

    private val repository = MessageRepository()

    fun sendMessage(message: Message) {
        repository.saveMessage(message)
    }
}