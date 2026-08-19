package com.arif.jetpackcomposed.whatappclone.data.repository

import com.arif.jetpackcomposed.whatappclone.data.model.Message
import com.google.firebase.database.FirebaseDatabase

class MessageRepository {

    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("messages")

    fun saveMessage(message: Message) {
        messagesRef.child(message.messageId).setValue(message)
    }
}