package com.arif.jetpackcomposed.whatappclone.data.repository

import com.arif.jetpackcomposed.whatappclone.data.model.User
import com.google.firebase.database.FirebaseDatabase

class UserRepository {

    private val database = FirebaseDatabase.getInstance(
        "https://whatappclon-be53d-default-rtdb.asia-southeast1.firebasedatabase.app"
    )
    private val usersRef = database.getReference("users")

    fun saveUser(user: User) {
        usersRef.child(user.uid).setValue(user)
    }

    fun getAllUsers(
        onSuccess: (List<User>) -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.get()
            .addOnSuccessListener { snapshot ->

                val users = mutableListOf<User>()

                for (userSnapshot in snapshot.children) {
                    val user = User(
                        uid = userSnapshot.key ?: "",
                        name = userSnapshot.child("name").value?.toString() ?: "",
                        phone = userSnapshot.child("phone").value?.toString() ?: "",
                        profileImage = userSnapshot.child("profileImage").value?.toString() ?: "",
                        about = userSnapshot.child("about").value?.toString()
                            ?: "Hey there! I am using WhatsApp.",
                        createdAt = userSnapshot.child("createdAt").value as? Long ?: 0L
                    )

                    users.add(user)
                }

                onSuccess(users)
            }
            .addOnFailureListener { error ->
                onError(error.message ?: "Failed to load users")
            }
    }
}