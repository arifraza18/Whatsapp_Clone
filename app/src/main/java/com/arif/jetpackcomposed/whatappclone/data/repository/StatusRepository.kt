package com.arif.jetpackcomposed.whatappclone.data.repository

import com.arif.jetpackcomposed.whatappclone.data.model.Status
import com.google.firebase.database.FirebaseDatabase

class StatusRepository {

    private val database = FirebaseDatabase.getInstance()
    private val statusRef = database.getReference("status")

    fun saveStatus(status: Status) {
        statusRef.child(status.statusId).setValue(status)
    }
}