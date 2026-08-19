package com.arif.jetpackcomposed

import HomePage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arif.jetpackcomposed.whatappclone.ui.auth.RegisterPage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            var showHomePage by remember {
                mutableStateOf(false)
            }

            if (showHomePage) {

                HomePage()

            } else {

                RegisterPage(
                    onRegisterSuccess = {
                        showHomePage = true
                    }
                )
            }
        }
    }
}