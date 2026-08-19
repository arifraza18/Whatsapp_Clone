package com.arif.jetpackcomposed.whatappclone.data.model

data class Chat(
    val chatId: String = "",
    val userId: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val unreadCount: Int = 0
)