package com.arif.jetpackcomposed.whatappclone.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val about: String = "",
    val createdAt: Long = 0L
)