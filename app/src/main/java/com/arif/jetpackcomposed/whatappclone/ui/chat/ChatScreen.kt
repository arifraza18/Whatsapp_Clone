package com.arif.jetpackcomposed.whatappclone.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arif.jetpackcomposed.whatappclone.viewmodel.chat.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arif.jetpackcomposed.whatappclone.viewmodel.call.CallViewModel



@Composable
fun ChatScreen(
    userName: String,
    receiverId: String,
    onBack: () -> Unit
) {

    val chatViewModel: ChatViewModel = viewModel()
    val callViewModel: CallViewModel = viewModel()
    val incomingCall by callViewModel.incomingCall



    val messages by chatViewModel.messages

    var message by remember { mutableStateOf("") }





    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
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




    LaunchedEffect(currentUserUid, receiverId) {

        if (currentUserUid != null) {

            chatViewModel.loadMessages(
                currentUserUid = currentUserUid,
                receiverId = receiverId
            )
        }
    }
    LaunchedEffect(currentUserUid) {

        if (currentUserUid != null) {
            callViewModel.listenForIncomingCalls(
                currentUserUid = currentUserUid
            )
        }
    }
    if (incomingCall != null) {

        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("Incoming Call")
            },
            text = {
                Text(
                    if (incomingCall?.get("callType") == "video") {
                        "Incoming video call"
                    } else {
                        "Incoming voice call"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        callViewModel.acceptCall()
                    }
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        callViewModel.rejectCall()
                    }
                ) {
                    Text("Reject")
                }
            }
        )
    }
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(Color(0xFFECE5DD))
    ) {

        // ================= HEADER =================

        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .background(Color(0xFF075E54))
                .statusBarsPadding()
                .padding(
                    top = 1.dp,
                    bottom = 1.dp
                ),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {

            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Companion.White
                )
            }
            Box(
                modifier = Modifier.Companion
                    .size(40.dp)
                    .background(
                        Color.Companion.Gray,
                        CircleShape
                    )
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Companion.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.Companion
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Companion.Crop
                    )
                } else {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "?",
                        color = Color.Companion.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Companion.Bold
                    )
                }
                Spacer(modifier = Modifier.Companion.width(8.dp))
            }
            Text(
                text = userName,
                modifier = Modifier.Companion.weight(1f),
                color = Color.Companion.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Companion.Bold
            )

            IconButton(
                onClick = {
                    if (currentUserUid != null) {
                        callViewModel.startVoiceCall(
                            callerId = currentUserUid,
                            receiverId = receiverId
                        )
                    }
                }
            )

            {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Color.White
                )


            }

            IconButton(
                onClick = {
                    if (currentUserUid != null) {
                        callViewModel.startVideoCall(
                            callerId = currentUserUid,
                            receiverId = receiverId
                        )
                    }
                }
            ) {
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
                    tint = Color.Companion.White
                )
            }
        }


        // ================= MESSAGE AREA =================
        Column(
            modifier = Modifier.Companion
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(12.dp)
        ) {


            // New sent messages
            messages.forEach { msg ->

                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    Box(
                        modifier = Modifier.Companion
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
                            verticalAlignment = Alignment.Companion.Bottom
                        ) {
                            Text(
                                text = msg.message,
                                fontSize = 16.sp
                            )

                            Spacer(
                                modifier = Modifier.Companion.width(8.dp)
                            )

                            Text(
                                text = SimpleDateFormat(
                                    "hh:mm a",
                                    Locale.getDefault()
                                ).format(Date(msg.timestamp)),
                                fontSize = 10.sp,
                                color = Color.Companion.Gray
                            )

                            Spacer(
                                modifier = Modifier.Companion.width(3.dp)
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
            modifier = Modifier.Companion
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(
                    horizontal = 8.dp,
                    vertical = 8.dp
                ),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {

            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                modifier = Modifier.Companion.weight(1f),
                placeholder = {
                    Text("message ")
                },
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp)
            )

            Spacer(
                modifier = Modifier.Companion.width(1.dp)
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

                        chatViewModel.sendMessage(
                            currentUserUid = currentUserUid,
                            receiverId = receiverId,
                            messageText = message
                        )
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