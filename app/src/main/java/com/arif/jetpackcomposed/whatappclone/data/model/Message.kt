package com.arif.jetpackcomposed.whatappclone.data.model


data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isSeen: Boolean = false
)