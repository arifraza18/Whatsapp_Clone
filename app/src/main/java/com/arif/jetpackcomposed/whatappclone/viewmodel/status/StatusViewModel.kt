package com.arif.jetpackcomposed.whatappclone.viewmodel.status

import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.model.Status
import com.arif.jetpackcomposed.whatappclone.data.repository.StatusRepository

class StatusViewModel : ViewModel() {

    private val repository = StatusRepository()

    fun saveStatus(status: Status) {
        repository.saveStatus(status)
    }
}