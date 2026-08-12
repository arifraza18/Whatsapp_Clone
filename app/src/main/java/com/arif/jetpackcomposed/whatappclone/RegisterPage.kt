package com.arif.jetpackcomposed.whatappclone


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException

import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue


@Composable
fun RegisterPage() {
    val auth = remember { FirebaseAuth.getInstance() }
    val database = remember {
        FirebaseDatabase.getInstance(
            "https://whatappclon-be53d-default-rtdb.asia-southeast1.firebasedatabase.app"
        )
    }
    val context = LocalContext.current
    val activity = context as Activity


    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    var verificationId by remember { mutableStateOf<String?>(null) }
    var otpSent by remember { mutableStateOf(false) }
    val callbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(
                credential: PhoneAuthCredential
            ) {
                // Firebase automatically verification complete
            }

            override fun onVerificationFailed(
                e: FirebaseException
            ) {
                // OTP verification failed
            }

            override fun onCodeSent(
                verificationIdValue: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = verificationIdValue
                otpSent = true
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Chating App",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            color = Color(0xFF25D366)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = if (otpSent) "Verify your number" else "Create your account",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (!otpSent) {

            // Full Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Full Name")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Number
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        phone = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Phone Number")
                },
                placeholder = {
                    Text(" ")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))
            if (otpSent) {

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            otp = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Enter OTP")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {

                    if (name.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please enter your name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (phone.length != 10) {
                        Toast.makeText(
                            context,
                            "Enter a valid 10 digit number",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val fullPhoneNumber = "+91$phone"

                    val callbacks =
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            override fun onVerificationCompleted(
                                credential: com.google.firebase.auth.PhoneAuthCredential
                            ) {
                                auth.signInWithCredential(credential)
                                    .addOnCompleteListener { task ->

                                        if (task.isSuccessful) {

                                            val uid = auth.currentUser?.uid

                                            if (uid != null) {

                                                val userData = hashMapOf<String, Any>(
                                                    "name" to name,
                                                    "phone" to fullPhoneNumber,
                                                    "createdAt" to ServerValue.TIMESTAMP
                                                )

                                                database.reference
                                                    .child("users")
                                                    .child(uid)
                                                    .setValue(userData)
                                                    .addOnSuccessListener {

                                                        Toast.makeText(
                                                            context,
                                                            "Phone verified & profile saved!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    .addOnFailureListener { error ->

                                                        Toast.makeText(
                                                            context,
                                                            "Database error: ${error.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                            }
                                        } else {

                                            Toast.makeText(
                                                context,
                                                task.exception?.message ?: "Verification failed",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                Toast.makeText(
                                    context,
                                    e.message ?: "Verification failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            override fun onCodeSent(
                                verificationIdFromFirebase: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                verificationId = verificationIdFromFirebase
                                otpSent = true

                                Toast.makeText(
                                    context,
                                    "OTP sent successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(fullPhoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(callbacks)
                        .build()

                    PhoneAuthProvider.verifyPhoneNumber(options)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366)
                )
            ) {
                Text(
                    text = "Send OTP",
                    fontSize = 16.sp
                )
            }
            if (otpSent) {

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            otp = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Enter OTP")
                    },
                    placeholder = {
                        Text("6 digit OTP")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Verify button clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                        val id = verificationId

                        if (id == null) {
                            Toast.makeText(
                                context,
                                "Please request OTP first",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@Button
                        }

                        if (otp.length != 6) {
                            Toast.makeText(
                                context,
                                "  ",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@Button
                        }

                        val credential = PhoneAuthProvider.getCredential(
                            id,
                            otp
                        )

                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        context,
                                        "Phone verified successfully!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                } else {

                                    Toast.makeText(
                                        context,
                                        task.exception?.message
                                            ?: "Invalid OTP",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        // Verify OTP next step mein
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366)
                    )
                ) {
                    Text(
                        text = "Verify OTP",
                        fontSize = 16.sp
                    )
                }
            }

        } else {

            // OTP
            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        otp = it
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Enter OTP")
                },
                placeholder = {
                    Text("  ")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {

                    if (otp.length != 6) {
                        Toast.makeText(
                            context,
                            "Enter 6 digit OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val id = verificationId

                    if (id != null) {

                        val credential =
                            PhoneAuthProvider.getCredential(id, otp)

                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {

                                    val uid = auth.currentUser?.uid

                                    if (uid != null) {

                                        val userData = hashMapOf<String, Any>(
                                            "name" to name,
                                            "phone" to "+91$phone",
                                            "createdAt" to ServerValue.TIMESTAMP
                                        )

                                        database.reference
                                            .child("users")
                                            .child(uid)
                                            .setValue(userData)
                                            .addOnSuccessListener {

                                                Toast.makeText(
                                                    context,
                                                    "Account created successfully!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            .addOnFailureListener { error ->

                                                Toast.makeText(
                                                    context,
                                                    "Database error: ${error.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }

                                } else {

                                    Toast.makeText(
                                        context,
                                        task.exception?.message
                                            ?: "Invalid OTP",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366)
                )
            ) {
                Text(
                    text = "Verify OTP",
                    fontSize = 16.sp
                )
            }
        }
    }
}