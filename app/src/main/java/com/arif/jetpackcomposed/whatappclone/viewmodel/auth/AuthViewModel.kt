package com.arif.jetpackcomposed.whatappclone.viewmodel.auth
import androidx.lifecycle.ViewModel
import com.arif.jetpackcomposed.whatappclone.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun logout() {
        repository.logout()
    }
}