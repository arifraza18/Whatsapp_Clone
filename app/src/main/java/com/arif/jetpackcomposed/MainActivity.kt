package com.arif.jetpackcomposed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arif.jetpackcomposed.whatappclone.ChatScreen
import com.arif.jetpackcomposed.whatappclone.HomePage
import com.arif.jetpackcomposed.whatappclone.RegisterPage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
//            RegisterPage()
                       HomePage()
//            ChatScreen() { }
//                ChatScreen(
//                    userName = "Rahul"
//                )
            }
        }
    }
