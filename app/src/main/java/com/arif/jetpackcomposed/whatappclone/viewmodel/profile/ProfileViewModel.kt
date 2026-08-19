package com.arif.jetpackcomposed.whatappclone.viewmodel.profile

import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.model.User
import com.arif.jetpackcomposed.whatappclone.data.repository.UserRepository

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()

    fun saveProfile(user: User) {
        userRepository.saveUser(user)
    }
}