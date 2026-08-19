package com.arif.jetpackcomposed.whatappclone.viewmodel.call

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.repository.CallRepository

class CallViewModel : ViewModel() {

    private val repository = CallRepository()

    val incomingCall = mutableStateOf<Map<String, Any>?>(null)

    fun startVoiceCall(
        callerId: String,
        receiverId: String
    ) {
        repository.createCall(
            callerId = callerId,
            receiverId = receiverId,
            callType = "voice"
        )
    }

    fun startVideoCall(
        callerId: String,
        receiverId: String
    ) {
        repository.createCall(
            callerId = callerId,
            receiverId = receiverId,
            callType = "video"
        )
    }

    fun listenForIncomingCalls(
        currentUserUid: String
    ) {
        repository.listenIncomingCalls(
            currentUserUid = currentUserUid,
            onIncomingCall = { callData ->
                incomingCall.value = callData
            }
        )
    }

    fun acceptCall() {
        val callId = incomingCall.value?.get("callId") as? String

        if (callId != null) {
            repository.updateCallStatus(
                callId = callId,
                status = "accepted"
            )
        }
    }

    fun rejectCall() {
        val callId = incomingCall.value?.get("callId") as? String

        if (callId != null) {
            repository.updateCallStatus(
                callId = callId,
                status = "rejected"
            )
        }

        incomingCall.value = null
    }

    fun clearIncomingCall() {
        incomingCall.value = null
    }
}