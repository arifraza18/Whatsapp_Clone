package com.arif.jetpackcomposed.whatappclone.viewmodel.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.model.Message
import com.arif.jetpackcomposed.whatappclone.data.repository.ChatRepository
import com.google.firebase.database.ValueEventListener

class ChatViewModel : ViewModel() {

    private val repository = ChatRepository()

    val errorMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    val messages = mutableStateOf<List<Message>>(emptyList())

    private var messageListener: ValueEventListener? = null

    // ================= SEND MESSAGE =================

    fun sendMessage(
        currentUserUid: String,
        receiverId: String,
        messageText: String
    ) {

        if (messageText.isBlank()) {
            return
        }

        val messageId = repository.getNewMessageId(
            currentUserUid = currentUserUid,
            receiverId = receiverId
        )

        val message = Message(
            messageId = messageId,
            senderId = currentUserUid,
            receiverId = receiverId,
            message = messageText,
            timestamp = System.currentTimeMillis(),
            isSeen = false
        )

        isLoading.value = true
        errorMessage.value = ""

        repository.saveMessage(
            currentUserUid = currentUserUid,
            receiverId = receiverId,
            message = message,

            onSuccess = {
                isLoading.value = false
            },

            onError = { error ->
                isLoading.value = false
                errorMessage.value = error
            }
        )
    }


    // ================= REAL-TIME LOAD =================

    fun loadMessages(
        currentUserUid: String,
        receiverId: String
    ) {

        errorMessage.value = ""

        messageListener?.let {
            repository.removeMessageListener(
                currentUserUid = currentUserUid,
                receiverId = receiverId,
                listener = it
            )
        }

        messageListener = repository.listenMessages(
            currentUserUid = currentUserUid,
            receiverId = receiverId,

            onMessagesChanged = { messageList ->
                messages.value = messageList
            },

            onError = { error ->
                errorMessage.value = error
            }
        )
    }


    // ================= STOP LISTENER =================

    fun stopListening(
        currentUserUid: String,
        receiverId: String
    ) {

        messageListener?.let {

            repository.removeMessageListener(
                currentUserUid = currentUserUid,
                receiverId = receiverId,
                listener = it
            )

            messageListener = null
        }
    }
}