package com.arif.jetpackcomposed.whatappclone.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CallRepository {

    private val database = FirebaseDatabase.getInstance(
        "https://whatappclon-be53d-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    private val callsRef = database.getReference("calls")

    fun createCall(
        callerId: String,
        receiverId: String,
        callType: String
    ) {

        val callId = callsRef.push().key ?: return

        val callData = mapOf(
            "callId" to callId,
            "callerId" to callerId,
            "receiverId" to receiverId,
            "callType" to callType,
            "status" to "ringing",
            "timestamp" to System.currentTimeMillis()
        )

        callsRef
            .child(callId)
            .setValue(callData)
    }

    fun listenIncomingCalls(
        currentUserUid: String,
        onIncomingCall: (Map<String, Any>) -> Unit
    ) {

        callsRef
            .orderByChild("receiverId")
            .equalTo(currentUserUid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (callSnapshot in snapshot.children) {

                        val status =
                            callSnapshot
                                .child("status")
                                .getValue(String::class.java)

                        if (status == "ringing") {

                            val callData =
                                callSnapshot.value as? Map<String, Any>

                            if (callData != null) {
                                onIncomingCall(callData)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            )
    }

    fun updateCallStatus(
        callId: String,
        status: String
    ) {
        callsRef
            .child(callId)
            .child("status")
            .setValue(status)
    }
}