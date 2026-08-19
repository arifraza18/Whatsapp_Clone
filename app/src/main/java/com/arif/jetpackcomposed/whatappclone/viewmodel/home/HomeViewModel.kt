package com.arif.jetpackcomposed.whatappclone.viewmodel.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.model.Chat
import com.arif.jetpackcomposed.whatappclone.data.model.User
import com.arif.jetpackcomposed.whatappclone.data.repository.ChatRepository
import com.arif.jetpackcomposed.whatappclone.data.repository.UserRepository

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val chatRepository = ChatRepository()

    val users = mutableStateOf<List<User>>(emptyList())
    val chats = mutableStateOf<List<Chat>>(emptyList())

    val errorMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    fun loadUsers() {

        isLoading.value = true
        errorMessage.value = ""

        userRepository.getAllUsers(
            onSuccess = { userList ->
                users.value = userList
                isLoading.value = false
            },
            onError = { error ->
                errorMessage.value = error
                isLoading.value = false
            }
        )
    }

    fun loadChats(currentUserUid: String) {

        chatRepository.getChats(
            currentUserUid = currentUserUid,
            onSuccess = { chatList ->
                chats.value = chatList
            },
            onError = { error ->
                errorMessage.value = error
            }
        )
    }
}