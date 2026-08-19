package com.arif.jetpackcomposed.whatappclone.data.repository

import com.arif.jetpackcomposed.whatappclone.data.model.Chat
import com.arif.jetpackcomposed.whatappclone.data.model.Message
import com.google.firebase.database.*

class ChatRepository {

    private val database = FirebaseDatabase.getInstance(
        "https://whatappclon-be53d-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    private val chatsRef = database.getReference("chats")
    private val messagesRef = database.getReference("messages")


    // ================= SAVE CHAT =================

    fun saveChat(chat: Chat) {

        chatsRef
            .child(chat.chatId)
            .setValue(chat)
    }


    // ================= GET CHATS =================

    fun getChats(
        currentUserUid: String,
        onSuccess: (List<Chat>) -> Unit,
        onError: (String) -> Unit
    ) {

        chatsRef
            .child(currentUserUid)
            .get()
            .addOnSuccessListener { snapshot ->

                val chats = mutableListOf<Chat>()

                for (chatSnapshot in snapshot.children) {

                    val chat =
                        chatSnapshot.getValue(Chat::class.java)

                    if (chat != null) {
                        chats.add(chat)
                    }
                }

                onSuccess(chats)
            }
            .addOnFailureListener { error ->

                onError(
                    error.message
                        ?: "Failed to load chats"
                )
            }

    }


    // ================= GET NEW MESSAGE ID =================

    fun getNewMessageId(
        currentUserUid: String,
        receiverId: String
    ): String {

        return messagesRef
            .child(currentUserUid)
            .child(receiverId)
            .push()
            .key
            ?: System.currentTimeMillis().toString()
    }


    // ================= REAL-TIME MESSAGE LISTENER =================

    fun listenMessages(
        currentUserUid: String,
        receiverId: String,
        onMessagesChanged: (List<Message>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener {

        val messageReference = messagesRef
            .child(currentUserUid)
            .child(receiverId)

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val messages = mutableListOf<Message>()

                for (messageSnapshot in snapshot.children) {

                    val message =
                        messageSnapshot.getValue(Message::class.java)

                    if (message != null) {
                        messages.add(message)
                    }
                }

                messages.sortBy {
                    it.timestamp
                }

                onMessagesChanged(messages)
            }

            override fun onCancelled(error: DatabaseError) {

                onError(
                    error.message
                        ?: "Failed to load messages"
                )
            }
        }

        messageReference.addValueEventListener(listener)

        return listener
    }


    // ================= REMOVE MESSAGE LISTENER =================

    fun removeMessageListener(
        currentUserUid: String,
        receiverId: String,
        listener: ValueEventListener
    ) {

        messagesRef
            .child(currentUserUid)
            .child(receiverId)
            .removeEventListener(listener)
    }


    // ================= SAVE MESSAGE =================

    fun saveMessage(
        currentUserUid: String,
        receiverId: String,
        message: Message,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val messageRef = messagesRef
            .child(currentUserUid)
            .child(receiverId)
            .child(message.messageId)

        messageRef
            .setValue(message)
            .addOnSuccessListener {

                updateChat(
                    currentUserUid = currentUserUid,
                    receiverId = receiverId,
                    lastMessage = message.message,
                    lastMessageTime = message.timestamp,
                    onSuccess = onSuccess,
                    onError = onError
                )
            }
            .addOnFailureListener { error ->

                onError(
                    error.message
                        ?: "Message sending failed"
                )
            }
    }


    // ================= UPDATE CHAT =================

    private fun updateChat(
        currentUserUid: String,
        receiverId: String,
        lastMessage: String,
        lastMessageTime: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val senderChat = Chat(
            chatId = "${currentUserUid}_${receiverId}",
            userId = receiverId,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            unreadCount = 0
        )

        val receiverChat = Chat(
            chatId = "${receiverId}_${currentUserUid}",
            userId = currentUserUid,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            unreadCount = 1
        )

        val updates = hashMapOf<String, Any>(
            "$currentUserUid/$receiverId" to senderChat,
            "$receiverId/$currentUserUid" to receiverChat
        )

        chatsRef
            .updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onError(
                    error.message ?: "Chat update failed"
                )
            }


        val chatId =
            "${currentUserUid}_${receiverId}"

        val chat = Chat(
            chatId = chatId,
            userId = receiverId,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            unreadCount = 0
        )

        chatsRef
            .child(chatId)
            .setValue(chat)
            .addOnSuccessListener {

                onSuccess()
            }
            .addOnFailureListener { error ->

                onError(
                    error.message
                        ?: "Chat update failed"
                )
            }
    }
}