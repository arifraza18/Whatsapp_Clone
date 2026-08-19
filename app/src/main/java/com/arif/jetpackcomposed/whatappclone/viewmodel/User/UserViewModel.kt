package com.arif.jetpackcomposed.whatappclone.viewmodel.User

import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.model.User
import com.arif.jetpackcomposed.whatappclone.data.repository.UserRepository

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    fun saveUser(user: User) {
        repository.saveUser(user)
    }
}