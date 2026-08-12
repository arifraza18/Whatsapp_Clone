package com.arif.jetpackcomposed.whatappclone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.compose.runtime.LaunchedEffect



data class ChatData(
    val name: String,
    val message: String,
    val time: String,
    val unreadCount: Int
)
@Composable
fun HomePage() {

    val whatsappGreen = Color(0xFF075E54)
    var searchText by remember { mutableStateOf("") }
    var selectedChat by remember { mutableStateOf<String?>(null) }
    var chats by remember {
        mutableStateOf<List<ChatData>>(emptyList())
    }
    LaunchedEffect(Unit) {

        val database = FirebaseDatabase.getInstance(
            "https://whatappclon-be53d-default-rtdb.asia-southeast1.firebasedatabase.app"
        )

        database.reference
            .child("users")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val userList = mutableListOf<ChatData>()

                        for (userSnapshot in snapshot.children) {

                            val name =
                                userSnapshot.child("name")
                                    .getValue(String::class.java)
                                    ?: continue

                            userList.add(
                                ChatData(
                                    name = name,
                                    message = "Hello bro 👋",
                                    time = "",
                                    unreadCount = 0
                                )
                            )
                        }

                        chats = userList
                    }
                    override fun onCancelled(error: DatabaseError) {
                        println("FIREBASE ERROR: ${error.message}")
                    }
                }
            )
    }

    if (selectedChat != null) {

        ChatScreen(
            userName = selectedChat!!,
            onBack = {
                selectedChat = null
            }
        )

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        // ================= HEADER =================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(whatsappGreen)
                .padding(
                    start = 16.dp,
                    end = 8.dp,
                    top = 18.dp,
                    bottom = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Chating App",
                modifier = Modifier.weight(1f),
                fontSize = 25.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }

        // ================= TABS =================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(whatsappGreen),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "CHATS",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "UPDATES",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "CALLS",
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = {
                Text("Search chats")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(30.dp)
        )
        chats
            .filter {
                it.name.contains(
                    searchText,
                    ignoreCase = true
                )
            }
            .forEach { chat ->

                ChatItem(
                    name = chat.name,
                    message = chat.message,
                    time = chat.time,
                    unreadCount = chat.unreadCount,
                    onClick = {
                        selectedChat = chat.name
                    }
                )
            }

        }
    }



@Composable
fun ChatItem(
    name: String,
    message: String,
    time: String,
    unreadCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = name.first().toString(),
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .padding(17.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = time,
                fontSize = 12.sp,
                color = Color.Gray
            )

            if (unreadCount > 0) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = unreadCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366))
                        .padding(5.dp)
                )
            }
        }
    }
}