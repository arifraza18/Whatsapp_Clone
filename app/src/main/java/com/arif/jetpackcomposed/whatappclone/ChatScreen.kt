package com.arif.jetpackcomposed.whatappclone

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

data class ChatMessage(
    val text: String,
    val time: String,
)

@Composable
fun ChatScreen(
    userName: String,
    onBack: () -> Unit
) {
    var message by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profileImageUri = uri
        }
    }
    val scrollState = rememberScrollState()

    LaunchedEffect(messages.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    val database = remember {
        FirebaseDatabase.getInstance(
            "https://whatappclon-be53d-default-rtdb.asia-southeast1.firebasedatabase.app"
        )
    }
    LaunchedEffect(userName) {

        if (currentUserUid == null) return@LaunchedEffect

        database.reference
            .child("messages")
            .child(currentUserUid)
            .child(userName)
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        messages.clear()

                        for (messageSnapshot in snapshot.children) {

                            val text =
                                messageSnapshot.child("text")
                                    .getValue(String::class.java)
                                    ?: continue

                            val time =
                                messageSnapshot.child("time")
                                    .getValue(String::class.java)
                                    ?: ""

                            messages.add(
                                ChatMessage(
                                    text = text,
                                    time = time
                                )
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("MESSAGE ERROR: ${error.message}")
                    }
                }
            )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECE5DD))
    ) {

        // ================= HEADER =================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF075E54))
                .statusBarsPadding()
                .padding(
                    top = 1.dp,
                    bottom = 1.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.Gray,
                        CircleShape
                    )
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = userName,
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Color.White
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "Video Call",
                    tint = Color.White
                )
            }

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }


        // ================= MESSAGE AREA =================
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(12.dp)
        ) {


            // New sent messages
            messages.forEach { msg ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFD9FDD3),
                                RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = 12.dp,
                                    bottomEnd = 2.dp
                                )
                            )
                            .padding(
                                start = 10.dp,
                                top = 7.dp,
                                end = 8.dp,
                                bottom = 6.dp
                            )
                    ) {

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {

                            Text(
                                text = msg.text,
                                fontSize = 16.sp
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = msg.time,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )

                            Spacer(
                                modifier = Modifier.width(3.dp)
                            )

                            Text(
                                text = "✓✓",
                                fontSize = 10.sp,
                                color = Color(0xFF53BDEB)
                            )
                        }
                    }
                }
            }
        }
        // ================= MESSAGE INPUT =================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("message ")
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp)
            )

            Spacer(
                modifier = Modifier.width(1.dp)
            )

            IconButton(
                onClick = {

                    if (message.isNotBlank() && currentUserUid != null) {

                        val time = SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                        ).format(Date())

                        val messageData = hashMapOf(
                            "text" to message,
                            "time" to time
                        )

                        database.reference
                            .child("messages")
                            .child(currentUserUid)
                            .child(userName)
                            .push()
                            .setValue(messageData)

                        message = ""
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}